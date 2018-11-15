//
//  RootViewController.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/13.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit
import SwiftyJSON
import Alamofire
import Toast_Swift
import BitcoinKit

class RootViewController: UIViewController {
    @IBOutlet weak var myAddressLabel: UILabel!
    @IBOutlet weak var reciveAddressLabel: UILabel!
    
    @IBOutlet weak var reciveAddressTextField: UITextField!
    @IBOutlet weak var balanceLabel: UILabel!
    @IBOutlet weak var sendAmountTextField: UITextField!
    @IBOutlet weak var jsonDataTextView: UITextView!
    @IBOutlet weak var feesLabel: UILabel!
    
    private var balance:Decimal = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = .white

        myAddressLabel.text = "我的地址：" + myAddress
        feesLabel.text = "手续费：\(fees) " + currencySymbol
        
        myAddressLabel.adjustsFontSizeToFitWidth = true
        feesLabel.adjustsFontSizeToFitWidth = true
        
        getBalnce()
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
    @IBAction func getBalanceButtonAction(_ sender: UIButton) {
        getBalnce()
        
    }
    
    @IBAction func sendTxButtonAction(_ sender: UIButton) {
        
        createTx()
    }
    
    @IBAction func getTxRecordButtonAction(_ sender: Any) {
        
        getTxRecord()
    }
    
    @IBAction func qrCodeButtonAction(_ sender: Any) {
        let qrCodeVc = UINavigationController(rootViewController: QRCodeViewController.init(text: myAddress))
        self.present(qrCodeVc, animated: true, completion: nil)
    }
    @IBAction func scanButtonAction(_ sender: UIButton) {
        
        let scanVc = ScanViewController()
        scanVc.delegate = self
        
        self.present(UINavigationController(rootViewController: scanVc), animated: true, completion: nil)
    }
    
    
    
    // MARK: - Api request
    // MARK: 获取余额
    private func getBalnce() {
        ApiManagerProvider.request(.getBalance(address: myAddress)) { (result) in
            switch result {
            case .success(let response):
                do{
                    let value = try response.mapJSON()
                    let json = JSON(value)
                    self.balance = Decimal(json["final_balance"].doubleValue)
                    self.balanceLabel.text = "\(self.balance / rate)" + " " + currencySymbol
                    
                    self.jsonDataTextView.text = "\(value)"
                    
                }catch let aError {
                    MyLog(aError)
                }
            case .failure(let aError):
                MyLog(aError.errorDescription)
            }
        }
    }
    
    // MARK: 创建交易
    private func createTx() {
        guard reciveAddressTextField.text! != "" else {
            self.view.showToast("收款地址不能为空")
            return
        }
        guard let amount = Decimal(string:sendAmountTextField.text!),amount > 0 else {
            self.view.showToast("发送金额有误")
            return
        }
        
        guard amount + fees <= balance else {
            self.view.showToast("余额不足")
            return
        }
        
        let reciveAddress = reciveAddressTextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        guard BitcoinTool.validateAddress(reciveAddress) else {
            self.view.makeToast("收款地址格式有误")
            return
        }
        
        var fee:Int
        switch coinType {
        case .bitcoinMain, .bitcoinTest:
            fee = (fees * rate).intValue
        case .ethMain, .ethTest:
            fee = gasPrice.intValue
        }
        
        ApiManagerProvider.request(.createTx(fromAddress:myAddress,toAddress: reciveAddressTextField.text!, amount: (amount * rate).intValue, fees:fee )) { (result) in
            switch result {
            case .success(let response):
                do{
                    let value = try response.mapJSON()
                    self.jsonDataTextView.text = "\(value)"
                    let json = JSON(value)
                    if let errorString = json["errors"].arrayObject {
                        MyLog(errorString)
                        return
                    }
                    if let jsonDict = json.dictionaryObject ,
                        let toSign = json["tosign"][0].string ,
                        let signature = BitcoinTool.sign(toSign, privateKey: myPrivateKey) {

                        self.sendTx(jsonDict, signature, myPublicKey)
                    }
                }catch let aError{
                    MyLog(aError)
                }
            case .failure(let aError):
                MyLog(aError)
            }
        }
        
    }
    // MARK: 发送交易
    private func sendTx(_ jsonData:[String:Any],_ signature:String,_ publicKey:String){
        ApiManagerProvider.request(.sendTx(txJson: jsonData, signature: signature, publicKey: publicKey)) { (result) in
            switch result {
            case .success(let response):
                do{
                    let value = try response.mapJSON()
                    self.jsonDataTextView.text = "\(value)"
                    let json = JSON(value)
                    if let errorString = json["errors"][0]["error"].string {
                        MyLog(errorString)
                    }else {
                        self.view.showToast("发送成功")
                        self.getBalnce()
                    }
                }catch let aError{
                    MyLog(aError)
                }
            case .failure(let aError):
                MyLog(aError)
            }
        }
    }
    
    // MARK: 获取交易记录
    private func getTxRecord() {
        ApiManagerProvider.request(.getTxRecord(address: myAddress)) { (result) in
            switch result {
            case .success(let response):
                do{
                    let value = try response.mapJSON()
                    self.jsonDataTextView.text = "\(value)"
                }catch let aError {
                    MyLog(aError)
                }
            case .failure(let aError):
                MyLog(aError)
            }
        }
    }
}


extension RootViewController : ScanViewControllerDelegate {
    func didReciveScanResult(_ result: String) {

        reciveAddressTextField.text = result
    }
}


extension UIView {
    func showToast(_ message:String?) {
        self.makeToast(message,position:.center)
    }
}
