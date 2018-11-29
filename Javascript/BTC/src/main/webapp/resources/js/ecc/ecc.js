var bitcore = require('bitcore-lib');
/**
 * 获取私钥
 * @returns privateKeyWIF
 */
function getPrivateKey() {
	
	var randArr = new Uint8Array(32);         //console.log("randArr: " + randArr);  // create a typed array of 32
	// bytes (256 bits)
	try {
		window.msCrypto.getRandomValues(randArr);         //console.log("randArr: " + randArr);  // populate array with
	} catch (e) {
		// TODO: handle exception
		window.crypto.getRandomValues(randArr);         //console.log("randArr: " + randArr);  // populate array with
	}
	// expect regular JS arrays.
	var privateKeyBytes = [];
	for (var index = 0; index < randArr.length; ++index) {
		privateKeyBytes[index] = randArr[index];
	}

	// hex string of our private key
	var privateKeyHex = Crypto.util.bytesToHex(privateKeyBytes).toUpperCase();  //console.log("privateKeyHex:" + privateKeyHex);
	var privateKeyWIF = new Bitcoin.Address(privateKeyBytes);
	privateKeyWIF.version = 0x80;  // 0x80 = 128,
	privateKeyWIF = privateKeyWIF.toString();   //console.log("privateKeyWIF:" + privateKeyWIF);

	return privateKeyWIF;
}

/**
 * 根据私钥获取公钥
 * @param privateKeyWIF
 * @returns publicKeyHex
 */
function getPublicKeyHex(privateKeyWIF) {

	var privateKeyHex = wifToHex(privateKeyWIF) ;
	var keyBigInt = BigInteger.fromByteArrayUnsigned(Crypto.util.hexToBytes(privateKeyHex));
	var ecparams = EllipticCurve.getSECCurveByName("secp256k1"); 

	// convert our random array or private key to a Big Integer
	var curvePt = ecparams.getG().multiply(keyBigInt);

	var x = curvePt.getX().toBigInteger();
	var y = curvePt.getY().toBigInteger();
	var publicKeyBytes = EllipticCurve.integerToBytes(x, 32);
	publicKeyBytes = publicKeyBytes.concat(EllipticCurve.integerToBytes(y, 32));
	publicKeyBytes.unshift(0x04);
	var publicKeyHex = Crypto.util.bytesToHex(publicKeyBytes);  //console.log("publicKeyHex:" + publicKeyHex);

	return publicKeyHex;
}

/**
 * 根据公钥获取地址
 * @param publicKeyHex
 * @returns address
 */
function getAddress(publicKeyHex) {  //console.log("publicKeyHex:" + publicKeyHex);
	

	var publicKeyBytes = Crypto.util.hexToBytes(publicKeyHex);
	var hash160 = Crypto.RIPEMD160(Crypto.util.hexToBytes(Crypto.SHA256(publicKeyBytes)));

	var version = 0x00;  //if using testnet, would use 0x6F or 111.
	var hashAndBytes = Crypto.util.hexToBytes(hash160);
	hashAndBytes.unshift(version);

	var doubleSHA = Crypto.SHA256(Crypto.util.hexToBytes(Crypto.SHA256(hashAndBytes)));
	var addressChecksum = doubleSHA.substr(0, 8);

	var unencodedAddress = "00" + hash160 + addressChecksum;

	var address = Bitcoin.Base58.encode(Crypto.util.hexToBytes(unencodedAddress));   //console.log("address=" + address) 
	return address;
}

/**
 * 将wif格式转化为hex
 * @param wif
 * @returns
 */
function wifToHex(wif) {

	var compressed = false;
	var decode = Bitcoin.Base58.decode(wif);  //console.log(decode);
	var key = decode.slice(0, decode.length - 4);
	key = key.slice(1, key.length);
	if (key.length >= 33 && key[key.length - 1] == 0x01) {
		key = key.slice(0, key.length - 1);
		compressed = true;
	}
	return Crypto.util.bytesToHex(key).toUpperCase();
}

/**
 * 
 * @param key
 * @returns
 */
function hexToWif(key) {
	var r = Crypto.util.hexToBytes(key);
	var privateKeyWIF = new Bitcoin.Address(r);
	privateKeyWIF.version = 0x80; //0x80 = 128
	privateKeyWIF = privateKeyWIF.toString();
	return privateKeyWIF;
}

/**
 * 签章
 * @param privatekey
 * @param msg
 * @returns signStr
 */
function doSign(privatekey,msg) {

	
	var privkey = bitcore.PrivateKey.fromWIF(privatekey);   //console.log("msg2",msg2);
	var signStr = bitcore.Message.fromString(msg).sign(privkey);  //	console.log("sign : ", signStr);
	
	return signStr;
}

/**
 * 生成keystore文件
 * @param secretKey
 * @param address
 * @param privateKey
 * @param publicKey
 * @returns
 */
function createKeyStore(secretKey,address,privateKey,publicKey) {
	
	//将生成的privateKey，publicKey，address存入keystore，再根据向量值和密钥值进行aes加密
	CryptoJS.AES.encrypt("Message", "Secret Passphrase");
	
	var address = address;
	var privateKey = privateKey;
	var publicKey = publicKey;
	var str = "{privateKey:"+privateKey+",publicKey:"+publicKey+",address:"+address+"}";   //console.log("str: "+str);
	

	// 密钥 16 位
	var key = secretKey + "jdcv@888";
	// 初始向量 initial vector 16 位
	var iv = key;
	// key 和 iv 可以一致
	 
	key = CryptoJS.enc.Utf8.parse(key);
	iv = CryptoJS.enc.Utf8.parse(iv);
	
	var keystore = CryptoJS.AES.encrypt(str, key, {
	    iv: iv,
	    mode: CryptoJS.mode.CBC,
	    padding: CryptoJS.pad.Pkcs7
	});
	 
	// 转换为字符串
	keystore = keystore.toString();      //console.log("keystore:"+keystore);

	return keystore;
}