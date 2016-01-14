function connectWebViewJavascriptBridge(callback) {
    if (window.WebViewJavascriptBridge) {
        callback(WebViewJavascriptBridge);
    } else {
        document.addEventListener('WebViewJavascriptBridgeReady', function() {
            callback(WebViewJavascriptBridge)
        }, false);
    }
};

$(document).ready(function() {
    connectWebViewJavascriptBridge(function(bridge) {

        $("#getTokenBtn").click(function() {
            bridge.send({
                "cmd": "getToken",
                "data":{}
            }, function responseCallback(responseData) {
                $("#logger")[0].innerHTML += responseData + "<br>";
            });
        });

        $("#addPicBtn").click(function() {
            bridge.send({
                "cmd": "getPictures",
                "data": {
                    "count": 9
                }
            }, function responseCallback(responseData) {
                var imgs = JSON.parse(responseData).data.imgs;
                for (var i = 0; i < imgs.length; i++) {
                    $('#picList').append('<li><img src="data:image/jpeg;base64,' + imgs[i] + '" alt="image" width="50" height="50"></li>');
                }
            });
        });
    });
});
