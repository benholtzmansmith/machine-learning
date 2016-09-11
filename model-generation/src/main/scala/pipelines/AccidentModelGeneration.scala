package pipelines

import com.mongodb.DBObject
import com.mongodb.hadoop.MongoInputFormat
import com.mongodb.hadoop.util.MongoConfigUtil
import features.{ ExtractLabel, FeatureGenerators }
import features.FeatureGenerators.featureGenerators
import machineLearning.data.models.Accident
import org.apache.hadoop.conf.Configuration
import org.apache.log4j.Logger
import org.apache.spark.mllib.classification.{ LogisticRegressionWithLBFGS, SVMWithSGD }
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.mllib.regression.{ GeneralizedLinearModel, LabeledPoint }
import org.apache.spark.rdd.RDD
import org.apache.spark.{ SparkConf, SparkContext }
import org.bson.BSONObject
import org.bson.types.ObjectId
import org.scalatest.TestData
import play.api.libs.json.{ JsError, JsSuccess }
import utils.JsonSerialization

/**
 *
 * Spark job to analyze new york accident data.
 *
 * To run this:
 * (1) curl https://nycopendata.socrata.com/api/views/h9gi-nx95/rows.csv >> accident.csv
 * (2) mongo import csv to local mongo instance
 * (3) rename fields to match the case class names
 * (3) Run job :-)
 *
 * Current precision of model: 98%
 * Recall is probably pretty shit but haven't checked.
 *
 */

object AccidentModelGeneration {

  val logger = Logger.getLogger(getClass)

  def main(args: Array[String]): Unit = {

    println("Starting Accident Data Model Learning ")

    val sparkConf = new SparkConf().setMaster("local").setAppName("accident-machine-learning")

    val sc = new SparkContext(sparkConf)

    val config = new Configuration

    //Hard coded for now to just point to a local mongo instance
    MongoConfigUtil.setInputURI(config, "mongodb://localhost:27017/accidents.accidents")

    val features = loadData(sc, config)

    val (trainData, testData) = (features(0), features(1))

    val logisticRegressionWithLBFGS = new LogisticRegressionWithLBFGS

    val logisticRegressionModel = logisticRegressionWithLBFGS.run(trainData)

    val libSVM = SVMWithSGD.train(trainData, 100)

    val testDataCount = testData.count()

    val libSVMperformance = testPerformance(libSVM, testData, testDataCount)

    val logisticRegressionPerformance = testPerformance(logisticRegressionModel, testData, testDataCount)

    println(
      s"""
         |Lib svm precision: ${libSVMperformance.precision}
         |Lib svm recall: ${libSVMperformance.recall}
         |
         |Logistic regression precision: ${logisticRegressionPerformance.precision}
         |Logistic regression recall: ${logisticRegressionPerformance.recall}
         |
         |""".stripMargin
    )

    libSVM.save(sc, "/target/tmp/libSVM")

    logisticRegressionModel.save(sc, "/target/tmp/logisticRegression")

    sc.stop()
  }

  def accidentToFeatures(featureGenerators: Seq[FeatureGenerators])(accident: Accident) = LabeledPoint(ExtractLabel.extract(accident), {
    val featureValues = featureGenerators.map(f => f.generateFeature(accident)).toArray
    val vectorSize = featureGenerators.length
    val indexes = (0 until vectorSize).toArray
    new SparseVector(size = vectorSize, indices = indexes, values = featureValues)
  })

  def loadData(sc: SparkContext, config: Configuration) = {
    sc.
      newAPIHadoopRDD(config, classOf[MongoInputFormat], classOf[Object], classOf[BSONObject]).
      asInstanceOf[RDD[(ObjectId, DBObject)]].
      flatMap {
        case (objId, dbObj) => {
          JsonSerialization.deserialize[Accident](dbObj) match {
            case JsSuccess(a, _) =>
              Some(a)
            case JsError(e) =>
              println(s"Error deserializing accident data:$e")
              None
          }
        }
      }.
      map(accidentToFeatures(featureGenerators)).
      randomSplit(Array(.6, .4), seed = 11L)
  }

  def testPerformance(model: GeneralizedLinearModel, testData: RDD[LabeledPoint], testDataCount: Long): ModelPerformance = {
    val modelPredictions = testData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    val (tpCount, fnCount, allOthers) = modelPredictions.aggregate(0, 0, 0)({
      case ((truePositiveCount, falseNegativeCount, allOthers), (trueLabel, predicted)) =>
        if (trueLabel == predicted) (truePositiveCount + 1, falseNegativeCount, allOthers)
        else if (trueLabel == 1 && trueLabel != predicted) (truePositiveCount, falseNegativeCount + 1, allOthers)
        else (truePositiveCount, falseNegativeCount, allOthers + 1)
    }, { case ((tp1, fn1, others1), (tp2, fn2, others2)) => (tp1 + tp2, fn1 + fn2, others1 + others2) })

    val precision = tpCount / testDataCount
    val recall = tpCount / (fnCount + tpCount)
    val others = allOthers / testDataCount

    ModelPerformance(precision = precision, recall = recall, others = others)
  }
}

case class ModelPerformance(precision: Double, recall: Double, others: Double)

