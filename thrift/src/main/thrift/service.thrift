include "data.thrift"

namespace java com.machineLearning.thrift

service PredictorService {
  data.Prediction predict(1: data.FeatureDict fd);
}
