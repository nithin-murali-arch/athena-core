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
		factory = window.sessionStorage.getItem("factory");
		if (factory === undefined) {
			factory = {};
		}
	}

	factory.setParam = function(key, value) {
		factory[key] = value;
	};

	factory.getParam = function(key) {
		return factory[key];
	};
	
	factory.removeParam = function(key){
		delete factory[key];
	};
	
	factory.logout = function(){
		removeParam('credentials');
	};
	
	factory.persist = function(){
		
	};
	return factory;
});

app.controller("LoginController", [ '$scope', 'bnbObjectHolder', function($scope, bnbObjectHolder) {
	$scope.logout = function(){
		bnbObjectHolder.logout();
	}
	
	$scope.test = "";
}]);

app.controller("JarUpload", [ '$scope', 'bnbObjectHolder', function($scope, bnbObjectHolder) {

}]);