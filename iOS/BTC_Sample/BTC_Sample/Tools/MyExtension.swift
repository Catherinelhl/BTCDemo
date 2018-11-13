//
//  MyExtension.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/12.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import Foundation


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
}

extension Substring {
    func toString() -> String {
        return String(self)
    }
}
