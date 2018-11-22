//
//  BTCDataUnspentVO.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/20.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit
import HandyJSON

class BTCDataUnspentVO: HandyJSON {

    var unspent_outputs:[BTCUspentVO]?
    
    required init() {}
}
