package utils

import com.mongodb.DBObject
import play.api.libs.json.{JsError, JsResult, Json, Reads}

import scala.util.{Failure, Success, Try}

object JsonSerialization {
  def deserialize[T: Reads](dbo: DBObject): JsResult[T] = {
    val jsonString = dbo.toString
    Try(Json.fromJson[T](Json.parse(jsonString))) match {
      case Success(result) => result
      case Failure(exception: Throwable) => JsError(exception.getMessage)
    }
  }
}
