//
//  RootViewController.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/13.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit

class RootViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = .white

        
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }

}
