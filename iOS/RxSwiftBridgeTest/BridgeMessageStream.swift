//
//  BridgeMessageStream.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 8/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import Foundation
import RxSwift;
import EVReflection;

class BridgeMessageStream<T: EVObject>: BridgeMessageStreamProtocol {
    let subject: PublishSubject<T>
    
    init() {
        subject = PublishSubject<T>()
    }
    
    func push(object: String) {
        var deserializedObject = T(json: object)
        sendNext(subject, deserializedObject)
    }
}