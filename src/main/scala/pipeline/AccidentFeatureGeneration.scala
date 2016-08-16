package pipeline

import com.mongodb.DBObject
import com.mongodb.hadoop.MongoInputFormat
import com.mongodb.hadoop.util.MongoConfigUtil
import features.ExctractLabel
import models.Accident
import org.apache.hadoop.conf.Configuration
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.bson.BSONObject
import org.bson.types.ObjectId
import play.api.libs.json._
import utils.JsonSerialization
import features.FeatureGenerators.featureGenerators
import scala.util.{Failure, Success, Try}

object AccidentFeatureGeneration {
	def main(args:Array[String]):Unit = {
 		val sparkConf = new SparkConf().setMaster("local").setAppName("ml")
		val sc = new SparkContext(sparkConf)
    val config = new Configuration
    MongoConfigUtil.setInputURI(config, "mongodb://localhost:27017/accidents.accidents")
    val features = sc.newAPIHadoopRDD(config, classOf[MongoInputFormat], classOf[Object], classOf[BSONObject] ).
      asInstanceOf[RDD[( ObjectId, DBObject )]].
      flatMap{ case (objId, dbObj) => JsonSerialization.deserialize[Accident](dbObj).asOpt }
        .map(accident => LabeledPoint(ExctractLabel.extract(accident), {
          val featureValues = featureGenerators.map(f => f.generateFeature(accident)).toArray
          val vectorSize = featureGenerators.length
          val indexes = (0 to vectorSize).toArray
          new SparseVector(size = vectorSize, indices = indexes, values = featureValues)
        })).randomSplit(Array(.6, .4), seed = 11L)

    val trainData = features(0)
    val testData = features(1)

    val logisticRegression = new LogisticRegression
    val logisticRegressionWithLBFGS = new LogisticRegressionWithLBFGS

    val model = logisticRegressionWithLBFGS.run(trainData)

    val performance = testData.map{ point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }.filter{ case (trueLabel, predicted) => trueLabel != predicted}.count() / testData.count.toDouble

    println(s"""Model Performance: $performance""")
  }
}



