include "data.thrift"

namespace java com.machineLearning.ml.thrift

service PredictorService {
  map<data.Classifier, double> thresholds();
  data.Prediction predict(1: data.FeatureDict fd);
}
