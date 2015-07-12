declare var System: any;
declare var streamInterface: any;
declare var reversePortalInterface: any;

class MRHelper {
  constructor() {
  }

  loadModule(moduleName: string, loadedName: string) {
    System.import(moduleName).then((mod: any) => {
      (<any>window)[loadedName] = mod.default
      streamInterface.push(true, loadedName)
      });
  }

  portal(loadedName: string, subjectName: string) {
    var portal = new Rx.Subject<any>();
    window[loadedName][subjectName] = portal;
  }

  activatePortal(loadedName: string, subjectName: string, serializedInput: string) {
    var data = JSON.parse(serializedInput)
    var portal = <Rx.Subject<any>>(window[loadedName][subjectName])
    portal.onNext(data)
  }

  reversePortal(loadedName: string, subjectName: string) {
    var portal = new Rx.Subject<any>();
    window[loadedName][subjectName] = portal;
    portal.subscribe((data: any)=>{
        reversePortalInterface.onNext(JSON.stringify(data), subjectName)
      })
  }

  portalsGenerated(loadedName: string) {
    window[loadedName].portalsGenerated()
  }
}

window['mrHelper'] = new MRHelper()
