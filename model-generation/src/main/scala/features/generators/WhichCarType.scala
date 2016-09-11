package features.generators

import features.FeatureGenerators
import machineLearning.data.models.Accident

object WhichCarType extends FeatureGenerators {
  def generateFeature(accident: Accident): Double =
    CarTypeMap.featureMap.getOrElse(accident.vehicleTypeCode1, 999)
}

object CarTypeMap {
  //TODO:Type this
  private val carTypes = Seq(
    "",
    "PASSENGER VEHICLE",
    "SPORT UTILITY / STATION WAGON",
    "MOTORCYCLE",
    "LIVERY VEHICLE",
    "VAN",
    "UNKNOWN",
    "OTHER",
    "PICK-UP TRUCK",
    "SCOOTER",
    "TAXI",
    "LARGE COM VEH(6 OR MORE TIRES)",
    "SMALL COM VEH(4 TIRES)",
    "BUS",
    "BICYCLE",
    "FIRE TRUCK",
    "AMBULANCE"
  )

  val featureMap = carTypes.zipWithIndex.toMap.mapValues(_.toDouble)
}