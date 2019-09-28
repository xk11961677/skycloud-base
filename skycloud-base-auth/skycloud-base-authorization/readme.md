授权服务介绍
----------

## 简介

授权微服务，可供网关gateway实现微服务对外权限的授予

## 使用指南

### 初始账号

本例中初使化的客户端与用户账号

client_id:     `test_client`

client_secret: `test_secret`

username: `admin`

password: `password`

### JWT Token介绍

本例中access_token Payload 负载（中间部分）base64解码后结构如下

```
{
   "user_name": "admin",   //用户username，users表中username字段
   "scope": [
       "read"              //授权权限范围
   ], 
   "organization": "admin", //组织，该字段为自定义，自定义方法见 开发指南中
   "exp": 1531975621,       //过期时间
   "authorities": [         //授权权限，本例子中为用户授权的角色名，roles表中code字段
       "ADMIN"
   ], 
   "jti": "23408d38-8cdc-4460-beac-24c76dc7629a",  //jwt token的id
   "client_id": "test_client"                      //客户端id，oauth_client_details表中client_id
}
```

### 接口测试

#### 密码模式，grant_type=password

用途：可用于用户通过前端应用登陆、使用应用，如app，web等终端

![postman](../../docs/image/oauth2.0_01.png)

请求报文

```
POST /oauth/token?scope=read&grant_type=password HTTP/1.1
Host: localhost:8902
Authorization: Basic dGVzdF9jbGllbnQ6dGVzdF9zZWNyZXQ=
Cache-Control: no-cache
Content-Type: application/x-www-form-urlencoded

username=admin&password=password
```
响应报文

```
{
    "additionalInformation": {
        "jti": "503f6418-db96-4e56-be1e-5dae768e7252"
    },
    "expired": false,
    "expiresIn": 0,
    "refreshToken": {
        "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjhjN2Y3ZDAwLTBiNzUtNDI3MC04ZTA0LWNjOTQzZjBhOThjZCIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiI1MDNmNjQxOC1kYjk2LTRlNTYtYmUxZS01ZGFlNzY4ZTcyNTIifQ.kxUYY7uZmiKunFqmnJ96P3BigyMuU7h0IsZRVdRkGDY"
    },
    "scope": [
        "read"
    ],
    "tokenType": "bearer",
    "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjUwM2Y2NDE4LWRiOTYtNGU1Ni1iZTFlLTVkYWU3NjhlNzI1MiIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsicmVhZCJdfQ.s6JJjifJ-4uvHxgnZiicYiayl32ep8u8FTIB8xlEr4M"
}
```

#### 客户端模式，grant_type=client_credentials

用途：可用于接口开放给第三方商户，商户申请client_id和密码，即可调用授权的接口

![postman](../../docs/image/oauth2.0_02.png)

请求报文

```
POST /oauth/token?scope=read&grant_type=client_credentials HTTP/1.1
Host: localhost:8902
Authorization: Basic dGVzdF9jbGllbnQ6dGVzdF9zZWNyZXQ=
Cache-Control: no-cache
```
响应报文

```
{
    "additionalInformation": {
        "jti": "7e852490-e912-46d8-ab96-c4d4caaff7fa"
    },
    "expired": false,
    "expiresIn": 0,
    "scope": [
        "read"
    ],
    "tokenType": "bearer",
    "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJyZWFkIl0sImp0aSI6IjdlODUyNDkwLWU5MTItNDZkOC1hYjk2LWM0ZDRjYWFmZjdmYSIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50In0.LHB6ZrhcqKsWjbhNJfFCVY3nrcFBhMMNmlhqYIP8UkI"
}
```

#### 授权码模式，grant_type=authorization_code

用途：可用开放平台账户给第三方商户，商户申请client_id和密码请求用户授权，用户授权商户即可调用平台授权的接口获取数据，类似微信、支付宝授权登陆
![postman](../../docs/image/oauth2.0_03.png)
![postman](../../docs/image/oauth2.0_04.png)
![postman](../../docs/image/oauth2.0_05.png)

**第一步：用户登陆授权陆**

1. 用户跳转至平台

`http://localhost:8902/oauth/authorize?response_type=code&client_id=test_client&scope=read&state=test&redirect_uri=http://baidu.com`

