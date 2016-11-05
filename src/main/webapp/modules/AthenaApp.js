"use strict";
var app = angular.module('AthenaApp', [ 'ngRoute', 'ngFileUpload']);
app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/login', {
		templateUrl : 'modules/templates/login.html',
		controller : 'LoginController'
	}).when('/upload', {
		templateUrl : 'modules/templates/addJar.html',
		controller : 'JarUpload'
	}).otherwise({
		redirectTo : '/login'
	});
}]);

app.service('bnbHttpService', ['$http', '$q', function($http, $q){
	this.call = function(config){
		var deferred = $q.defer();
		$http(config).then(function(response){
			deferred.resolve(response);
		});
		return deferred.promise;
	};
}]);

app.controller("LoginController", [ '$scope', 'bnbHttpService', '$location', function($scope, bnbHttpService, $location) {
	var data;
	$scope.login = {};
	$scope.submitLogin = function(){
		var loginConfig = {
		method: "post",
		headers: { 'Content-Type': 'application/json'},
		dataType: 'json',
		url: 'services/authenticate/login',
		data: JSON.stringify($scope.login)
	};
		bnbHttpService.call(loginConfig).then(function(response){
			data = response;
			console.log(response);
			if(response.data.loggedin){
				$location.path("/upload");
			}
		});
	};
	$scope.watchKeys = function($event){
		if($event.keyCode === 13){
			$scope.submitLogin();
		}
	}
}]);

app.controller("JarUpload", [ '$scope', 'bnbHttpService', function($scope, bnbHttpService) {
	
	$scope.upload = function(){
		var input =  document.getElementById('file');
	      var file = input.files[0];
	      var fr = new FileReader();
	      fr.onload = receivedText;
	      fr.readAsBinaryString(file);
		var uploadConfig = {
				method: "post",
				headers: { 'Content-Type': 'multipart/form-data'},
				url: 'services/upload/jar',
				data: {file: $scope.file}
			};
		if(angular.isDefined($scope.file)){
			delete $scope.error;
			var data;
			bnbHttpService.call(uploadConfig).then(function(response){
				data = response;
				console.log(response);
			});
		}
		else{
			$scope.error = "No file chosen!";
		}
	}
	function receivedText() {
	    document.getElementById('editor').appendChild(document.createTextNode(fr.result));
	  } 
}]);
