//
//  BridgeNavigationDelegate.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 8/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import Foundation
import WebKit
import RxSwift

class BridgeNavigationDelegate : NSObject, WKNavigationDelegate {
    let readySubject: PublishSubject<Any>
    
    init(readySubject: PublishSubject<Any> ) {
        self.readySubject = readySubject
        super.init()
        //hack, the webview sucks
        var timer = NSTimer.scheduledTimerWithTimeInterval(100, target: self, selector: Selector("what"), userInfo: nil, repeats: true)

    }
    
    func webView(webView: WKWebView, didFinishNavigation navigation: WKNavigation!) {
        complete()
    }
    
    func complete() {
        sendNext(self.readySubject, true);
        sendCompleted(self.readySubject)

    }
    
    func what() {
        
    }
}