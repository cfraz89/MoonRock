/// <reference path="../bower_components/axios/axios.d.ts" />
/// <reference path="../bower_components/rxjs/ts/rx.d.ts" />

import axios = require('axios')

export class appModule implements MoonRockPortals {
  addPressed: Rx.Observable<any>
  add1Text: Rx.Observable<any>
  add2Text: Rx.Observable<any>

  addResponse: Rx.Observer<number>
  postsResponse: Rx.Observer<{data: any}>

  portalsGenerated() {
    this.addPressed.subscribe(event=>{

    })

    axios.get('http://jsonplaceholder.typicode.com/posts').then((response: axios.Response) => {
      this.postsResponse.onNext({data: response.data})
    })
  }
}

export default (new appModule())
