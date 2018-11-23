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
import SwiftyJSON


enum CoinType : String {
    case bitcoinMain = "https://api.blockcypher.com/v1/btc/main"
    case bitcoinTest = "https://api.blockcypher.com/v1/btc/test3"
    case ethMain = "https://api.blockcypher.com/v1/eth/main"
    case ethTest = "https://api.blockcypher.com/v1/beth/test"
    
    case blockChain_btc_Main = "https://blockchain.info"
}

var coinType:CoinType = .blockChain_btc_Main

let ApiManagerProvider = MoyaProvider<ApiManager>(requestClosure: { (endpoint:Endpoint, done: @escaping MoyaProvider.RequestResultClosure) in
    do{
        var request = try endpoint.urlRequest()
        request.timeoutInterval = 30 //设置请求超时时间
        done(.success(request))
    }catch{
        return
    }
})



/// API
///
/// - getBalance: 获取余额
/// - createTx: 创建交易
/// - sendTx: 发送交易
/// - getTxRecord: 获取交易记录
enum ApiManager {
    case getBalance(address:String)
    case createTx(fromAddress:String,toAddress:String,amount:Int,fees:Int?)
    case sendTx(txJson:[String:Any],signatures:[String],publicKeys:[String]?)
    case getTxRecord(address:String)
    
    case getUnspent(address:String)
    case pushTx(txHex:String)
}

extension ApiManager : TargetType {
    var baseURL: URL {
        let urlString = coinType.rawValue
        return URL(string: urlString)!
    }
    
    var path: String {
        switch self {
        case .getBalance(let address):
            if coinType == .blockChain_btc_Main {
                return "/balance"
            }
            return "/addrs/\(address)/balance"
            
        case .createTx(_,_,_,_) :
            return "/txs/new"
            
        case .sendTx(_,_,_):
            return "/txs/send"
            
        case .getTxRecord(let address):
            switch coinType {
            case .bitcoinMain, .bitcoinTest:
                return "/addrs/\(address)/full"
            case .ethMain, .ethTest:
                return "/addrs/\(address)"
            case .blockChain_btc_Main:
                return "/rawaddr/\(address)"
            }
            
        case .getUnspent(_):
            switch coinType {
            case .blockChain_btc_Main:
                return "/unspent"
            default:
                return ""
            }
        case .pushTx(_):
            switch coinType {
            case .blockChain_btc_Main:
                return "/pushtx"
            default:
                return ""
            }
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .getBalance(_) , .getTxRecord(_) , .getUnspent(_):
            return .get
        case .createTx(_,_,_,_) ,.sendTx(_,_,_), .pushTx(_):
            return .post
        }
    }
    
    var sampleData: Data {
        return "{}".data(using: .utf8)!
    }
    
    var task: Task {
        let urlParam = ["token":"4eaed359b2984580b55e5b004fd0f68d"]
        switch self {
        case .createTx(let fromAddress,let toAddress, let amount,let fees):
            var param:[String:Any] = [:]
            
            switch coinType {
            case .bitcoinMain, .bitcoinTest:
                param["fees"] = fees
            case .ethMain, .ethTest:
                param["gas_price"] = fees
            case .blockChain_btc_Main:
                break
            }
            
            param["inputs"] = [["addresses":[fromAddress]]]
            param["outputs"] = [["addresses":[toAddress],
                                 "value":amount]]
            
            MyLog(JSON(param).rawValue)
            let jsonData = try! JSON(param).rawData()
            return .requestCompositeData(bodyData: jsonData, urlParameters: urlParam)
            
        case .sendTx(let txJson,let signatures, let publicKeys):
            var param:[String:Any] = [:]
            param = txJson
            param["signatures"] = signatures
            param["pubkeys"] = publicKeys
            
            MyLog(JSON(param).rawValue)
            let jsonData = try! JSON(param).rawData()
            return .requestCompositeData(bodyData: jsonData, urlParameters: urlParam)
            
        case .getBalance(let address):
            return .requestParameters(parameters: ["active":address], encoding: URLEncoding.default)
        case .getUnspent(let address):
            return .requestParameters(parameters: ["active":address,"limit":1000], encoding: URLEncoding.default)
        case .pushTx(let txHex):
            let param = ["tx":txHex]
//            let jsonData = try! JSON(param).rawData()
            return .requestParameters(parameters: param, encoding: URLEncoding.default)
        default :
            return .requestParameters(parameters: urlParam, encoding: URLEncoding.default)
        }
        
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}
