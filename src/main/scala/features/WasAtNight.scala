package features

import models.Accident

object WasAtNight extends FeatureGenerators {
  def generateFeature(accident: Accident): Double = {
    if (accident.time > 21 || accident.time < 5) 1
    else 0
  }
}
