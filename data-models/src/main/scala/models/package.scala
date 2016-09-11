package machineLearning.data

import java.util.Date

import org.joda.time.TimeOfDay
import play.api.libs.json._

object DateFormat {
  implicit val timeOfDayFormat: Format[TimeOfDay] = new Format[TimeOfDay] {
    def writes(o: TimeOfDay): JsValue = JsString(o.toString)

    def reads(json: JsValue): JsResult[TimeOfDay] = json match {
      case JsString(a) => JsSuccess(new TimeOfDay(a))
      case _ => JsError("Date deserialization faild. Field wasn't a string")
    }
  }
  implicit val javeDate: Format[Date] = new Format[Date] {
    def writes(o: Date): JsValue = JsString(o.toString)

    def reads(json: JsValue): JsResult[Date] = json match {
      case JsString(a) => JsSuccess(new Date(a))
      case _ => JsError("Date deserialization faild. Field wasn't a string")
    }
  }
}

package object models {
  import org.cvogt.play.json.implicits.formatSingleton
  import org.cvogt.play.json.SingletonEncoder.simpleName
  import org.cvogt.play.json.Jsonx

  sealed trait Borough
  case object MANHATTAN extends Borough
  case object BROOKLYN extends Borough
  case object QUEENS extends Borough
  case object STATENISLAND extends Borough
  case object BRONX extends Borough

  object Borough {
    final case class Unknown(private[models]json: JsValue) extends Borough
    implicit lazy val FormatBoroughUnknown: Format[Unknown] = Jsonx.formatInline[Unknown]
    implicit val formatBorough: Format[Borough] = Jsonx.formatSealedWithFallback[Borough, Unknown]
  }

  case class ZipCode(code: Int)

  object ZipCode {
    implicit val zipCodeFormat: Format[ZipCode] = new Format[ZipCode] {

      def writes(o: ZipCode): JsValue = JsString(o.toString)

      def reads(json: JsValue): JsResult[ZipCode] = {
        json match {
          case JsString(a) => JsSuccess(ZipCode(a.toInt))
          case JsNumber(a) => JsSuccess(ZipCode(a.toInt))
          case _ => JsError("Zip code deserialization error. Field wasn't a string or int"): JsError
        }
      }
    }
  }

  case class Accident(
    time: TimeOfDay,
    borough: Borough,
    location: String,
    onStreetName: String,
    offStreetName: String,
    numberOfPersonsInjured: Int,
    numberOfPersonsKilled: Int,
    numberOfPedestriansInjured: Int,
    numberOfCyclistsInjured: Int,
    numberOfMotoristsInjured: Int,
    contributingFactorVehicle1: String,
    contributingFactorVehicle2: String,
    contributingFactorVehicle3: String,
    contributingFactorVehicle4: String,
    vehicleTypeCode1: String,
    vehicleTypeCode2: String,
    vehicleTypeCode3: String,
    vehicleTypeCode4: String
  )

  object Accident {
    import DateFormat._
    implicit val format: Format[Accident] = Json.format[Accident]
  }

  case class ModelPrediction(
    accidentId: AccidentId,
    prediction: Prediction,
    confidence: Confidence
  )

  case class AccidentId(string: String)

  case class Prediction(value: Double)

  case class Confidence(value: Double)

}
