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

    @IBOutlet weak var symbolLabel: UILabel!
    @IBOutlet weak var feesLabel: UILabel!
    @IBOutlet weak var txHashTextField: UITextField!
    
    private var balance:Decimal = 0
    
//    private var totalSend:Decimal = 0

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = .white

        myAddressLabel.text = "我的地址：" + myAddress
        symbolLabel.text = currencySymbol
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
        
        switch coinType {
        case .blockChain_btc_Main:
            createTx_1()
        default:
            createTx_0()
        }
        
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
    
    
    @IBAction func getTxDetailButtonAction(_ sender: UIButton) {
        getTxDetail()
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
                    switch coinType {
                    case .blockChain_btc_Main:
                        self.balance = NSDecimalNumber.init(value: json[myAddress]["final_balance"].intValue).decimalValue
                    default:
                        self.balance = NSDecimalNumber.init(value: json["final_balance"].intValue).decimalValue
                    }
                    
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
    
    // MARK: 创建交易0
    private func createTx_0() {
        guard reciveAddressTextField.text! != "" else {
            self.view.showToast("收款地址不能为空")
            return
        }
        
        let reciveAddress = reciveAddressTextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        guard BitcoinTool.validateAddress(reciveAddress) else {
            self.view.makeToast("收款地址格式有误")
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
        
        var fee:Int = 0
        switch coinType {
        case .bitcoinMain, .bitcoinTest:
            fee = (fees * rate).intValue
        case .ethMain, .ethTest:
            fee = gasPrice.intValue
        default:
            break
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
                    if let errorString = json["error"].string {
                        MyLog(errorString)
                        return
                    }
                    if let jsonDict = json.dictionaryObject ,
                        let toSignArr = json["tosign"].arrayObject as? [String] {
                        var signatures = [String]()
                        var publicKeys = [String]()
                        for toSign in toSignArr {
                            if let signature = BitcoinTool.sign(toSign, privateKey: myPrivateKey) {
                                signatures.append(signature)
                                publicKeys.append(myPublicKey)
                            }
                        }
                        self.sendTx_0(jsonDict, signatures, publicKeys)
                    }
                }catch let aError{
                    MyLog(aError)
                }
            case .failure(let aError):
                MyLog(aError)
            }
        }
        
    }
    // MARK: 发送交易0
    private func sendTx_0(_ jsonData:[String:Any],_ signature:[String],_ publicKey:[String]){
        ApiManagerProvider.request(.sendTx(txJson: jsonData, signatures: signature, publicKeys: publicKey)) { (result) in
            switch result {
            case .success(let response):
                do{
                    let value = try response.mapJSON()
                    self.jsonDataTextView.text = "\(value)"
                    let json = JSON(value)
                    if let errorString = json["errors"][0]["error"].string {
                        MyLog(errorString)
                    }else if let errorString = json["error"].string {
                        MyLog(errorString)
                    }else {
                        self.view.showToast("发送成功")
                    }
                }catch let aError{
                    MyLog(aError)
                }
            case .failure(let aError):
                MyLog(aError)
            }
        }
    }
    
    // MARK: 创建交易1
    private func createTx_1() {
        
        let reciveAddress = reciveAddressTextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        
        guard reciveAddress != "" else {
            self.view.showToast("收款地址不能为空")
            return
        }
        
        guard BitcoinTool.validateAddress(reciveAddress) else {
            self.view.makeToast("收款地址格式有误")
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
        
        
        ApiManagerProvider.request(.getUnspent(address: myAddress)) { (result) in
            switch result {
            case .success(let response):
                do {
                    let value = try response.mapJSON()
                    MyLog(value)
                    let json = JSON.init(value)
                    if let dataUtxo = BTCDataUnspentVO.deserialize(from: json.dictionaryObject),
                        let unspentArray = dataUtxo.unspent_outputs {
                        self.createUnspentTxs(unspentArray, amount: amount)
                    }
                    
                }catch let aError {
                    MyLog(aError)
                }
                
            case .failure(let aError):
                MyLog(aError)
            }
        }
    }
    
    private func createUnspentTxs(_ unspentArray:[BTCUspentVO], amount:Decimal) {
        var totalSend:Int64 = 0
        var utxos:[UnspentTransaction] = []
        for unspent in unspentArray {
            if let value = unspent.value?.int64Value ,
                let lockScriptData = unspent.script?.hashData ,
                let txHashData = unspent.tx_hash?.hashData ,
                let txOutputN = unspent.tx_output_n?.uint32Value {
                
                totalSend += value
                let unspentOutput = TransactionOutput(value: value, lockingScript: lockScriptData)
                let unspentOutpoint = TransactionOutPoint(hash: txHashData, index: txOutputN)
                let utxo = UnspentTransaction(output: unspentOutput, outpoint: unspentOutpoint)
                utxos.append(utxo)
                if totalSend >= NSDecimalNumber.init(decimal:(amount + fees) * rate).int64Value {
                    break
                }
            }
        }
        
        do {
            let toAddress = try AddressFactory.create(reciveAddressTextField.text!)
            let privateKey = try PrivateKey.init(wif: myPrivateKey)
            let unsignedTx = createUnsignedTx(toAddress: toAddress, privateKey: privateKey,totalSend: totalSend, amount: Int64((amount * rate).intValue), utxos: utxos)
            let tx = signTx(unsignedTx: unsignedTx, privateKey: privateKey)
            let txHex = tx.serialized().hex
            MyLog(txHex)
            pushTx(txhex: txHex)

        } catch let aError {
            MyLog(aError)
        }
        
        
    }
    
    private func createUnsignedTx(toAddress:Address,privateKey:PrivateKey,totalSend:Int64,amount:Int64,utxos:[UnspentTransaction]) -> UnsignedTransaction {
        
        let myAddress = privateKey.publicKey().toCashaddr()

        let fee = Int64((fees * rate).intValue)
        let returnAmount = totalSend - amount - fee
        
        let toPubKeyHash: Data = toAddress.data
        let myPubkeyHash: Data = myAddress.data
        
        let lockingScriptTo = Script.buildPublicKeyHashOut(pubKeyHash: toPubKeyHash)
        let lockingScriptReturn = Script.buildPublicKeyHashOut(pubKeyHash: myPubkeyHash)
        
        let toOutput = TransactionOutput(value: amount, lockingScript: lockingScriptTo)
        let myOutput = TransactionOutput(value: returnAmount, lockingScript: lockingScriptReturn)
        
        let unsignedInputs = utxos.map { TransactionInput(previousOutput: $0.outpoint, signatureScript: Data(), sequence: UInt32.max) }
        var outputs = [toOutput]
        if returnAmount > 0 {
            outputs.append(myOutput)
        }
        let tx = Transaction(version: 1, inputs: unsignedInputs, outputs: outputs, lockTime: 0)
        return UnsignedTransaction(tx: tx, utxos: utxos)
    }
    
    private func signTx(unsignedTx:UnsignedTransaction,privateKey:PrivateKey) -> Transaction{
        var inputsToSign = unsignedTx.tx.inputs
        var transactionToSign: Transaction {
            return Transaction(version: unsignedTx.tx.version, inputs: inputsToSign, outputs: unsignedTx.tx.outputs, lockTime: unsignedTx.tx.lockTime)
        }
        
        // Signing
        let hashType = SighashType.BTC.ALL
        for (i, utxo) in unsignedTx.utxos.enumerated() {

            let sighash: Data = transactionToSign.signatureHash(for: utxo.output, inputIndex: i, hashType: hashType)
            if let signature = try? Crypto.sign(sighash, privateKey: privateKey) {
                let txin = inputsToSign[i]
                let pubkey = privateKey.publicKey()
                
                let unlockingScript = Script.buildPublicKeyUnlockingScript(signature: signature, pubkey: pubkey, hashType: hashType)
                
                inputsToSign[i] = TransactionInput(previousOutput: txin.previousOutput, signatureScript: unlockingScript, sequence: txin.sequence)
            }
        }
        
        return transactionToSign
    }
    
    // MARK: 发布交易1
    private func pushTx(txhex:String) {
        ApiManagerProvider.request(.pushTx(txHex: txhex)) { (result) in
            switch result {
            case .success(let response):
                do{
                    if let valueString = String.init(data: response.data, encoding: .utf8) {
                        MyLog(valueString)
                        self.jsonDataTextView.text = "\(valueString)"
                    }
                    let value = try JSON.init(data: response.data)
                    self.jsonDataTextView.text = "\(value.rawValue)"
                    MyLog(value)
                }catch let aError {
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
                MyLog(JSON(response.data))
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
    
    // MARK: 通过交易hash获取交易详情
    private func getTxDetail() {
        let txHash = txHashTextField.text!.trimmingCharacters(in: .whitespacesAndNewlines)
        guard txHash != "" else {
            self.view.showToast("交易hash值不能为空")
            return
        }
        
        ApiManagerProvider.request(.getTxDetail(txHash: txHash)) { (result) in
            switch result {
            case .success(let response):
                do{
                    let value = try response.mapJSON()
                    MyLog(value)
                    self.jsonDataTextView.text = "\(value)"
                }catch let aError{
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
