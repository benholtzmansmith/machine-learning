package features

import models.Accident

object WasPickUpTruck extends FeatureGenerators {
  def generateFeature(accident: Accident): Double =
    if (accident.vehicleTypeCode1 == "PICK-UP TRUCK") 1.0
    else 0.0
}
