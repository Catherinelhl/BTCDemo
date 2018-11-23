//
//  BTCUspentVO.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/20.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import Foundation
import HandyJSON

class BTCUspentVO: HandyJSON {
    var tx_hash:String?
    var tx_hash_big_endian:String?
    var tx_index:NSNumber?
    var tx_output_n:NSNumber?
    var value:NSNumber?
    var script:String?
    
    required init() {}
}
