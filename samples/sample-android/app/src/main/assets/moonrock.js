var MoonRock = (function () {
    function MoonRock() {
    }
    MoonRock.prototype.push = function (data, stream) {
        streamInterface.push(JSON.stringify(data), stream);
    };
    return MoonRock;
})();
exports["default"] = (new MoonRock());
