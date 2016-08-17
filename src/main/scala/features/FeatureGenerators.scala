package features

import features.generators.{WasAtNight, WhichBorough, WhichCarType}
import models.Accident

trait FeatureGenerators {
  def generateFeature(accident: Accident):Double
}
object FeatureGenerators {
  val featureGenerators:Seq[FeatureGenerators] = Seq(
    WhichCarType,
    WasAtNight,
    WhichBorough
  )
}