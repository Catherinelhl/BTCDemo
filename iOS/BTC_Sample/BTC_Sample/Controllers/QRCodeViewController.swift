//
//  QRCodeViewController.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/14.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit

class QRCodeViewController: UIViewController {

    @IBOutlet weak var qrCodeImageView: UIImageView!
    
    @IBOutlet weak var textLabel: UILabel!
    
    private var text = ""
    
    init(text:String) {
        super.init(nibName: nil, bundle: nil)
        self.text = text
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
//        self.navigationItem.leftBarButtonItem = UIBarButtonItem.init(barButtonSystemItem: .cancel, target: self, action: #selector(dismissVc))
        
        self.textLabel.text = text
        
        self.qrCodeImageView.image = UIImage.createQRCode(size: 400, dataStr: self.text)
    }

    
    @objc private func dismissVc() {
        self.navigationController?.popViewController(animated: true)
    }

}
