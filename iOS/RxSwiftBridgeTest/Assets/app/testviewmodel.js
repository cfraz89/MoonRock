/// <reference path="../bower_components/axios/axios.d.ts" />
var axios = require('axios');
var bridge_1 = require("bridge");
var testViewModel = (function () {
    function testViewModel() {
    }
    testViewModel.prototype.test = function (streamName) {
        bridge_1.default.push({ variable: "total victory" }, streamName);
    };
    testViewModel.prototype.getData = function (streamName) {
        axios.get('http://jsonplaceholder.typicode.com/posts').then(function (postsResponse) {
            bridge_1.default.push({ data: postsResponse.data }, streamName);
        });
    };
    return testViewModel;
})();
exports.testViewModel = testViewModel;
