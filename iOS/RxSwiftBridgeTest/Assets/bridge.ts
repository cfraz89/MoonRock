interface WKWebkitWindow extends Window {
    webkit: any;
}

declare var window: WKWebkitWindow;

class Bridge {
  push(data: any, stream="main") {
    window.webkit.messageHandlers.streams.postMessage({data: JSON.stringify(data), stream: stream});
  }
}

export default (new Bridge());
