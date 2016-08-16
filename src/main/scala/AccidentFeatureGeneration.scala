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
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.bson.BSONObject
import org.bson.types.ObjectId
import org.joda.time.DateTime

object AccidentFeatureGeneration {
	def main(args:Array[String]):Unit = {
		val path = args(0)
 		val sparkConf = new SparkConf().setMaster("local").setAppName("ml")
		val sc = new SparkContext(sparkConf)
    val config = new Configuration
    MongoConfigUtil.setInputURI(config, "localhost:27017")
    sc.newAPIHadoopRDD(config, classOf[MongoInputFormat], classOf[Object], classOf[BSONObject] ).
      asInstanceOf[RDD[( ObjectId, DBObject )]].
      flatMap{ case (objId, dbObj) => JsonSerialization.deserialize[Accident](dbObj).asOpt }
    new LogisticRegression()

	}
}

case class Accident(date:DateTime, time:DateTime, vehicleType1:String)

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