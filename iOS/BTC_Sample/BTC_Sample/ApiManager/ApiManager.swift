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
    case bitcoinMain = "/btc/main"
    case bitcoinTest = "/btc/test3"
    case ethMain = "/eth/main"
    case ethTest = "/beth/test"
}

var coinType:CoinType = .bitcoinMain

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
            }
        }
    }
    
    var method: Moya.Method {
        switch self {
        case .getBalance(_) , .getTxRecord(_):
            return .get
        case .createTx(_,_,_,_) ,.sendTx(_,_,_):
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
            
        default :
            return .requestParameters(parameters: urlParam, encoding: URLEncoding.default)
        }
        
    }
    
    var headers: [String : String]? {
        return nil
    }
    
    
}
