var app=angular.module('wintousapp',['ngRoute'])
.config(['$routeProvider', function($routeProvider){
    $routeProvider.when('/computers', {
        templateUrl: basePath+'/views/button.html',
        controller:"testController"
    });
}]);
app.controller("testController",function($scope){
	console.log("-----------------------");
});
