namespace java com.machineLearning.thrift


struct FeatureKey {
  1: required string featureType;
  2: required string key;
}

struct FeatureValue {
  1: required double value;
}

struct FeatureKV {
  1: required FeatureKey feature;
  2: required FeatureValue value;
}

struct FeatureDict {
  1: required list<FeatureKV> values;
}

struct Prediction {
  1: required double probability;
}

