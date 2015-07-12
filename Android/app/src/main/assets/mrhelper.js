var MRHelper = (function () {
    function MRHelper() {
    }
    MRHelper.prototype.loadModule = function (moduleName, loadedName) {
        System.import(moduleName).then(function (mod) {
            window[loadedName] = mod.default;
            streamInterface.push(true, loadedName);
        });
    };
    MRHelper.prototype.portal = function (loadedName, subjectName) {
        var portal = new Rx.Subject();
        window[loadedName][subjectName] = portal;
    };
    MRHelper.prototype.activatePortal = function (loadedName, subjectName, serializedInput) {
        var data = JSON.parse(serializedInput);
        var portal = (window[loadedName][subjectName]);
        portal.onNext(data);
    };
    MRHelper.prototype.reversePortal = function (loadedName, subjectName) {
        var portal = new Rx.Subject();
        window[loadedName][subjectName] = portal;
        portal.subscribe(function (data) {
            reversePortalInterface.onNext(JSON.stringify(data), subjectName);
        });
    };
    MRHelper.prototype.portalsGenerated = function (loadedName) {
        window[loadedName].portalsGenerated();
    };
    return MRHelper;
})();
window['mrHelper'] = new MRHelper();
