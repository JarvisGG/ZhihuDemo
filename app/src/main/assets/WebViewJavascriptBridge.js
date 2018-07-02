(function() {
    if (checkBridge()) {
        return;
    }

    let messagingIframe;
    let sendMessageQueue = [];
    let receiveMessageQueue = [];

    let responseCallbacks = {};
    let messageHandlers = {};

    let uniqueId = 1;

    const CUSTOM_PROTOCOL_SCHEME = 'yy';
    const QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';

    let doc = document;
    function _createQueueReadyIframe(doc) {
        messagingIframe = doc.createElement('iframe');
        messagingIframe.style.display = 'none';
        doc.documentElement.appendChild(messagingIframe);
    }
    _createQueueReadyIframe(doc);
    
    let WebViewJavascriptBridge = window.WebViewJavascriptBridge = {
        init: init,
        send: send,
        registerHandler: registerHandler,
        callHandler: callHandler,
        _fetchQueue: _fetchQueue,
        _handleMessageFromNative: _handleMessageFromNative
    };

    let readyEvent = doc.createEvent('Events');
    readyEvent.initEvent('WebViewJavascriptBridgeReady');
    readyEvent.bridege = WebViewJavascriptBridge;
    doc.dispatchEvent(readyEvent);

    function init(messageHandler) {
        if (WebViewJavascriptBridge._messageHandler) {
            throw new Error('WebViewJavascriptBridge.init called twice');
        }
        WebViewJavascriptBridge._messageHandler = messageHandler;
        let receiveMessages = receiveMessageQueue;
        receiveMessageQueue = null;
        for (let i = 0; i < receiveMessages.length; i++) {
            _dispatchMessageFromNative(receiveMessages[i]);
        }
    }

    function send(data, responseCallback) {
        _doSend({
            data: data
        }, responseCallback);
    }

    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    function callHandler(handlerName, data, responseCallback) {
        _doSend({
            handlerName: handlerName,
            data: data
        }, responseCallback);
    }

    function _fetchQueue() {
        let messageQueueString = JSON.stringify(sendMessageQueue);
        sendMessageQueue = [];
        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
    }
    
    function _handleMessageFromNative(messageJSON) {
        console.log(messageJSON);
        if (receiveMessageQueue && receiveMessageQueue.length > 0) {
            receiveMessageQueue.push(messageJSON);
        } else {
            _dispatchMessageFromNative(messageJSON);
        }
    }

    function _dispatchMessageFromNative(messageJSON) {
        setTimeout(function() {
            let message = JSON.parse(messageJSON);
            let responseCallback;
            if (message.responseId) {
                responseCallback = responseCallbacks[message.responseId];
                if (!responseCallback) {
                    return ;
                }
                responseCallback(message.responseData);
                delete responseCallbacks[message.responseId];
            } else {
                if (message.callbackId) {
                    let callbackResponseId = message.callbackId;
                    responseCallback = function(responseData) {
                        _doSend({
                            responseId: callbackResponseId,
                            responseData: responseData
                        });
                    };
                }

                let handler = WebViewJavascriptBridge._messageHandler;
                if (message.handlerName) {
                    handler = messageHandlers[message.handlerName];
                }
                try {
                    handler(message.data, responseCallback);
                } catch (exception) {
                    if (typeof console != 'undefined') {
                        console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
                    }
                }
            }
        });
    }

    function _doSend(message, responseCallback) {
        if (responseCallback) {
            let callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = responseCallback;
            message.callbackId = callbackId;
        }
        sendMessageQueue.push(message);
        messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + "://" + QUEUE_HAS_MESSAGE;
    }
})();

function checkBridge() {
    return window.WebViewJavascriptBridge;
}
