import play.api.libs.json.{Format, Json}

package object models {
  case class Accident(vehicleTypeCode1:String, numberOfPersonsKilled:Int)

  object Accident {
    implicit val format:Format[Accident] = Json.format[Accident]
  }
}
