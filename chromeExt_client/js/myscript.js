
// var script = document.createElement('script');
// script.src = 'http://code.jquery.com/jquery-1.11.0.min.js';
// script.type = 'text/javascript';
// document.getElementsByTagName('head')[0].appendChild(script);
var sTop = document.body.scrollTop;
var sLeft = document.body.scrollLeft;
var div = document.createElement( 'div' );
var h = window.innerHeight;
var w = window.innerWidth;
div.className = "modalDiv";
document.body.appendChild( div );
div.style.height = h+"px";
div.style.width = w+"px";
div.style.backgroundColor = "rgba(0,0,0,0.2)";
div.style.position = "absolute";
div.style.top = sTop+"px";
div.style.left = sLeft+"px";
div.style.display = "block";
div.style.zIndex = "2000000000";


document.body.style.cursor = "crossHair";

var initX, initY, finalX, finalY;
var leftDiv, rightDiv, topDiv, bottomDiv, snipDiv;
document.onmousedown = function(e) {
	initX = e.clientX;
	initY = e.clientY;
	document.onmousemove = function(e) {
		captureCoords(e);
		//console.log(e.clientY);
	}
	this.onmouseup = function(e) {
		finalX = e.clientX;
		finalY = e.clientY
		chrome.extension.sendMessage({name: "screenshot"}, function(response) {
			document.onmousemove = null
    	var coords = {
    		left : initX<finalX?initX:finalX,
    		width : Math.abs(initX-finalX),
    		top : initY<finalY?initY:finalY,
    		height : Math.abs(initY-finalY),
    		wHeight : h,
    		wWidth : w
    	};
    	chrome.extension.sendMessage({name: "coords", coords: coords}, function(dataUrl){
    		var x = window.open();
    		var image = document.createElement("img");
    		image.className = "screenShot";
    		image.src = dataUrl;
    		image.height = h;
    		image.width = w;
    		//image.style.position = "absolute";


    		var newWindow = x.document.body;
    		newWindow.margin = 0;
    		//commented for now
			//newWindow.appendChild(image);


			var devRes = window.devicePixelRatio?window.devicePixelRatio:1;
			if(devRes == 1){
				image.onload = function() {
					var c = document.createElement("canvas");
					c.id = "myCanvas";
					c.width = coords.width;
					c.height = coords.height;
					var ctx = c.getContext("2d");
					ctx.webkitImageSmoothingEnabled = false;
					ctx.mozImageSmoothingEnabled = false;
					ctx.msImageSmoothingEnabled = false;
					ctx.imageSmoothingEnabled = false;
					ctx.drawImage(image,
						coords.left*devRes, coords.top*devRes,
						coords.width*devRes, coords.height*devRes,
						0,0,
						coords.width, coords.height);
					newWindow.appendChild(c);
					removeDivsAndEvents();
				}
			}

			else {
				image.onload = function() {
					var c = document.createElement("canvas");
					c.id = "myCanvas";
					c.width = coords.width*2;
					c.height = coords.height*2;
					c.style.width = coords.width+"px";
					c.style.height = coords.height+"px";
					c.getContext("2d").scale(2,2)
					var ctx = c.getContext("2d");
					ctx.webkitImageSmoothingEnabled = false;
					ctx.mozImageSmoothingEnabled = false;
					ctx.msImageSmoothingEnabled = false;
					ctx.imageSmoothingEnabled = false;
					ctx.drawImage(image,
						coords.left*devRes, coords.top*devRes,
						coords.width*devRes, coords.height*devRes,
						0,0,
						coords.width, coords.height);
					newWindow.appendChild(c);
					removeDivsAndEvents();
					var dataUrl = c.toDataURL();
					chrome.extension.sendMessage({name: "upload", data: dataUrl}, function(dataUrl){
						console.log("UPLOADED :P");
					});
				}
			}
    	});
		});


  	}
}

function captureCoords(e) {
	if(e.clientY != initY || e.clientX != initX) {
		//document.body.removeChild(div);
		if(div.style.backgroundColor == "rgba(0, 0, 0, 0.2)") {
			div.style.backgroundColor = "rgba(0, 0, 0, 0)";

			leftDiv = document.createElement( 'div' );
			leftDiv.className = "leftDiv";
			leftDiv.style.backgroundColor = "rgba(0,0,0,0.2)";
			leftDiv.style.position = "absolute";
			div.appendChild(leftDiv);

			rightDiv = document.createElement( 'div' );
			rightDiv.className = "rightDiv";
			rightDiv.style.backgroundColor = "rgba(0,0,0,0.2)";
			rightDiv.style.position = "absolute";
			div.appendChild(rightDiv);

			topDiv = document.createElement( 'div' );
			topDiv.className = "topDiv";
			topDiv.style.backgroundColor = "rgba(0,0,0,0.2)";
			topDiv.style.position = "absolute";
			div.appendChild(topDiv);

			bottomDiv = document.createElement( 'div' );
			bottomDiv.className = "bottomDiv";
			bottomDiv.style.backgroundColor = "rgba(0,0,0,0.2)";
			bottomDiv.style.position = "absolute";
			div.appendChild(bottomDiv);

			snipDiv = document.createElement( 'div' );
			snipDiv.className = "snipDiv";
			snipDiv.style.backgroundColor = "rgba(255,255,255,0)";
			snipDiv.style.position = "absolute";
			div.appendChild(snipDiv);
		}
		if(leftDiv) {
			leftDiv.style.left = 0;
			var topL = initY<e.clientY?initY:e.clientY;
			leftDiv.style.top = topL+"px";
			var widthL = initX<e.clientX?initX:e.clientX;
			leftDiv.style.width = widthL+"px";
			var heightL = Math.abs(e.clientY-initY);
			leftDiv.style.height = heightL+"px";

			var rLeft = initX>e.clientX?initX:e.clientX;
			rightDiv.style.left = rLeft+"px";
			var topR = initY<e.clientY?initY:e.clientY;
			rightDiv.style.top = topR+"px";
			rightDiv.style.right = 0;
			var heightR = Math.abs(e.clientY-initY);
			rightDiv.style.height = heightR+"px";

			topDiv.style.left = 0;
			topDiv.style.top = 0;
			topDiv.style.width = 100+"%";
			var heightT = initY<e.clientY?initY:e.clientY;
			topDiv.style.height = heightT+"px";

			bottomDiv.style.left = 0;
			var topB = initY>e.clientY?initY:e.clientY;
			bottomDiv.style.top = topB+"px";
			bottomDiv.style.width = 100+"%";
			bottomDiv.style.bottom = 0;

			var leftS = initX<e.clientX?initX:e.clientX;
			snipDiv.style.left = leftS+"px";
			var widthS = Math.abs(e.clientX-initX);
			snipDiv.style.width = widthS+"px";
			var heightS = Math.abs(e.clientY-initY);
			snipDiv.style.height = heightS+"px";
			var topS = initY<e.clientY?initY:e.clientY;
			snipDiv.style.top = topS+"px";
		}

	}
}

//TODO: remove div elements
//TODO: change cursor
function removeDivsAndEvents() {
	document.body.removeChild(div);
	document.onmousedown = null;
	document.onmouseup = null;
	document.body.style.cursor = "auto";
}
