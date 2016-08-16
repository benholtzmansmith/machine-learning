import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

package object models {
  case class Accident(date:DateTime, time:Double, vehicleType1:String, numerOfPersonsKilled:Int)

  object Accident {
    implicit val format:Format[Accident] = Json.format[Accident]
  }
}
