package features

import models.Accident

trait FeatureGenerators {
  def generateFeature(accident: Accident):Double
}
object FeatureGenerators {
  val featureGenerators:Seq[FeatureGenerators] = Seq(WasPickUpTruck)
}