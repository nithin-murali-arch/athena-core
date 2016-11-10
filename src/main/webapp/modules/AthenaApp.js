"use strict";
var app = angular.module('AthenaApp', ['ngMaterial', 'ui.bootstrap', 'ngRoute', 'ngFileUpload']);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/login', {
        templateUrl: 'modules/templates/login.html',
        controller: 'LoginController'
    }).when('/upload', {
        templateUrl: 'modules/templates/addJar.html',
        controller: 'JarUpload'
    }).when('/view', {
        templateUrl: 'modules/templates/view.html',
        controller: 'JarUpload'
    }).when('/loginplugin', {
        templateUrl: 'modules/templates/loginplugin.html',
        controller: 'LoginPluginController'
    }).otherwise({
        redirectTo: '/login'
    });
}]);

app.directive('bnbGrid', function() {
    return {
        restrict: 'E',
        scope: {
            grid: '=grid'
        },
        templateUrl: 'modules/templates/grid.html'
    }
});

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
            url: 'services/authenticate/login',
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
}]);

app.controller("JarUpload", ['$scope', 'Upload', '$timeout', 'bnbHttpService', function($scope, Upload, $timeout, bnbHttpService) {
    var jarColumns = ['jarName'];
    var appColumns = ['Param Name', 'Param Value'];
    if (sessionStorage.UserName.length !== 0)
    {
        $scope.loggedin = true ;
    }   
    bnbHttpService.call({
        'url': 'services/moduleHandler/listAll',
        'method': 'GET'
    }).then(function(response) {
        console.log(response);
        $scope.jars = JSON.parse(response.data);
        prepareJars();
        console.log($scope.appParams);
        $scope.jarGrid = {
                headers: jarColumns,
                data: $scope.jars
            };


    });
    bnbHttpService.call({
        'url': 'services/moduleHandler/listApps',
        'method': 'GET'
    }).then(function(response) {
        console.log(response);
        $scope.appParams = JSON.parse(response.data);
        prepareApps();
        console.log($scope.appParams);
        $scope.appGrid = {
                headers: appColumns,
                data: $scope.appParams,
                formBelow: true
            };
        $scope.appGrid.fire = function(key, value){
            bnbHttpService.call({
                'url': 'services/moduleHandler/addParam/' + key + '/' + value,
                'method': 'GET'
            }).then(function(response) {
                console.log(response);
                $scope.appParams = JSON.parse(response.data);
                prepareApps();
                console.log($scope.appParams);
                $scope.appGrid.data = $scope.appParams;
                key = undefined;
                value = undefined;
            });
        };

    });
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
    



    function prepareApps() {
        var objA = [];
        angular.forEach($scope.appParams, function(value, key) {
            var obj = {};
            obj.key = value.KEYTEXT;
            obj.value = value.VALUETEXT;
            objA.push(obj);
        });
        $scope.appParams = objA;
    }

    function prepareJars() {
        var objA = [];
        angular.forEach($scope.jars, function(value, key) {
            var obj = {};
            obj.key = value.jarName;
            objA.push(obj);
        });
        $scope.jars = objA;
    }

    $scope.$watch('file', function(newValue, oldValue) {
        if (newValue !== null && newValue !== undefined) {
            $scope.fileName = "C:\\Fakepath\\" + newValue.name;
        }
    });


}]);