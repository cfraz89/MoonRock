/// <reference path="../bower_components/axios/axios.d.ts" />
/// <reference path="../bower_components/rxjs/ts/rx.d.ts" />
var axios = require('axios');
var appModule = (function () {
    function appModule() {
    }
    appModule.prototype.portalsGenerated = function () {
        var _this = this;
        this.addPressed.subscribe(function (event) {
        });
        axios.get('http://jsonplaceholder.typicode.com/posts').then(function (response) {
            _this.postsResponse.onNext({ data: response.data });
        });
    };
    return appModule;
})();
exports.appModule = appModule;
exports.default = (new appModule());
