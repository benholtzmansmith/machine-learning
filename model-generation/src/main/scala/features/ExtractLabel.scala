package features

import machineLearning.data.models.Accident

object ExtractLabel {
  def extract(accident: Accident): Double = {
    if (accident.numberOfPersonsKilled > 0) 1
    else 1
  }
}
