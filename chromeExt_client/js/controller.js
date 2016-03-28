'use strict';

var app = angular.module('snipItControllers', []);
var userName, email, currImage, currTagList;
app.controller('SnipItControl', ['$scope', '$routeParams','$location',
  function($scope, $routeParams, $location) {
		$scope.title = "Snip It!";
		var key = JSON.parse(localStorage.getItem("snipItApp"));
		if(key) {
				$scope.userName = key["userName"];
        $scope.email = key["email"];
        //TODO: logIn
		} else {
				$scope.userName = null;
        $scope.email = null;
		}

		if($scope.email) {
			userName = $scope.userName;
      email = $scope.email;
			$location.url('/home');
		}else{
			$location.url('/signUp');
		}
	}
]);

app.controller('HomeControl', ['$scope', '$routeParams','$location',
	function($scope, $routeParams, $location) {
		$scope.title = "Snip It!";
		$scope.name = userName;
    $scope.email = email;
		$scope.snipIt = "Take a Snippet";
		$scope.showHistory = "Show History";
    $scope.searchImages = "Search Images";
    chrome.extension.sendMessage({name: "currentUser", data: email}, function(){
      console.log("userName passed");
    });

		$scope.snipItFunc = function() {
      $location.url('/snapshot');
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

app.controller('SnapShotControl', ['$scope', '$routeParams','$location',
	function($scope, $routeParams, $location) {
    $scope.takePrivate = "Capture private screenshot";
    $scope.takePublic = "Capture public screenshot";
    $scope.publicFunc = function() {
      chrome.extension.sendMessage({name: "picType", data: "false"}, function(){
        console.log("pictype passed");
      });
      window.close();
      chrome.tabs.executeScript(null, {file: "./js/myscript.js"});
    }
    $scope.privateFunc = function() {
      chrome.extension.sendMessage({name: "picType", data: "true"}, function(){
        console.log("pictype passed");
      });
      window.close();
      chrome.tabs.executeScript(null, {file: "./js/myscript.js"});
    }

  }
]);

app.directive('headerDetail', function ($location) {
    return {
        restrict: 'E',
        link: function($scope, element, attrs) {
            $scope.userName = userName;
            $scope.renderHome = function() {
              $location.url('/home');
            };
            $scope.signOut = function() {
              localStorage.removeItem('snipItApp');
              $location.url('/');
            }
        },
        templateUrl: "../partials/headerDetail.html"
    };
});

app.controller('SignUpControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
		$scope.placeHolderName = "UserName";
    $scope.placeHolderPass = "Password";
    $scope.placeHolderEmail = "Email";
    $scope.placeHolderAge = "Age";
		$scope.getIn = "Sign Up!";
    $scope.logIn = "Login";
    // $scope.userName;
    // $scope.userPass;
    // $scope.email;
    $scope.sex;
    $scope.data = {
      availableOptions: [
        {id: '1', name: 'Background'},
        {id: '2', name: 'Student'},
        {id: '3', name: 'Professor'},
        {id: '4', name: 'Technical Professional'},
        {id: '5', name: 'Non-Technical Professional'}
      ],
      selectedOption: {id: '2', name: 'Student'}
    };
		$scope.getInFunc = function() {
			if($scope.email != undefined && $scope.userPass != undefined && $scope.age != undefined) {
					var obj = {
                      'userName':$scope.userName,
                      'email':$scope.email,
                      'password':$scope.userPass,
                      'age':$scope.age,
                      'occupation':$scope.data.selectedOption.name,
                      'sex':$scope.sex
                    };
					localStorage.setItem('snipItApp',JSON.stringify(obj));
					userName = $scope.userName;
          $http({
            method: 'POST',
            url: 'http://localhost:8080/user/signup/',
            data: obj
          }).then(function successCallback(response) {
            delete obj.age;
            delete obj.occupation;
            delete obj.sex;
            localStorage.setItem('snipItApp',JSON.stringify(obj));
  					userName = $scope.userName;
            email = $scope.email;
  					$location.url('/home');
          }, function errorCallback(response) {
            console.error("FAILED ");
            console.log(response);
          });
			} else {
          console.log($scope.email +" "+ $scope.userPass +" "+ $scope.age);
      }
		}

    $scope.logInFunc = function() {
			$location.url('/login');
		}
	}
]);

app.controller('LogInControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
    $scope.placeHolderPass = "Password";
    $scope.placeHolderEmail = "Email";
    $scope.logIn = "Login";
		$scope.logInFunc = function() {
			if($scope.email != undefined && $scope.userPass != undefined) {
					var obj = {
                      'email':$scope.email,
                      'password':$scope.userPass
                    };
          $http({
            method: 'POST',
            url: 'http://localhost:8080/user/login/',
            data: obj
          }).then(function successCallback(response) {
            if(response.data.success == "Yes") {
              userName = response.data.user;
              obj.userName = userName;
              localStorage.setItem('snipItApp',JSON.stringify(obj));
              $location.url('/home');
            }

          }, function errorCallback(response) {
            console.error("FAILED");
          });
					//localStorage.setItem('snipItApp',JSON.stringify(obj));
					//userName = $scope.userName;
					//$location.url('/home');
			} else {
          console.log($scope.email +" "+ $scope.userPass +" "+ $scope.age);
      }
		}
	}
]);

app.controller('HistoryControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
		$scope.userName = userName;
    $scope.email = email;
    $scope.url;
    $scope.userData
    $http.get('http://localhost:8080/user/search/chrome/'+$scope.email).success(function(data) {
        $scope.userData = data;
    });

    $scope.expandImage = function(data) {
      currImage = data.x.url;
      currTagList = data.x.tags;
      $location.url('/image');
    }
	}
]);

app.controller('ImageControl', ['$scope', '$routeParams','$location','$http',
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
    $scope.imageSrc = currImage;
    $scope.copy = function() {
      document.getElementsByClassName("imageUrlInput")[0].select();
      document.execCommand("copy");
    }
    $scope.addTag = function() {
      var formData = {
        fileName : currImage,
        tags : $scope.enteredTag
      };
      $http({
        method: 'POST',
        url: 'http://localhost:8080/uploadService/chrome/tags',
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
      var formData = {
        fileName : currImage,
        tags : data.x
      };
      $http({
        method: 'POST',
        url: 'http://localhost:8080/uploadService/chrome/deletetags',
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

app.controller('SearchControl', ['$scope', '$routeParams','$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.title = "Snip It!";
    $scope.enterTags = "Search using tags";
		$scope.userName = userName;
    $scope.email = email;
    $scope.enteredTag;
    $scope.userData;
    $scope.crossSearch;
    $scope.search = function() {
      console.log($scope.crossSearch);
      var searchType;
      if($scope.crossSearch) {
        searchType = "crosssearch";
      } else {
        searchType = "search";
      }
      $http.get('http://localhost:8080/user/'+searchType+'/chrome/'+$scope.email+'/'+$scope.enteredTag).success(function(data) {
          $scope.userData = data;
      });
    }


    $scope.expandImage = function(data) {
      currImage = data.x.url;
      $location.url('/image');
    }
	}
]);
