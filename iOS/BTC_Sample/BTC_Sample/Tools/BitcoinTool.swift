//
//  BitcoinTool.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/12.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import Foundation
import BitcoinKit

class BitcoinTool {
    
    /// 生成私钥
    ///
    /// - Returns: 私钥信息
    class func generateBTCKey() -> BTCKeyStore {
        let privateKey = PrivateKey.init(network: coinType == .bitcoinTest ? .testnet : .mainnet, isPublicKeyCompressed: false)
        let wallet = Wallet.init(privateKey: privateKey)
        MyLog("privateKey_WIF:\(wallet.privateKey.toWIF())")
        MyLog("publicKey:\(wallet.publicKey.description)")
        MyLog("address:\(wallet.publicKey.toCashaddr().base58)")
        let keyStore = BTCKeyStore()
        keyStore.privateKey = wallet.privateKey.toWIF()
        keyStore.publicKey = wallet.publicKey.description
        keyStore.address = wallet.publicKey.toCashaddr().base58
        return keyStore
    }
    
    /// 根据私钥生成地址
    ///
    /// - Parameter wif: 私钥
    /// - Returns: 地址&私钥信息
    class func generateBTCKeyBy(privateKey wif:String) -> BTCKeyStore? {
        do {
            let privateKey = try PrivateKey.init(wif: wif)
            let wallet = Wallet.init(privateKey: privateKey)
            MyLog("privateKey_WIF:\(wallet.privateKey.toWIF())")
            MyLog("publicKey:\(wallet.publicKey.description)")
            MyLog("address:\(wallet.publicKey.toCashaddr().base58)")
            let keyStore = BTCKeyStore()
            keyStore.privateKey = wallet.privateKey.toWIF()
            keyStore.publicKey = wallet.publicKey.description
            keyStore.address = wallet.publicKey.toCashaddr().base58
            
            return keyStore
        } catch let error {
            MyLog(error)
            return nil
        }
    }
    
    class func generateETHKey() -> ETHKeyStore {
        // 创建钱包
        let privateKey = PrivateKey.init(network: .mainnet, isPublicKeyCompressed: false)
        let wallet = Wallet.init(privateKey: privateKey)
        MyLog("privateKey:\(wallet.privateKey.description)")
        MyLog("publicKey:\(wallet.publicKey.description)")
        MyLog("address:\(wallet.publicKey.raw.ethAddressString ?? "")")
        
        let keyStore = ETHKeyStore()
        keyStore.privateKey = wallet.privateKey.description
        keyStore.publicKey = wallet.publicKey.description
        keyStore.address = wallet.publicKey.raw.ethAddressString
        return keyStore
    }
    
    class func generateETHKeyBy(_ privatKey:String) -> ETHKeyStore? {
        
        guard let privateKeyData = Data(btcHex: privatKey) else {
            return nil
        }
        
        let privateKey = PrivateKey.init(data: privateKeyData, network: .mainnet, isPublicKeyCompressed: false)
        let wallet = Wallet.init(privateKey: privateKey)
        MyLog("privateKey:\(wallet.privateKey.description)")
        MyLog("publicKey:\(wallet.publicKey.description)")
        MyLog("address:\(wallet.publicKey.raw.ethAddressString ?? "")")
        
        let keyStore = ETHKeyStore()
        keyStore.privateKey = wallet.privateKey.description
        keyStore.publicKey = wallet.publicKey.description
        keyStore.address = wallet.publicKey.raw.ethAddressString
        return keyStore
        
    }
    
    class func validateAddress(_ address:String) -> Bool{
        
        
        return true
    }
    
    /// 签名交易hash字符串
    ///
    /// - Parameters:
    ///   - message: hash字符串
    ///   - wif: 私钥字符串
    /// - Returns: 签名后字符串
    class func sign(_ message:String,privateKey str:String) -> String? {
        
        guard let hashData = Data(btcHex: message) else {
            return nil
        }
        
        do {
            var privateKey : PrivateKey
            switch coinType {
            case .bitcoinMain, .bitcoinTest:
                privateKey = try PrivateKey.init(wif: str)
            case .ethMain, .ethTest:
                guard let privateKeyData = Data(btcHex: str) else {
                    return nil
                }
                privateKey = PrivateKey.init(data: privateKeyData, network: .mainnet, isPublicKeyCompressed: false)
            }
            
            
            let signData = try Crypto.sign(hashData, privateKey: privateKey)
            
            let isSignSuccess = try Crypto.verifySignature(signData, message: hashData, publicKey: privateKey.publicKey().raw)
            
            if isSignSuccess {
                let signString = signData.hex
                MyLog("签名字符串:")
                MyLog(signString)
                MyLog("公钥：")
                MyLog(privateKey.publicKey().description)
                return signString
            }else {
                return nil
            }
        } catch let aError {
            MyLog(aError)
            return nil
        }
    }
    
}
