//
//  AES.h
//  BlockChain
//
//  Created by 刘勇 on 2018/8/8.
//  Copyright © 2018年 orangeblock.com. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface AES : NSObject

///  AES 网络数据加密
+ (NSString *) AES128Encrypt: (NSString *)inputText;

///  AES 本地数据加密
+ (NSString *) AES128LocalEncrypt: (NSString *)inputText passwordAndIv:(NSString *)psdAndIv;

///  AES  解密
+ (NSString *) AES128LocalDecrypt: (NSString *)inputText passwordAndIv:(NSString *)psdAndIv;


+ (NSString *)setSecretKey:(NSString *)secretKey128;
@end
