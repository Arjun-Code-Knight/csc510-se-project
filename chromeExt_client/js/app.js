'use strict';

/* App Module */

var snipItApp = angular.module('snipItApp', [
  'ngRoute',
  'snipItControllers'
]);

snipItApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/', {
        templateUrl: 'partials/snipIt.html',
        controller: 'SnipItControl'
      }).
      when('/signUp', {
        templateUrl: 'partials/signUp.html',
        controller: 'SignUpControl'
      }).
      when('/home', {
        templateUrl: 'partials/home.html',
        controller: 'HomeControl'
      }).
      otherwise({
        templateUrl: 'partials/snipIt.html',
        controller: 'SnipItControl'
      });
  }]);
