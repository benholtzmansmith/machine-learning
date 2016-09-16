package pipelines

import com.twitter.finagle.Thrift
import machineLearning.data.models.{ Accident, ModelPrediction }

import scala.concurrent.duration.DurationInt
import com.machineLearning.thrift._
import com.twitter.util.{ Await, Duration, Future => TwitterFuture }

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
    val features: FeatureDict = ???

    /**
     * Pass features to thrift service to make model prediction
     */

    val predictor = Thrift.newServiceIface[PredictorService[TwitterFuture]]("localhost:9090")

    val predictionFut: TwitterFuture[Prediction] = predictor.predict(features)

    val predictionTimeout = new Duration(6 * 10 ^ 10)

    val prediction: Prediction = Await.result(predictionFut, predictionTimeout)

    doSomethingWithPrediction(prediction)

  }

  def doSomethingWithPrediction(modelPrediction: Prediction): Unit = ???
}
