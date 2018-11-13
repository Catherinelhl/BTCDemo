//
//  AES.m
//  BlockChain
//
//  Created by 刘勇 on 2018/8/8.
//  Copyright © 2018年 orangeblock.com. All rights reserved.
//

#import "AES.h"
#import <CommonCrypto/CommonCryptor.h>


#define __PASSWORD_KEY__      @"jdcv@888@jdcv888"
#define __IV_KEY__            @"qwertyuioplkjhgg"

//密码8位固定
static  NSString *secrectKey_128_fixed = @"jdcv@888";

//密码8~16位
NSString *secretKey_128_1 = @"a";
NSString *secretKey_128_2 = @"b2";
NSString *secretKey_128_3 = @"cd3";
NSString *secretKey_128_4 = @"e@f4";
NSString *secretKey_128_5 = @"ghij5";
NSString *secretKey_128_6 = @"k#lmn6";
NSString *secretKey_128_7 = @"opqrst7";
NSString *secretKey_128_8 = @"uv@wxyz8";

@implementation AES

+ (NSString *) AES128Encrypt: (NSString *)inputText
{
    
    char keyPtr[kCCKeySizeAES128+1];
    memset(keyPtr, 0, sizeof(keyPtr));
    [__PASSWORD_KEY__ getCString:keyPtr maxLength:sizeof(keyPtr) encoding:NSUTF8StringEncoding];

    char ivPtr[kCCBlockSizeAES128 + 1];
    memset(ivPtr, 0, sizeof(ivPtr));
    [__IV_KEY__ getCString:ivPtr maxLength:sizeof(ivPtr) encoding:NSUTF8StringEncoding];

    NSData* data = [inputText dataUsingEncoding:NSUTF8StringEncoding];
    size_t bufferSize = [data length] + kCCBlockSizeAES128;
    void *buffer = malloc(bufferSize);

    size_t numBytesEncrypted = 0;
    CCCryptorStatus cryptStatus = CCCrypt(kCCEncrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding, keyPtr, kCCBlockSizeAES128, ivPtr, [data bytes], [data length], buffer, bufferSize, &numBytesEncrypted);
    if (cryptStatus == kCCSuccess)
    {
        NSData *resultData = [NSData dataWithBytesNoCopy:buffer length:numBytesEncrypted];
        return [resultData base64EncodedStringWithOptions:0];

    }
    free(buffer);

    return nil;
    
}
// 本地加密
+ (NSString *) AES128LocalEncrypt: (NSString *)inputText passwordAndIv:(NSString *)psdAndIv
{
    psdAndIv = [self setSecretKey:psdAndIv];
    char keyPtr[kCCKeySizeAES128 + 1];
    bzero(keyPtr, sizeof(keyPtr));
    [psdAndIv getCString:keyPtr maxLength:sizeof(keyPtr) encoding:NSUTF8StringEncoding];
    
    // IV
    char ivPtr[kCCBlockSizeAES128 + 1];
    bzero(ivPtr, sizeof(ivPtr));
    [psdAndIv getCString:ivPtr maxLength:sizeof(ivPtr) encoding:NSUTF8StringEncoding];

    NSData* data = [inputText dataUsingEncoding:NSUTF8StringEncoding];
    size_t bufferSize = [data length] + kCCBlockSizeAES128;
    void *buffer = malloc(bufferSize);
    size_t numBytesEncrypted = 0;
    
    
    CCCryptorStatus cryptorStatus = CCCrypt(kCCEncrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding,
                                            keyPtr, kCCKeySizeAES128,
                                            ivPtr,
                                            [data bytes], [data length],
                                            buffer, bufferSize,
                                            &numBytesEncrypted);
    
    if(cryptorStatus == kCCSuccess){
        NSData *resultData = [NSData dataWithBytesNoCopy:buffer length:numBytesEncrypted];
        return [resultData base64EncodedStringWithOptions:0];
        
    }
    
    free(buffer);
    return nil;
}
// 本地解密
+ (NSString *) AES128LocalDecrypt: (NSString *)inputText passwordAndIv:(NSString *)psdAndIv
{
    psdAndIv = [self setSecretKey:psdAndIv];
    char keyPtr[kCCKeySizeAES128 + 1];
    bzero(keyPtr, sizeof(keyPtr));
    [psdAndIv getCString:keyPtr maxLength:sizeof(keyPtr) encoding:NSUTF8StringEncoding];
    
    // IV
    char ivPtr[kCCBlockSizeAES128 + 1];
    bzero(ivPtr, sizeof(ivPtr));
    [psdAndIv getCString:ivPtr maxLength:sizeof(ivPtr) encoding:NSUTF8StringEncoding];
    
    NSData *data = [[NSData alloc] initWithBase64EncodedString:inputText options:0];
    size_t bufferSize = [data length] + kCCBlockSizeAES128;
    void *buffer = malloc(bufferSize);
    size_t numBytesEncrypted = 0;
    
    CCCryptorStatus cryptorStatus = CCCrypt(kCCDecrypt, kCCAlgorithmAES128, kCCOptionPKCS7Padding,
                                            keyPtr, kCCKeySizeAES128,
                                            ivPtr,
                                            [data bytes], [data length],
                                            buffer, bufferSize,
                                            &numBytesEncrypted);
    
    if(cryptorStatus == kCCSuccess){
        NSData * resultData = [NSData dataWithBytesNoCopy:buffer length:numBytesEncrypted];
        return [[NSString alloc] initWithData:resultData encoding:NSUTF8StringEncoding];
    }
    
    free(buffer);
    return nil;
}

+ (NSString *)setSecretKey:(NSString *)secretKey128
{
    switch (secretKey128.length) {
        case 8:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_8];
            break;
        case 9:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_7];
            break;
        case 10:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_6];
            break;
        case 11:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_5];
            break;
        case 12:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_4];
            break;
        case 13:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_3];
            break;
        case 14:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_2];
            break;
        case 15:
            secretKey128 = [NSString stringWithFormat:@"%@%@",secretKey128,secretKey_128_1];
            break;
        case 16:
            // 16位密碼不需補
            break;
    }
    return secretKey128;
}


@end
