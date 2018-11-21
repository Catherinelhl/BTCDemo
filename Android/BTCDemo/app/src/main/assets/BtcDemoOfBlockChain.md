##BTC Of BlockChain

[有道云链接地址] (herf http://note.youdao.com/noteshare?id=6a54a21c50bbb66d3de447b134747693)

* 获取当前钱包的余额

request | method | params
--------|--------|-------
/balance| GET | String address
#### Request eg:

```
https://blockchain.info/balance?active=16ugnJ7pndAFJJfMwoSDFbNTwzHvxhL1cL
 
```

#### Response Json:
```
{
  "mkkjcX4s4zoJJaE5E1NnfvsbuMvmzgBWTo": {
    "final_balance": 45751300,
    "n_tx": 4,
    "total_received": 67759400
  }
}
```

* 获取当前钱包的未交易区块

request | method | params
--------|--------|-------
/unspent| GET | String address

#### Request eg:

```

https://blockchain.info/unspent?active=16ugnJ7pndAFJJfMwoSDFbNTwzHvxhL1cL
 
```


#### Response Json:
```
{
  "unspent_outputs": [
    {
      "tx_hash": "00d93386605c079daf75d4854e0daf7f5374960968564e1343bf0e74e188c523",
    "tx_hash_big_endian": "23c588e1740ebf43134e5668099674537faf0d4e85d475af9d075c608633d900",
    "tx_index": 286558506,
    "tx_output_n": 1,
    "script": "76a9143973ddc5132ab11263c508c9ebc4d2f5cc1d53dc88ac",
    "value": 32991900,
    "value_hex": "01f76a9c",
    "confirmations": 201
    },
    {
      "tx_hash": "bc6bcf2517226f8b408bf54fcdcd906c6a2e3161e9779c6edfb410ad434b6e05",
      "tx_hash_big_endian": "056e4b43ad10b4df6e9c77e961312e6a6c90cdcd4ff58b408b6f221725cf6bbc",
      "tx_index": 286564143,
      "tx_output_n": 1,
      "script": "76a9143973ddc5132ab11263c508c9ebc4d2f5cc1d53dc88ac",
      "value": 100000,
      "value_hex": "0186a0",
      "confirmations": 166
    },
    {
      "tx_hash": "f1bf14c0a1fe464fa515474e5e641068de195285697b8124bb04fd1b6b80613b",
      "tx_hash_big_endian": "3b61806b1bfd04bb24817b69855219de6810645e4e4715a54f46fea1c014bff1",
      "tx_index": 286573251,
      "tx_output_n": 81,
      "script": "76a9143973ddc5132ab11263c508c9ebc4d2f5cc1d53dc88ac",
      "value": 12659400,
      "value_hex": "00c12ac8",
      "confirmations": 116
    }
  ]
}
```

* 发送交易

####1.BlockChain方式


 request | method | params
--------|--------|-------
/pushtx| POST | String tx

#### Request eg:

```
https://blockchain.info/pushtx
```
Request Json:

```
{
"tx":"0100000001e172ca12c41990eb0114a3f37eb2c566c0d858bde5a83bd663fcfde20c4c5d41010000008a47304402200cdae056ea0ba22cfb6c066a4e795cabd4dd89a2b9707142e835f1a91e20612e0220381e40ab9fbfa3deea6ef74cec18af8c4827a2bdcc8453439bac69f08e69b0ab8141048fe10b91d8c6f250d2016376e82c31658e7227fdeaa463f64cf868eb3c90e3e184d7e08179e7dc87a02f8fae8e375c72db1dbef93e204fbec93c016590f53b8dffffffff02d00700000000000017a9148669d6028a8e274d7610c3f111ac2c68d855ddb087a00f0000000000001976a91440cf7765cd74dee5113c5a71637731c5704be71b88ac00000000"
}
 
```

#### Response Json:
```
 Transaction Submitted
```

####2. BTC.com方式

request | method | params
--------|--------|-------
/tools/tx-publish| POST | String tx

#### Request eg:

```

https://chain.api.btc.com/v3/tools/tx-publish
```
Request Json:

```
{
"rawhex":"0100000001e172ca12c41990eb0114a3f37eb2c566c0d858bde5a83bd663fcfde20c4c5d41010000008a47304402200cdae056ea0ba22cfb6c066a4e795cabd4dd89a2b9707142e835f1a91e20612e0220381e40ab9fbfa3deea6ef74cec18af8c4827a2bdcc8453439bac69f08e69b0ab8141048fe10b91d8c6f250d2016376e82c31658e7227fdeaa463f64cf868eb3c90e3e184d7e08179e7dc87a02f8fae8e375c72db1dbef93e204fbec93c016590f53b8dffffffff02d00700000000000017a9148669d6028a8e274d7610c3f111ac2c68d855ddb087a00f0000000000001976a91440cf7765cd74dee5113c5a71637731c5704be71b88ac00000000"
}
 
```

#### Response Json:
```
 {
 "data":"bb04726d203dc8aba0a91dbf20344ba05cf058929a5833d0b9cfdecac0b07d39",
 "err_no":0,
 "err_msg":null
 }
 
```

* 获取交易记录

request | method | params
--------|--------|-------
/rawaddr/{address}| GET | String address

#### Request eg:
```
https://blockchain.info/rawaddr/16ugnJ7pndAFJJfMwoSDFbNTwzHvxhL1cL

```
#### Response Json:
```
{
        "hash160":"40cf7765cd74dee5113c5a71637731c5704be71b",
        "address":"16ugnJ7pndAFJJfMwoSDFbNTwzHvxhL1cL",
        "n_tx":25,
        "total_received":2035000,
        "total_sent":2030000,
        "final_balance":5000,
        "txs":[
    
    {
       "ver":1,
       "inputs":[ ...],
       "weight":1020
       "relayed_by":"127.0.0.1",
       "out":[ ...],
       "lock_time":0,
       "result":0,
       "size":255,
       "time":1542685297,
       "tx_index":390942774,
       "vin_sz":1,
       "hash":"20dbd629ed8150f3524964f03d3a82c096b00382e9e7ab4ee74d6d659fe5df83",
       "vout_sz":2
    },...]
```

* 获取当前交易区块的状态（You can also request the transaction to return in binary form (Hex encoded) using ?format=hex）

request | method | params
--------|--------|-------
/rawtx/{tx_hash}| GET | String tx_hash

#### Request eg:

```
https://blockchain.info/rawtx/bb04726d203dc8aba0a91dbf20344ba05cf058929a5833d0b9cfdecac0b07d39 

```
#### Response Json:
```
 
    {
       "ver":1,
       "inputs":[
          {
             "sequence":4294967295,
             "witness":"",
             "prev_out":{
                "spent":true,
                "tx_index":390716851,
                "type":0,
                "addr":"16ugnJ7pndAFJJfMwoSDFbNTwzHvxhL1cL",
                "value":22000,
                "n":1,
                "script":"76a91440cf7765cd74dee5113c5a71637731c5704be71b88ac"
             },
             "script":"483045022100c0e0e06729bd983db98d3107bf5da585f928e659cfc749b1ae63ac84441dc5700220020568b0475029b177904faedeec327fc867166be52cc13e178b3afbbc55d99a8141048fe10b91d8c6f250d2016376e82c31658e7227fdeaa463f64cf868eb3c90e3e184d7e08179e7dc87a02f8fae8e375c72db1dbef93e204fbec93c016590f53b8d"
          }
       ],
       "weight":1024,
       "relayed_by":"0.0.0.0",
       "out":[
          {
             "spent":false,
             "tx_index":390941217,
             "type":0,
             "addr":"3DwjFrvc6avJeUJ6hG54WoSp3p2cVM4xwc",
             "value":20000,
             "n":0,
             "script":"a9148669d6028a8e274d7610c3f111ac2c68d855ddb087"
          },
          {
             "spent":false,
             "tx_index":390941217,
             "type":0,
             "addr":"16ugnJ7pndAFJJfMwoSDFbNTwzHvxhL1cL",
             "value":1000,
             "n":1,
             "script":"76a91440cf7765cd74dee5113c5a71637731c5704be71b88ac"
          }
       ],
       "lock_time":0,
       "size":256,
       "double_spend":false,
       "time":1542684716,
       "tx_index":390941217,
       "vin_sz":1,
       "hash":"bb04726d203dc8aba0a91dbf20344ba05cf058929a5833d0b9cfdecac0b07d39",
       "vout_sz":2
    }

```