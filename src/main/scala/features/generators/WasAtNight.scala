package features.generators

import features.FeatureGenerators
import models.Accident
import org.joda.time.TimeOfDay

object WasAtNight extends FeatureGenerators{
  val startOfDay = new TimeOfDay("6:00")

  val beginningOfNight = new TimeOfDay("20:00")

  def generateFeature(accident: Accident): Double = {
    if (accident.time.isBefore(startOfDay) || accident.time.isAfter(beginningOfNight)) 1.0
    else 0.0
  }
}
