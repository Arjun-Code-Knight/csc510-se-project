var imgUrl;
chrome.extension.onMessage.addListener(function(request, sender, sendResponse) {
    if (request.name == 'screenshot') {
    	captureTab(sendResponse);
    }
    else if(request.name == 'coords') {
    	cropImage(request.coords, sendResponse);
    }
    return true;
});

function captureTab(sendResponse) {
	var IMG_QUALITY = 80; 
	var IMG_MIMETYPE = 'jpeg';
	var opts = {format: IMG_MIMETYPE, quality: IMG_QUALITY};
    chrome.tabs.captureVisibleTab(null, opts, function(dataUrl) {
    	imgUrl = dataUrl;
        sendResponse({ screenshotUrl: dataUrl });
    });
}


function cropImage(coords, callback) {
	var img = new Image();
	img.src = imgUrl;
	callback(imgUrl);
}

