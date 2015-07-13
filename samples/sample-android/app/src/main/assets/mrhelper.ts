declare var System: any
declare var streamInterface: any
declare var reversePortalInterface: any


class MRHelper {
  window: any

  constructor() {
    this.window = <any>window
  }

  getModule(loadedName: string): any {
    return this.window[loadedName]
  }

  loadModule(moduleName: string, loadedName: string) {
    System.import(moduleName).then((mod: any) => {
      this.window[loadedName] = mod.default
      streamInterface.push(true, loadedName)
    });
  }

  portal(loadedName: string, subjectName: string) {
    var portal = new Rx.Subject<any>();
    this.getModule(loadedName)[subjectName] = portal;
  }

  activatePortal(loadedName: string, subjectName: string, serializedInput: string) {
    var data = JSON.parse(serializedInput)
    var portal = <Rx.Subject<any>>(this.getModule(loadedName)[subjectName])
    portal.onNext(data)
  }

  reversePortal(loadedName: string, subjectName: string) {
    var portal = <Rx.Observable<any>>(this.getModule(loadedName)[subjectName]);
    portal.subscribe((data: any)=>{
        reversePortalInterface.onNext(JSON.stringify(data), subjectName)
      })
  }
  portalsGenerated(loadedName: string) {
    this.getModule(loadedName).portalsGenerated()
  }
  portalsLinked(loadedName: string) {
    this.getModule(loadedName).portalsLinked()
  }
}

(<any>window).mrHelper = new MRHelper()
