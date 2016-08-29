angular.module('machineLearningApp', [])
  .controller('TrainingController', function($scope, $http) {
    $scope.train = function(path){
		$http.get('/train').then(
			function() {
				$scope.result = "Training model!"      
			},
			function(){
				$scope.result = "Failed to train model!"
			}
		)
	}
  });