package features.generators

import features.FeatureGenerators
import machineLearning.data.models.Accident

object HowManyCyclistsInjured extends FeatureGenerators {
  def generateFeature(accident: Accident): Double = accident.numberOfCyclistsInjured.toDouble
}
