/// <reference path="../bower_components/axios/axios.d.ts" />
/// <reference path="../bower_components/rxjs/ts/rx.d.ts" />

import axios = require('axios');
import moonRock from "moonrock";

export class testViewModel {

  constructor() {
  }

  test(streamKey: string): any {
    return {variable: "total victory"}
  }

  getData(streamKey: string): void {
      axios.get('http://jsonplaceholder.typicode.com/posts').then(function(postsResponse: axios.Response) {
        moonRock.push ({data: postsResponse.data}, streamKey)
      })
  }
}

export default (new testViewModel())
