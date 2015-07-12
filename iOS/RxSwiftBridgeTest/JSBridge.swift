//
//  JSBridge.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 7/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import Foundation
import WebKit
import EVReflection
import RxSwift

class JSBridge {
    class var MainStream: String {
        get {
            return "main"
        }
    }
    
    let webView: WKWebView
    let messageHandler: BridgeMessageHandler
    let readySubject: PublishSubject<Any>
    
    init () {
        readySubject = PublishSubject<Any>()
        var contentController = WKUserContentController()
        self.messageHandler = BridgeMessageHandler()
        contentController.addScriptMessageHandler(messageHandler, name: "streams")
        var configuration = WKWebViewConfiguration()
        configuration.userContentController = contentController
        self.webView = WKWebView(frame: CGRect(), configuration: configuration)
        //var url = NSBundle.mainBundle().URLForResource("Assets/bridge", withExtension: "html");
        var url = NSURL(string: "http://localhost:8082/bridge.html")
        self.webView.navigationDelegate = BridgeNavigationDelegate(readySubject: readySubject)
        self.webView.loadRequest(NSURLRequest(URL: url!))
        self.webView.configuration.userContentController = contentController
        self.webView.reloadFromOrigin()
        
    }
    
    func run<T: EVObject>(script: String, streamName: String, streamType: T) -> Observable<T> {
        let stream = messageHandler.streamFor(streamName, streamClass: streamType);
        self.webView.evaluateJavaScript(script, completionHandler: nil)
        return stream.subject;
    }
    
    func loadModule(module: String, className: String, loadedName: String) -> ScriptModule {
        let script = String(format: "System.import('%@').then(function(module) {window.%@ = new module.%@();});", module, loadedName, className)
        self.webView.evaluateJavaScript(script, completionHandler: nil)
        return ScriptModule(bridge: self, loadedName: loadedName)
    }
    
    var readyObservable: Observable<Any> {
        get {
            return readySubject
        }
    }
}