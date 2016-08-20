angular.module('machineLearningApp', [])
  .controller('TrainingController', function($scope) {
    $scope.train = function(){
      console.log("running")
    }
  });