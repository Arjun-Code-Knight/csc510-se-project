var imgUrl, userName;
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
      console.log(request);
        userName = request.data;
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

  //console.log(c);
  formData.append("USER", userName);
  formData.append("attachment", dataUrl1);



  var xhr = new XMLHttpRequest();
  xhr.open("POST", "http://192.168.0.15:8080/uploadService/chrome/file");
  xhr.send(formData);
}
