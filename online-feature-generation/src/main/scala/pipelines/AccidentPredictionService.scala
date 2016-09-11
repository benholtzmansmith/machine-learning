package pipelines

import machineLearning.data.models.{ Accident, ModelPrediction }
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ Await, Future }

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
    val features: Seq[Double] = ???

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
