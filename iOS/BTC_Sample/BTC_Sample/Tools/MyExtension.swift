//
//  MyExtension.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/12.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import Foundation
import CryptoSwift

extension Data {
    init?(btcHex:String) {
        let len = btcHex.count / 2
        var data = Data(capacity: len)
        for i in 0..<len {
            let j = btcHex.index(btcHex.startIndex, offsetBy: i * 2)
            let k = btcHex.index(j, offsetBy: 2)
            let bytes = btcHex[j..<k]
            if var num = UInt8(bytes, radix: 16) {
                data.append(&num, count: 1)
            } else {
                return nil
            }
        }
        self = data
    }
    
    
    /// 生成ETH地址
    var ethAddressString : String? {
        get {
            let stringToEncrypt = self.toHexString().dropFirst(2).toString()
            if let data = Data(btcHex: stringToEncrypt) {
                return "0x" + SHA3(variant: .keccak256).calculate(for: data.bytes).toHexString().dropFirst(24).toString()
            }else {
                return nil
            }
            
        }
    }
}


extension Substring {
    func toString() -> String {
        return String(self)
    }
}

extension Decimal {
    var intValue:Int {
        get{
            return Int(NSDecimalNumber(decimal: self).doubleValue)
        }
    }
    
    var doubleValue:Double {
        get{
            return NSDecimalNumber(decimal: self).doubleValue
        }
    }
}
