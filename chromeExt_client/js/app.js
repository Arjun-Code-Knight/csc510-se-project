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
      when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LogInControl'
      }).
      when('/home', {
        templateUrl: 'partials/home.html',
        controller: 'HomeControl'
      }).
      when('/history', {
        templateUrl: 'partials/history.html',
        controller: 'HistoryControl'
      }).
      when('/image', {
        templateUrl: 'partials/image.html',
        controller: 'ImageControl'
      }).
      when('/search', {
        templateUrl: 'partials/search.html',
        controller: 'SearchControl'
      }).
      when('/snapshot', {
        templateUrl: 'partials/snapshot.html',
        controller: 'SnapShotControl'
      }).
      when('/feedback', {
        templateUrl: 'partials/feedback.html',
        controller: 'FeedbackControl'
      }).
      otherwise({
        templateUrl: 'partials/snipIt.html',
        controller: 'SnipItControl'
      });
  }]);
