//
//  ApiManager.swift
//  BTC_Sample
//
//  Created by Dong on 2018/11/12.
//  Copyright © 2018 orangeblock.com. All rights reserved.
//

import UIKit
import Moya
import Alamofire


let coinType:CoinType = .bitcoinMain

enum CoinType : String {
    case bitcoinMain = "/btc/main"
    case bitcoinTest = "/btc/test3"
}

let ApiManagerProvider = MoyaProvider<ApiManager>()

/// API
///
/// - getBalance: 获取余额
/// - createTx: 创建交易
/// - sendTx: 发送交易
/// - getTxRecord: 获取交易记录
enum ApiManager {
    case getBalance(address:String)
    case createTx(toAddress:String,amount:Int)
    case sendTx(txJson:[String:Any],signature:String,publicKey:String)
    case getTxRecord(address:String)
}

extension ApiManager : TargetType {
    var baseURL: URL {
        let urlString = "https://api.blockcypher.com/v1" + coinType.rawValue
        return URL(string: urlString)!
    }
    
    var path: String {
        switch self {
        case .getBalance(let address):
            return "/addrs/\(address)/balance"
        case .createTx(_,_) :
            return "/txs/new"
        case .sendTx(_,_,_):
            return "/txs/send"
        case .getTxRecord(let address):
            return "/addrs/\(address)/full"
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .getBalance(_) , .getTxRecord(_):
            return .get
        case .createTx(_,_) ,.sendTx(_,_,_):
            return .post
        }
    }
    
    var sampleData: Data {
        return "{}".data(using: .utf8)!
    }
    
    var task: Task {
        var param:[String:Any] = [:]
        switch self {
        case .createTx(let toAddress, let amount):
            var param:[String:Any] = [:]
            param["inputs"] = [["addresses":[""]]]
            param["outputs"] = [["addresses":[toAddress],
                                 "value":amount]]
            
        case .sendTx(let txJson,let signature, let publicKey):
            param = txJson
            param["signatures"] = [signature]
            param["pubkeys"] = [publicKey]
            
        default :
            return .requestPlain
        }
        return .requestParameters(parameters: param, encoding: URLEncoding.default)
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}
