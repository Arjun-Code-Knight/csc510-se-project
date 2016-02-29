document.getElementById("butt").addEventListener('click', function() {
	chrome.extension.sendMessage({name: "screenshot"}, function(response) {
		window.close();
	 	chrome.tabs.executeScript(null, {file: "myscript.js"});
	});
	// var bgPage = chrome.extension.getBackgroundPage();
	// bgPage.hello(function(response) {
	// 	window.close();
	// 	chrome.tabs.executeScript(null, {file: "myscript.js"});
		
	// });
});