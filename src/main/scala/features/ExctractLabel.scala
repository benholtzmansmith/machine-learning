package features

import models.Accident

object ExctractLabel {
  def extract(accident: Accident):Double = {
    if (accident.numberOfPersonsKilled > 0) 1
    else 1
  }
}
