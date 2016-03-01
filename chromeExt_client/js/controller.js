'use strict';

var snipItControllers = angular.module('snipItControllers', []);
var userName;
snipItControllers.controller('SnipItControl', ['$scope', '$routeParams','$location',
  function($scope, $routeParams, $location) {
    // $http.get('phones/phones.json').success(function(data) {
    //   $scope.phones = data;
  	// });
		$scope.title = "Snip It!";
		var key = JSON.parse(localStorage.getItem("snipItApp"));
		if(key) {
				$scope.userName = key["un"];
		} else {
				$scope.userName = null;
		}

		if($scope.userName) {
			//TODO: snipit page
			userName = $scope.userName;
			$location.url('/home');
		}else{
			//TODO: getIn page
			//window.location.href = '#/signUp';
			//$location.path( "/signUp" );
			$location.url('/signUp');
		}
	}
]);

snipItControllers.controller('HomeControl', ['$scope', '$routeParams',
	function($scope, $routeParams) {
		$scope.title = "Snip It!";
		$scope.name = userName;
		$scope.snipIt = "Take a Snippet";
		$scope.showHistory = "Show History";
		$scope.snipItFunc = function() {
			chrome.extension.sendMessage({name: "screenshot"}, function(response) {
				window.close();
			 	chrome.tabs.executeScript(null, {file: "./js/myscript.js"});
			});
		}

		$scope.showHistoryFunc = function() {
			console.log("wassup");
		}
		// document.getElementById("snip").addEventListener('click', function() {
		// 	chrome.extension.sendMessage({name: "screenshot"}, function(response) {
		// 		window.close();
		// 	 	chrome.tabs.executeScript(null, {file: "./js/myscript.js"});
		// 	});
		// 	// var bgPage = chrome.extension.getBackgroundPage();
		// 	// bgPage.hello(function(response) {
		// 	// 	window.close();
		// 	// 	chrome.tabs.executeScript(null, {file: "myscript.js"});
		//
		// 	// });
		// });
	}
]);

snipItControllers.controller('SignUpControl', ['$scope', '$routeParams','$location',
	function($scope, $routeParams, $location) {
		$scope.title = "Snip It!";
		$scope.placeHolder = "UserName"
		$scope.getIn = "Get In!";
		$scope.userName;
		$scope.getInFunc = function() {
			if($scope.userName) {
					var obj = {'un':$scope.userName};
					localStorage.setItem('snipItApp',JSON.stringify(obj));
					userName = $scope.userName;
					$location.url('/home');
			}

		}
	}
]);
