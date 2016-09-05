package pipelines

import models.Accident
import features.FeatureGenerators.featureGenerators

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration.DurationInt

/**
 * Created by benjaminsmith on 9/5/16.
 */

object AccidentPredictionService {
  def main(args: Array[String]): Unit = {
    /**
     * Get some accident data from somewhere
     */
    val someAccidentData: Accident = ???

    /**
     * Generate online features
     */
    val features: Seq[Double] = featureGenerators.map(_.generateFeature(someAccidentData))

    /**
     * Pass features to thrift service to make model prediction
     */
    val predictionFut: Future[ModelPrediction] = ???

    val predictionTimeout = 1.minute

    val prediction: ModelPrediction = Await.result(predictionFut, predictionTimeout)

    doSomethingWithPrediction(prediction)
  }

  def doSomethingWithPrediction(modelPrediction: ModelPrediction): Unit = ???
}

case class ModelPrediction(
  accidentId: AccidentId,
  prediction: Prediction,
  confidence: Confidence
)

case class AccidentId(string: String)

case class Prediction(value: Double)

case class Confidence(value: Double)
