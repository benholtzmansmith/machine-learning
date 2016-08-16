package pipeline

import com.mongodb.DBObject
import com.mongodb.hadoop.MongoInputFormat
import com.mongodb.hadoop.util.MongoConfigUtil
import features.ExctractLabel
import features.FeatureGenerators.featureGenerators
import models.Accident
import org.apache.hadoop.conf.Configuration
import org.apache.log4j.Logger
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.linalg.SparseVector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.bson.BSONObject
import org.bson.types.ObjectId
import play.api.libs.json.{JsError, JsSuccess}
import utils.JsonSerialization


/**
  *
  * Spark job to analyze new york accident data.
  *
  * To run this:
  * (1) curl https://nycopendata.socrata.com/api/views/h9gi-nx95/rows.csv >> accident.csv
  * (2) mongo import csv to local mongo instance
  * (3) rename fields to match the case class names
  * (3) Run job :-)
  *
  * Current precision of model: 98%
  * Recall is probably pretty shit but haven't checked.
  *
  * */


object AccidentFeatureGeneration {

  val logger = Logger.getLogger(getClass)

  def main(args:Array[String]):Unit = {

    println("Starting Accident Data Model Learning ")

 		val sparkConf = new SparkConf().setMaster("local").setAppName("accident-machine-learning")

		val sc = new SparkContext(sparkConf)

    val config = new Configuration

    //Hard coded for now to just point to a local mongo instance
    MongoConfigUtil.setInputURI(config, "mongodb://localhost:27017/accidents.accidents")

    val features =
      sc.
        newAPIHadoopRDD(config, classOf[MongoInputFormat], classOf[Object], classOf[BSONObject] ).
        asInstanceOf[RDD[( ObjectId, DBObject )]].
        flatMap{ case (objId, dbObj) => {
          JsonSerialization.deserialize[Accident](dbObj) match {
            case JsSuccess(a, _) =>
              Some(a)
            case JsError(e) =>
              println(s"Error deserializing accident data:$e")
              None
          }
        } }.
        map(accident => LabeledPoint(ExctractLabel.extract(accident), {
          val featureValues = featureGenerators.map(f => f.generateFeature(accident)).toArray
          val vectorSize = featureGenerators.length
          val indexes = (0 until vectorSize).toArray
          new SparseVector(size = vectorSize, indices = indexes, values = featureValues)
        })).
        randomSplit(Array(.6, .4), seed = 11L)

    val trainData = features(0)

    val testData = features(1)

    val logisticRegressionWithLBFGS = new LogisticRegressionWithLBFGS

    val model = logisticRegressionWithLBFGS.run(trainData)

    val precision = testData.map{ point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }.filter{ case (trueLabel, predicted) => trueLabel != predicted}.count() / testData.count.toDouble

    println(s"""Model Precision: $precision""")
  }
}



