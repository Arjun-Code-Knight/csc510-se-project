var imgUrl, userEmail, picType;
chrome.extension.onMessage.addListener(function(request, sender, sendResponse) {
    if (request.name == 'screenshot') {
    	captureTab(sendResponse);
    }
    else if(request.name == 'coords') {
    	cropImage(request.coords, sendResponse);
    }
    else if(request.name == "upload") {
        uploadFile(request.data);
    }
    else if(request.name == "currentUser") {
        userEmail = request.data;
    }
    else if(request.name == "picType") {
        picType = request.data;
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

function uploadFile(c) {

  var formData = new FormData();
  var image1 = new Image();
  image1.src = c;
  var dataUrl1 = c.replace(/^data:image\/(png|jpeg);base64,/, "");

  formData.append("email", userEmail);
  formData.append("private", picType);
  formData.append("attachment", dataUrl1);

  var xhr = new XMLHttpRequest();
  xhr.open("POST", "http://192.168.0.31:8080/uploadService/chrome/file");
  xhr.send(formData);
}
