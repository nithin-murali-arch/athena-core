"use strict";
var app = angular.module('PluginApp', ['ngMaterial', 'ui.bootstrap', 'ngRoute', 'ngFileUpload']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/loginplugin', {
        templateUrl: 'modules/templates/loginplugin.html',
        controller: 'LoginPluginController'
    }).when('/view', {
        templateUrl: 'modules/templates/view.html',
        controller: 'JarUploadController'
    }).otherwise({
        redirectTo: '/loginplugin'
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


app.controller("LoginPluginController", ['$scope', 'bnbHttpService', '$location', function($scope, bnbHttpService, $location) {
    var data;
    $scope.login = {};
    $scope.submitLogin = function() {
        var loginConfig = {
            method: "post",
            headers: {
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: 'services/pluginhub/login',
            data: JSON.stringify($scope.login)
        };
        bnbHttpService.call(loginConfig).then(function(response) {
            data = response;
            console.log(response);
            if (response.data.loggedin) {
            	sessionStorage.UserName = $scope.login.username;
                $location.path("/view");
            }
        });
    };
    $scope.watchKeys = function($event) {
        if ($event.keyCode === 13) {
            $scope.submitLogin();
        }
    };

    $scope.searchResult = function(name) {
        var routeInfo = {
            method: "get",
            headers: {
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: 'services/pluginhub/searchAll/' + name
        };

        bnbHttpService.call(routeInfo).then(function(response) {
        	$scope.pluginData = response.data;
            });
    };
    $scope.routeToView = function(plugin) {
    	sessionStorage.createdBy = plugin.createdBy;
    	sessionStorage.createdDate = plugin.createdDate;
    	sessionStorage.fileName = plugin.fileName;
        sessionStorage.pluginDesc = plugin.pluginDesc;
        sessionStorage.pluginName = plugin.pluginName;
        sessionStorage.id = plugin.id;
        sessionStorage.isLatest = plugin.isLatest;
        sessionStorage.downloadCount = plugin.downloadCount;
        sessionStorage.loggedIn = false;
        sessionStorage.loggedInVIewFlag = true;
    	$location.path("/view");	
    };
    
}]);

app.controller("JarUploadController", ['$scope', 'Upload', '$timeout', 'bnbHttpService', function($scope, Upload, $timeout, bnbHttpService) {
	$scope.searchData = {};
    var jarColumns = ['jarName'];
    var appColumns = ['Param Name', 'Param Value'];
    $scope.createdBy = sessionStorage.createdBy;
    $scope.createdDate = sessionStorage.createdDate;
    $scope.fileName = sessionStorage.fileName;
    $scope.pluginDesc = sessionStorage.pluginDesc;
    $scope.pluginName = sessionStorage.pluginName;
    $scope.id = sessionStorage.id;
    $scope.isLatest = sessionStorage.isLatest;
    $scope.downloadCount = sessionStorage.downloadCount;
    $scope.loggedIn = sessionStorage.loggedIn;
    $scope.loggedInVIewFlag = sessionStorage.loggedInVIewFlag;
    $scope.loggedIn = $scope.loggedIn == "true" ? true : false;
    
    if($scope.loggedIn == true){
    	$scope.fileName = "";
    }
    if (angular.isDefined(sessionStorage.UserName) && sessionStorage.UserName.length !== 0)
    {
        $scope.loggedIn = true ;
        $scope.userName = sessionStorage.UserName; 
    }  
    if($scope.loggedIn == true && $scope.createdBy == undefined){
    	$scope.fileName = undefined;
    }
       $scope.searchResult = function(name) {
    	$scope.updateFlag = true;
    	$scope.viewFlag = false;
        var routeInfo = {
            method: "get",
            headers: {
                'Content-Type': 'application/json'
            },
            dataType: 'json',
            url: 'services/pluginhub/searchAll/' + name
        };

        bnbHttpService.call(routeInfo).then(function(response) {
        	$scope.pluginData = response.data;
            });
    };
    
    $scope.showUpdate = function(plugin) {
        if(plugin.createdBy == $scope.userName) {
            $scope.viewFlag = true;
            $scope.updateFlag = false;
        }
    	$scope.searchData = plugin;
    };
    
    $scope.upload = function(file) {
    	$scope.searchData.createdBy = sessionStorage.UserName;
    	$scope.searchData.fileName = file.name;
    	bnbHttpService.call({
            'url': 'services/pluginhub/add/',
            'method': 'POST',
            data: $scope.searchData
        }).then(function(response) {
        	if(response.data === 'Success!'){
                file.upload = Upload.upload({
                    url: 'pluginHubUpload',
                    data: {
                        file: file
                    },
                });
                file.upload.then(function(response) {
                    $timeout(function() {
                        file.result = response.data;
                        if (angular.isDefined(file.result.error)) {
                            alert(file.result.error);
                        } else {
                            delete $scope.file;
                            $scope.fileName = "";
                            $scope.appParams = file.result.appParams;
                            $scope.jars = file.result.jars;
                            prepareApps();
                            prepareJars();
                            $scope.jarGrid.data = $scope.jars;
                            $scope.appGrid.data = $scope.appParams;
                        }

                    });
                });
        	}
        });

    }
    
    $scope.$watch('file', function(newValue, oldValue) {
        if (newValue !== null && newValue !== undefined) {
            $scope.fileName = "C:\\Fakepath\\" + newValue.name;
            $scope.searchData.fileName = newValue.name;
        }
    });


}]);
