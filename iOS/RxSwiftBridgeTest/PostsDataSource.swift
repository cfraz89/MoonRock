//
//  PostsDataSource.swift
//  RxSwiftBridgeTest
//
//  Created by Chris Fraser on 8/07/2015.
//  Copyright (c) 2015 Chris Fraser. All rights reserved.
//

import Foundation
import UIKit

class PostsDataSource : NSObject, UITableViewDataSource {
    var posts: [Post]

    init(posts: [Post]) {
        self.posts = posts
    }

    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.posts.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCellWithIdentifier("post", forIndexPath: indexPath) as! PostCell
        let row = indexPath.row

        cell.TitleLabel.text = self.posts[row].title
        cell.BodyLabel.text = self.posts[row].body
        return cell
    }
}
