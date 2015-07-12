//
//  ScriptModule.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 8/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import Foundation
import EVReflection
import RxSwift

class ScriptModule {
    let bridge: JSBridge
    let loadedName: String
    
    init(bridge: JSBridge, loadedName: String) {
        self.bridge = bridge
        self.loadedName = loadedName
    }
    
     func function<T: EVObject>(function: String, streamName: String, streamType: T) -> Observable<T> {
        let formattedScript = String(format: "%@.%@(\"%@\")", self.loadedName, function, streamName)
        println (formattedScript)
        return self.bridge.run(formattedScript, streamName: streamName, streamType: streamType)
    }
}