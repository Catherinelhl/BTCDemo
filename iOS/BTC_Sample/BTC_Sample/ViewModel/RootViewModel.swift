//
//  RootViewModel.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/22.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit
import RxCocoa
import RxSwift
import SwiftyJSON
import BitcoinKit
import Moya

class RootViewModel {

    
    func createAndSendTx(with amount:Decimal, reciveAddress:String) -> Single<Any>{
    
        return Single<Any>.create(subscribe: { (single) -> Disposable in
            var pushRequest:Cancellable?
            let request = ApiManagerProvider.request(.getUnspent(address: myAddress)) { (result) in
                switch result {
                case .success(let response):
                    do {
                        let value = try response.mapJSON()
                        MyLog(value)
                        let json = JSON.init(value)
                        if let dataUtxo = BTCDataUnspentVO.deserialize(from: json.dictionaryObject),
                            let unspentArray = dataUtxo.unspent_outputs {
                            
                            pushRequest = try self.createUnspentTxs(unspentArray, amount: amount, reciveAddress: reciveAddress, completion: { (result) in
                                switch result {
                                case .success(let response):
                                    if let value = try? JSON.init(data: response.data) {
                                        MyLog(value)
                                    }else if let valueString = String.init(data: response.data, encoding: .utf8) {
                                        MyLog(valueString)
                                    }else {
                                        single(.error(MyError.normalError("数据解析失败")))
                                    }
                                case .failure(let aError):
                                    MyLog(aError)
                                    single(.error(aError))
                                }

                            })
                        }else {
                            single(.error(MyError.normalError("获取解析utxo失败")))
                        }
                        
                    }catch let aError {
                        MyLog(aError)
                        single(.error(aError))
                    }
                    
                case .failure(let aError):
                    MyLog(aError)
                    single(.error(aError))
                }
            }
            
            return Disposables.create {
                request.cancel()
                pushRequest?.cancel()
            }
        })
        
    }
    
    private func createUnspentTxs(_ unspentArray:[BTCUspentVO], amount:Decimal,reciveAddress:String, completion: @escaping Completion) throws -> Cancellable {
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
            let toAddress = try AddressFactory.create(reciveAddress)
            let privateKey = try PrivateKey.init(wif: myPrivateKey)
            let unsignedTx = createUnsignedTx(toAddress: toAddress, privateKey: privateKey,totalSend: totalSend, amount: Int64((amount * rate).intValue), utxos: utxos)
            let tx = try signTx(unsignedTx: unsignedTx, privateKey: privateKey)
            let txHex = tx.serialized().hex
            MyLog(txHex)
            return pushTx(txhex: txHex, completion: { (result) in
                completion(result)
            })
            
        } catch let aError {
            MyLog(aError)
            throw aError
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
    
    private func signTx(unsignedTx:UnsignedTransaction,privateKey:PrivateKey) throws -> Transaction{
        var inputsToSign = unsignedTx.tx.inputs
        var transactionToSign: Transaction {
            return Transaction(version: unsignedTx.tx.version, inputs: inputsToSign, outputs: unsignedTx.tx.outputs, lockTime: unsignedTx.tx.lockTime)
        }
        
        // Signing
        let hashType = SighashType.BTC.ALL
        for (i, utxo) in unsignedTx.utxos.enumerated() {
            
            let sighash: Data = transactionToSign.signatureHash(for: utxo.output, inputIndex: i, hashType: hashType)
            do {
                let signature = try Crypto.sign(sighash, privateKey: privateKey)
                let txin = inputsToSign[i]
                let pubkey = privateKey.publicKey()
                
                let unlockingScript = Script.buildPublicKeyUnlockingScript(signature: signature, pubkey: pubkey, hashType: hashType)
                
                inputsToSign[i] = TransactionInput(previousOutput: txin.previousOutput, signatureScript: unlockingScript, sequence: txin.sequence)
            }catch let aError{
                MyLog(aError)
                throw aError
            }
        }
        
        return transactionToSign
    }
    
    // MARK: 发布交易1
    private func pushTx(txhex:String, completion: @escaping Completion) -> Cancellable{
        return ApiManagerProvider.request(.pushTx(txHex: txhex)) { (result) in
            completion(result)
        }
    }
    
}
