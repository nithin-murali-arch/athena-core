"use strict";
var app = angular.module('AthenaApp', ['ngMaterial','ui.bootstrap','ngRoute', 'ngFileUpload']);
app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'modules/templates/login.html',
        controller: 'LoginController'
    }).when('/upload', {
        templateUrl: 'modules/templates/addJar.html',
        controller: 'JarUpload'
    }).otherwise({
        redirectTo: '/login'
    });
}]);

app.service('bnbHttpService', ['$http', '$q', function($http, $q) {
    this.call = function(config) {
        var deferred = $q.defer();
        $http(config).then(function(response) {
            deferred.resolve(response);
        });
        return deferred.promise;
    };
}]);

app.controller("LoginController", ['$scope', 'bnbHttpService', '$location', function($scope, bnbHttpService, $location) {
    var data;
    $scope.login = {};
    $scope.submitLogin = function() {
        var loginConfig = {
            method: "post",
            headers: {
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: 'services/authenticate/login',
            data: JSON.stringify($scope.login)
        };
        bnbHttpService.call(loginConfig).then(function(response) {
            data = response;
            console.log(response);
            if (response.data.loggedin) {
                $location.path("/upload");
            }
        });
    };
    $scope.watchKeys = function($event) {
        if ($event.keyCode === 13) {
            $scope.submitLogin();
        }
    };
}]);

app.controller("JarUpload", ['$scope', 'Upload', '$timeout', function($scope, Upload, $timeout) {

    $scope.upload = function(file) {
        file.upload = Upload.upload({
            url: 'UploadJar',
            data: {
                file: file
            },
        });
        file.upload.then(function(response) {
            $timeout(function() {
                file.result = response.data;
                if(angular.isDefined(file.result.error)){
                	alert(file.result.error);
                }
                else{
                	delete $scope.file;
                    $scope.fileName = "";
                    $scope.appParams = file.result.parameters
                    $scope.jars = file.result.jars;
                }
                
            });
        });
    }
        $scope.$watch('file', function(newValue, oldValue) {
        	if(newValue !== null && newValue !== undefined){
        		$scope.fileName = "C:\\Fakepath\\" +newValue.name;
        	}
        });
//            document.getElementById("uploadBtn").onchange = function() {
//                document.getElementById("uploadFile").value = this.value;
//            };
//        };
//        function receivedText() {
//    	    document.getElementById('editor').appendChild(document.createTextNode(fr.result));
//    	} 
//    };
}]);