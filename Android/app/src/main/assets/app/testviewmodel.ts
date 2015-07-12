/// <reference path="../bower_components/axios/axios.d.ts" />
/// <reference path="../bower_components/rxjs/ts/rx.d.ts" />

import axios = require('axios');

export class testViewModel implements MoonRockPortals {
  addPressed: Rx.Observable<{input1: number, input2: number}>
  addResponse: Rx.Observer<number>
  postsResponse: Rx.Observer<{data: any}>

  portalsGenerated() {
    this.addPressed.subscribe(add=>{
      this.addResponse.onNext(add.input1 + add.input2)
    })

    axios.get('http://jsonplaceholder.typicode.com/posts').then((response: axios.Response) => {
      this.postsResponse.onNext({data: response.data})
    })
  }
}

export default (new testViewModel())
