package com.machineLearning.predictor

import com.machineLearning.thrift.{ FeatureDict, Prediction, PredictorService }
import com.twitter.finagle.Thrift
import com.twitter.util.{ Await, Future => TwitterFuture }
import org.apache.spark.mllib.classification.LogisticRegressionModel
import org.apache.spark.mllib.linalg.{ SparseVector, Vector }
import org.apache.spark.{ SparkConf, SparkContext }

/**
 * Created by benjaminsmith on 9/14/16.
 */
object Predictor {
  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf().setAppName("predictor")

    val sc = new SparkContext(sparkConf)

    val model: LogisticRegressionModel = LogisticRegressionModel.load(sc, "some/local/path")

    val predictor = new Predictor(model)

    Await.ready(predictor.run())
  }
}

class Predictor(logisticRegressionModel: LogisticRegressionModel) {

  def run() = Thrift.serveIface("localhost:9090", new PredictorService[TwitterFuture]() {

    def predict(fd: FeatureDict): TwitterFuture[Prediction] = {

      TwitterFuture {

        val vectorSize = fd.values.length

        val indexes = (0 until vectorSize).toArray

        val featuresDoubles = fd.values.map { _._2._1 }.toArray

        val features: Vector = new SparseVector(vectorSize, indexes, featuresDoubles)

        val prediction = logisticRegressionModel.predict(features)

        Prediction(prediction)

      }
    }
  })
}
