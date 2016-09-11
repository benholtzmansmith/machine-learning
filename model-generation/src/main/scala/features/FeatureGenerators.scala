package features

import features.generators._
import machineLearning.data.models.Accident

trait FeatureGenerators {
  def generateFeature(accident: Accident): Double
}
object FeatureGenerators {
  val featureGenerators: Seq[FeatureGenerators] = Seq(
    WhichCarType,
    WasAtNight,
    WhichBorough,
    HowManyMotoristsInjured,
    HowManyCyclistsInjured,
    HowManyPedestriansInjured
  )
}