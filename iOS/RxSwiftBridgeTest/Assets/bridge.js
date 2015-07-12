var Bridge = (function () {
    function Bridge() {
    }
    Bridge.prototype.push = function (data, stream) {
        if (stream === void 0) { stream = "main"; }
        window.webkit.messageHandlers.streams.postMessage({ data: JSON.stringify(data), stream: stream });
    };
    return Bridge;
})();
exports.default = (new Bridge());
