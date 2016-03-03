'use strict';

var snipItControllers = angular.module('snipItControllers', []);
var userName, currImage, currTagList;
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

snipItControllers.controller('HomeControl', ['$scope', '$routeParams','$location',
	function($scope, $routeParams, $location) {
		$scope.title = "Snip It!";
		$scope.name = userName;
		$scope.snipIt = "Take a Snippet";
		$scope.showHistory = "Show History";
    $scope.searchImages = "Search Images";

    chrome.extension.sendMessage({name: "currentUser", data: userName}, function(){
      console.log("userName passed");
    });

		$scope.snipItFunc = function() {
      window.close();
      chrome.tabs.executeScript(null, {file: "./js/myscript.js"});
		}

		$scope.showHistoryFunc = function() {
      $location.url('/history');
		}

    $scope.searchImagesFunc = function() {
      $location.url('/search');
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
		$scope.placeHolder = "UserName";
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

snipItControllers.controller('HistoryControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
		$scope.userName = userName;
    $scope.url;
    $scope.userData
    $http.get('http://192.168.0.15:8080/user/chrome/'+$scope.userName).success(function(data) {
        $scope.userData = data;
        console.log($scope.userData);
    });

    $scope.expandImage = function(data) {
      currImage = data.x.url;
      currTagList = data.x.tags;
      $location.url('/image');
    }
	}
]);

snipItControllers.controller('ImageControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
    $scope.enterTags = "Enter tags";
		$scope.userName = userName;
    $scope.enteredTag;
    $scope.tagList = new Array();
    var arr = currTagList.split('|');
    for(var i = 0; i < arr.length; i++) {
      if(arr[i] != '') {
          $scope.tagList.push(arr[i]);
      }

    }
    console.log(currTagList);
    $scope.imageSrc = currImage;
    $scope.addTag = function() {
      var formData = {
        fileName : currImage,
        tags : $scope.enteredTag
      };
      $http({
        method: 'POST',
        url: 'http://192.168.0.15:8080/uploadService/chrome/tags',
        data: formData
      }).then(function successCallback(response) {
        $scope.tagList.push($scope.enteredTag);
        $scope.enteredTag = '';
        console.log("Successfully created");
      }, function errorCallback(response) {
        console.error("FAILED");
      });
    }
    $scope.deleteThis = function(data) {
      console.log(data.x);
      var formData = {
        fileName : currImage,
        tags : data.x
      };
      $http({
        method: 'POST',
        url: 'http://192.168.0.15:8080/uploadService/chrome/deletetags',
        data: formData
      }).then(function successCallback(response) {
        $scope.tagList.splice(data.$index,1);
        console.log("Successfully deleted");
      }, function errorCallback(response) {
        console.error("FAILED");
      });

    }
	}
]);

snipItControllers.controller('SearchControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
    $scope.enterTags = "Search using tags";
		$scope.userName = userName;
    $scope.enteredTag;
    $scope.userData;
    $scope.search = function() {
      console.log("check");
      $http.get('http://192.168.0.15:8080/user/'+$scope.userName+'/'+$scope.enteredTag).success(function(data) {
          $scope.userData = data;
          console.log($scope.userData);
      });
    }


    $scope.expandImage = function(data) {
      currImage = data.x.url;
      $location.url('/image');
    }
	}
]);
