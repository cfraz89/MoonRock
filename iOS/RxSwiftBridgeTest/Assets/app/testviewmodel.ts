/// <reference path="../bower_components/axios/axios.d.ts" />

import axios = require('axios');
import bridge from "bridge";

export class testViewModel {
  constructor() {
  }

  test(streamName: string): void {
    bridge.push({variable: "total victory"}, streamName);
  }

  getData(streamName: string): void {
      axios.get('http://jsonplaceholder.typicode.com/posts').then(function(postsResponse: axios.Response) {
        bridge.push ({data: postsResponse.data}, streamName);
      });
  }
}
