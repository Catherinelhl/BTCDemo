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
    class func generateWallet() -> KeyStoreVO {
        let privateKey = PrivateKey.init(network: isTestNet ? .testnet : .mainnet, isPublicKeyCompressed: false)
        let wallet = Wallet.init(privateKey: privateKey)
        let keyStore = KeyStoreVO()
        keyStore.privateKey = wallet.privateKey.toWIF()
        keyStore.publicKey = wallet.publicKey.description
        keyStore.address = wallet.publicKey.toCashaddr().base58
        return keyStore
    }
    
}
