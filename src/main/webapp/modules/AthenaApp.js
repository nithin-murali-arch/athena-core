var app = angular.module('athenaApp', [ 'ngRoute' ]);

app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/login', {
		templateUrl : 'login.html',
		controller : 'LoginController'
	}).when('/upload', {
		templateUrl : 'addJar.html',
		controller : 'JarUpload'
	}).otherwise({
		redirectTo : '/login'
	})
} ]);

app.factory('bnbObjHolder', function() {
	var factory;
	if (factory === undefined) {
		factory = {};
	}

	factory.setParam = function(key, value) {
		factory[key] = value;
	}

	factory.getParam = function(key) {
		return factory[key];
	}

	return factory;
});

app.controller("LoginController", [ '$scope', function($scope) {

} ]);

app.controller("JarUpload", [ '$scope', function($scope) {

} ]);