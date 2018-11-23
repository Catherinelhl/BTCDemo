//
//  MyDefine.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/12.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import Foundation

var myAddress:String {
    get{
        switch coinType {
        case .bitcoinTest:
            return "mkkjcX4s4zoJJaE5E1NnfvsbuMvmzgBWTo"
        case.bitcoinMain , .blockChain_btc_Main:
            return "1CmRaAsRQ7guVPyfDYN7ucbwkZFx4yHTFA"
        case .ethMain, .ethTest:
            return "0x5836cc7b00696fd24e33f01c85f50371d87e9fd0"
        }
    }
}
var myPrivateKey:String {
    get{
        switch coinType {
        case .bitcoinTest:
            return "93AaWXJMutsyX5KPCXzGjK9uPm18ezP5jiFjcCtvZwELYX9LAkk"
        case.bitcoinMain , .blockChain_btc_Main:
            return "5JX8HpBNRR912Qtk2ddrm4LFRpsSZ3oFqa6kFCBVAjaNkbw75MT"
        case .ethMain, .ethTest:
            return "7ba2c387f7f35b6e97f2bc34fe7785a51b89939fbaa525f830e912bbc2aa6dee"
        }
    }
}
var myPublicKey:String {
    get{
        switch coinType {
        case .bitcoinTest:
            return "04b33ac0f3c51e5a197a77b55998c1da575e0ef9e52b75056885dcd59d78bb340a0f8c25b4b7eaca07c242bb71d56507b887f1957999ff2565b9fbee238d6e756e"
        case.bitcoinMain , .blockChain_btc_Main:
            return "0453ed8abc42e67478e03051253180532eda639c1411b83b46c31741d55ee3bb17968933f46714a1e5f8bc9821dd655b9d12734472192dd6b1ce3cf65b076d2a58"
        case .ethMain, .ethTest:
            return "04ec8dfc59cab16380fefa556c08df339209ef91427809eb2b1475299f4090df18216f934782895fdda9b5532ac55ab87b3f22a9c1e01e534ed828eeac0688c287"
        }
    }
}

var rate:Decimal {
    get{
        switch coinType {
        case .bitcoinMain,.bitcoinTest , .blockChain_btc_Main:
            return pow(10, 8)
        case .ethMain, .ethTest:
            return pow(10, 18)
        }
    }
}

var fees:Decimal {
    get{
        switch coinType {
        case .bitcoinMain, .bitcoinTest , .blockChain_btc_Main:
            return 0.0005
        case .ethMain, .ethTest:
            return (gasPrice * 21000) / rate
        }
    }
}

var gasPrice:Decimal {
    get{
        return 5 * pow(10, 9)
    }
}

var currencySymbol:String {
    get{
        switch coinType {
        case .bitcoinMain, .bitcoinTest, .blockChain_btc_Main:
            return "BTC"
        case .ethMain, .ethTest:
            return "ETH"
        }
    }
}


// MARK: - 打印方法
func MyLog<T>(_ message : T,file:String = #file,methodName: String = #function, lineNumber: Int = #line){
    #if DEBUG
    let fileName = (file as NSString).lastPathComponent
    let dateForm = DateFormatter.init()
    dateForm.dateFormat = "HH:mm:ss:SSS"
    print("[\(fileName)][\(lineNumber)][\(dateForm.string(from: Date()))]\(methodName):\(message)")
    #endif
    
}
