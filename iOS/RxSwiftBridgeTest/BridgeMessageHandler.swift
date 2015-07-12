//
//  BridgeMessageHandler.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 7/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import Foundation
import WebKit
import RxSwift
import EVReflection

class BridgeMessageHandler: NSObject, WKScriptMessageHandler {

    var streams: [String: BridgeMessageStreamProtocol]
    
    override init() {
        streams = [String: BridgeMessageStreamProtocol]()
        super.init()
    }
    
    func streamFor<T: EVObject>(streamName: String, streamClass: T) -> BridgeMessageStream<T> {
        var stream = self.streams[streamName];
        if (stream == nil) {
            stream = BridgeMessageStream<T>()
            self.streams[streamName] = stream
        }
        return stream as! BridgeMessageStream<T>
    }
    
    func userContentController(userContentController: WKUserContentController, didReceiveScriptMessage message: WKScriptMessage) {
        let bridgeMessage = message.body as! NSDictionary
        let data = bridgeMessage["data"]
        let streamName = bridgeMessage["stream"] as! String
        let stream = self.streams[streamName] as BridgeMessageStreamProtocol?
        stream!.push(data as! String)
    }
}