```
client_id： 商户申请的client_id(oauth_client_details表中的记录)
state： 该参数在跳转回去时原样带回
redirect_uri： 该参数要与商户申请client_id时登记的url(oauth_client_details表中的web_server_redirect_uri字段)一样
```

2. 用户进入登陆页面，输入用户名和密码登陆


3. 用户点击 "授权"或"拒绝"


4. 用户同意授权后，浏览器自动重定向至redirect_uri并带上code和state参数



**第二步：根据url上带的code获取用户的access_token**


请求报文

```
POST /oauth/token?grant_type=authorization_code&code=vANrcE&redirect_uri=http://www.baidu.com HTTP/1.1
Host: localhost:8902
Authorization: Basic dGVzdF9jbGllbnQ6dGVzdF9zZWNyZXQ=
Cache-Control: no-cache
```
响应报文

```
{
    "additionalInformation": {
        "jti": "f1575387-5e00-4f25-80d1-6f948298ff35"
    },
    "expired": false,
    "expiresIn": 0,
    "refreshToken": {
        "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjBiYzhhZmRhLTg3MDQtNDgwYS05MTA2LTQyNWU5YThiYjIyNSIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsiYWxsIiwicmVhZCIsIndyaXRlIl0sImF0aSI6ImYxNTc1Mzg3LTVlMDAtNGYyNS04MGQxLTZmOTQ4Mjk4ZmYzNSJ9.8RcSU78sbDwQYdh-3lufIYfGxyQpwr-4cnTZTEfZcQk"
    },
    "scope": [
        "all",
        "read",
        "write"
    ],
    "tokenType": "bearer",
    "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6ImYxNTc1Mzg3LTVlMDAtNGYyNS04MGQxLTZmOTQ4Mjk4ZmYzNSIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsiYWxsIiwicmVhZCIsIndyaXRlIl19.dWo1Xbpu6H5IgpAAq5ihA8dRwVZp1F3OuEVQx2xjiHQ"
}
```

#### 简化模式，grant_type=authorization_code [此处就不截图,请自行测试]

请求报文

```
POST /oauth/authorize?response_type=token&client_id=test_client&redirect_uri=http://www.baidu.com HTTP/1.1
Host: localhost:8902
Authorization: Basic dGVzdF9jbGllbnQ6dGVzdF9zZWNyZXQ=
Cache-Control: no-cache
```
响应报文

```

```


#### 刷新access_token

用途：使用refresh_token更新access_token

![postman](../../docs/image/oauth2.0_06.png)

请求报文

```
POST /oauth/token?scope=read&grant_type=refresh_token HTTP/1.1
Host: localhost:8902
Authorization: Basic dGVzdF9jbGllbnQ6dGVzdF9zZWNyZXQ=
Cache-Control: no-cache
Content-Type: application/x-www-form-urlencoded;

refresh_token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjBiYzhhZmRhLTg3MDQtNDgwYS05MTA2LTQyNWU5YThiYjIyNSIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsiYWxsIiwicmVhZCIsIndyaXRlIl0sImF0aSI6ImYxNTc1Mzg3LTVlMDAtNGYyNS04MGQxLTZmOTQ4Mjk4ZmYzNSJ9.8RcSU78sbDwQYdh-3lufIYfGxyQpwr-4cnTZTEfZcQk
```
响应报文

```
{
    "additionalInformation": {
        "jti": "8d120c55-0151-4100-8520-423478a62615"
    },
    "expired": false,
    "expiresIn": 0,
    "refreshToken": {
        "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjBiYzhhZmRhLTg3MDQtNDgwYS05MTA2LTQyNWU5YThiYjIyNSIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiI4ZDEyMGM1NS0wMTUxLTQxMDAtODUyMC00MjM0NzhhNjI2MTUifQ.CzAxu7FqtnK26jmuYUVN80yigHFzoraLoJAM5Nyghic"
    },
    "scope": [
        "read"
    ],
    "tokenType": "bearer",
    "value": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbImFkbWluIl0sImp0aSI6IjhkMTIwYzU1LTAxNTEtNDEwMC04NTIwLTQyMzQ3OGE2MjYxNSIsImNsaWVudF9pZCI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsicmVhZCJdfQ.FpZDyPmOh2g1iuAq-kyqboYbZ8eGVwlt8XbI8tOtHZU"
}
```