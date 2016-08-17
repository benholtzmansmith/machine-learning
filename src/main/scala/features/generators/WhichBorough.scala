package features.generators

import features.FeatureGenerators
import models._

object WhichBorough extends FeatureGenerators{
  def generateFeature(accident: Accident): Double = {
    accident.boroughTyped match {
      case MANHATTAN => 1.0
      case BROOKLYN => 2.0
      case QUEENS => 3.0
      case STATENISLAND => 4.0
      case BRONX => 5.0
      case _ => 6.0
    }
  }
}
