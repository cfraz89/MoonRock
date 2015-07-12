/// <reference path="../bower_components/axios/axios.d.ts" />
/// <reference path="../bower_components/rxjs/ts/rx.d.ts" />
var axios = require('axios');
var testViewModel = (function () {
    function testViewModel() {
    }
    testViewModel.prototype.portalsGenerated = function () {
        var _this = this;
        this.addPressed.subscribe(function (add) {
            _this.addResponse.onNext(add.input1 + add.input2);
        });
        axios.get('http://jsonplaceholder.typicode.com/posts').then(function (response) {
            _this.postsResponse.onNext({ data: response.data });
        });
    };
    return testViewModel;
})();
exports.testViewModel = testViewModel;
exports["default"] = (new testViewModel());
