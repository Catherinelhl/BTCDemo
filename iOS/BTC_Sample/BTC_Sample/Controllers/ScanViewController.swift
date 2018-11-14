//
//  ScanViewController.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/13.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit

@objc protocol ScanViewControllerDelegate {
    @objc optional func didReciveScanResult(_ result:String)
}

class ScanViewController: UIViewController {

    weak var delegate:ScanViewControllerDelegate?
    
    private var sessionManager:AVCaptureSessionManager?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(barButtonSystemItem: .cancel, target: self, action: #selector(dismissVc))
        
        sessionManager = AVCaptureSessionManager(captureType: .AVCaptureTypeBoth, scanRect: .null, success: {[weak self] (result) in
            if let result = result {
                MyLog(result)
                self?.delegate?.didReciveScanResult?(result)
                self?.dismissVc()
            }
        })
        sessionManager?.showPreViewLayerIn(view: self.view)
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        sessionManager?.start()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        
        sessionManager?.stop()
    }
    
    
    @objc private func dismissVc() {
        self.dismiss(animated: true, completion: nil)
    }
    
    deinit {
        MyLog("移除扫描控制器")
    }

}
