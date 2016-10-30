var app = angular.module('AthenaApp', [ 'ngRoute' ]);

app.config([ '$routeProvider', function($routeProvider) {
	$routeProvider.when('/login', {
		templateUrl : 'modules/templates/login.html',
		controller : 'LoginController'
	}).when('/upload', {
		templateUrl : 'modules/templates/addJar.html',
		controller : 'JarUpload'
	}).otherwise({
		redirectTo : '/login'
	})
} ]);

app.factory('bnbObjHolder', function() {
	var factory;
	if (factory === undefined) {
		factory = window.sessionStorage.getItem("factory");
		if (factory === null) {
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

app.controller("LoginController", [ '$scope', 'bnbObjHolder', function($scope, bnbObjHolder) {
	$scope.logout = function(){
		bnbObjHolder.logout();
	}
	
	$scope.test = "";
}]);

app.controller("JarUpload", [ '$scope', 'bnbObjectHolder', function($scope, bnbObjectHolder) {

}]);