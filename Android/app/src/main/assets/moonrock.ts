declare var streamInterface: any

class MoonRock {

  push(data: any, stream: string) {
    streamInterface.push(JSON.stringify(data), stream)
  }
}

export default (new MoonRock())
