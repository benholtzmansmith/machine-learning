package features.generators

import features.FeatureGenerators
import models.Accident

object HowManyMotoristsInjured extends FeatureGenerators {
  def generateFeature(accident: Accident): Double = accident.numberOfMotoristsInjured
}
