angular.module('machineLearningApp', ['angular-loading-bar'])
  .controller('TrainingController', function($scope, $http) {
    $scope.train = function(path){
		$http.get('/train').then(onSuccess, onFailure)
	}

	var onSuccess = function() { $scope.result = "Training model!" }

	var onFailure = function() { $scope.result = "Failed to train model!" }
  });