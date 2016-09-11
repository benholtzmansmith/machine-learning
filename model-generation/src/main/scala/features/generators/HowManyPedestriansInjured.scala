package features.generators

import features.FeatureGenerators
import machineLearning.data.models.Accident

object HowManyPedestriansInjured extends FeatureGenerators {
  def generateFeature(accident: Accident): Double = accident.numberOfPedestriansInjured.toDouble
}
