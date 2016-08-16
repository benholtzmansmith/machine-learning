package sparkML

import com.mongodb.DBObject
import com.mongodb.hadoop.MongoInputFormat
import play.api.libs.json._
import com.mongodb.hadoop.util.MongoConfigUtil
import org.apache.hadoop.conf.Configuration

import scala.util.{Failure, Success, Try}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.bson.BSONObject
import org.bson.types.ObjectId
import org.joda.time.DateTime
import sparkML.FeatureGenerators.featureGenerators

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

case class Accident(date:DateTime, time:Double, vehicleType1:String, numerOfPersonsKilled:Int)

object Accident {
  implicit val format:Format[Accident] = Json.format[Accident]
}

object JsonSerialization {
  def deserialize[T: Reads](dbo: DBObject): JsResult[T] = {
    val jsonString = dbo.toString
    Try(Json.fromJson[T](Json.parse(jsonString))) match {
      case Success(result) => result
      case Failure(exception: Throwable) => JsError(exception.getMessage)
    }
  }
}

trait FeatureGenerators {
  def generateFeature(accident: Accident):Double
}
object FeatureGenerators {
  val featureGenerators:Seq[FeatureGenerators] = Seq(WasAtNight)
}

object WasAtNight extends FeatureGenerators {
  def generateFeature(accident: Accident): Double = {
    if (accident.time > 21 || accident.time < 5) 1
    else 0
  }
}

object ExctractLabel {
  def extract(accident: Accident):Double = {
    if (accident.numerOfPersonsKilled > 0) 1
    else 1
  }
}