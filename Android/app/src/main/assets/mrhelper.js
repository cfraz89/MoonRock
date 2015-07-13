var MRHelper = (function () {
    function MRHelper() {
        this.window = window;
    }
    MRHelper.prototype.getModule = function (loadedName) {
        return this.window[loadedName];
    };
    MRHelper.prototype.loadModule = function (moduleName, loadedName) {
        var _this = this;
        System.import(moduleName).then(function (mod) {
            _this.window[loadedName] = mod.default;
            streamInterface.push(true, loadedName);
        });
    };
    MRHelper.prototype.portal = function (loadedName, subjectName) {
        var portal = new Rx.Subject();
        this.getModule(loadedName)[subjectName] = portal;
    };
    MRHelper.prototype.activatePortal = function (loadedName, subjectName, serializedInput) {
        var data = JSON.parse(serializedInput);
        var portal = (this.getModule(loadedName)[subjectName]);
        portal.onNext(data);
    };
    MRHelper.prototype.reversePortal = function (loadedName, subjectName) {
        var portal = (this.getModule(loadedName)[subjectName]);
        portal.subscribe(function (data) {
            reversePortalInterface.onNext(JSON.stringify(data), subjectName);
        });
    };
    MRHelper.prototype.portalsGenerated = function (loadedName) {
        this.getModule(loadedName).portalsGenerated();
    };
    MRHelper.prototype.portalsLinked = function (loadedName) {
        this.getModule(loadedName).portalsLinked();
    };
    return MRHelper;
})();
window.mrHelper = new MRHelper();
