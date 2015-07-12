//
//  ViewController.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 7/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import UIKit
import RxSwift
import RxCocoa

class ViewController: UIViewController {
    @IBOutlet weak var InstaLabel: UILabel!
    @IBOutlet weak var TableView: UITableView!
    var bridge: JSBridge;
    var module: ScriptModule?
    var disposeBag = DisposeBag()
    
    let Module: String = "app/testviewmodel"
    let ModuleClass: String = "testViewModel"
    let ModuleLocalName: String = "viewModel"
    let InstaTest: String = "instatest"
    
    var postsDataSource: PostsDataSource

    required init(coder aDecoder: NSCoder) {
        self.module = nil
        self.bridge = JSBridge()
        self.postsDataSource = PostsDataSource(posts:[])
        super.init(coder: aDecoder)
        
        self.bridge.readyObservable
            >- subscribeNext { ready in
                self.module = self.bridge.loadModule(self.Module, className: self.ModuleClass, loadedName: self.ModuleLocalName)
            }
            >- disposeBag.addDisposable
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.TableView.dataSource = self.postsDataSource
    }

    @IBAction func Instawin(sender: AnyObject) {
        self.module!.function("test", streamName: InstaTest, streamType: TestModel())
            >- subscribeNext { data in
                self.InstaLabel.text = data.variable
            }
            >- disposeBag.addDisposable
    }
    
    @IBAction func Netwin(sender: AnyObject) {
        self.module!.function("getData", streamName: JSBridge.MainStream, streamType: PostList())
            >- subscribeNext { posts in
                self.postsDataSource.posts = posts.data
                self.TableView.reloadData()
            }
            >- disposeBag.addDisposable
    }
}