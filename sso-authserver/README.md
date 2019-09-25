## sso-oauthserver
## 说明
基于spring cloud outh2.0 实现单点认证服务器
## 特殊接口调试
##### 请求url：/oauth/authorize
##### 类路径：
org.springframework.security.oauth2.provider.endpoint
##### 请求url：/oauth/token
##### 类路径：
org.springframework.security.oauth2.provider.endpoint

### 1.授权码模式
授权码模式（authorization code）是功能最完整、流程最严密的授权模式。它的特点就是通过客户端的后台服务器，
与"服务提供商"的认证服务器进行互动。其具体的流程如下：
#### 具体步骤：

A：用户访问客户端（client），客户端告知浏览器（user-Agent）重定向到授权服务器
B：呈现授权界面给用户，用户选择是否给予客户端授权
C：假设用户给予授权，授权服务器（Authorization Server）将用户告知浏览器重定向（重定向地址为Redirection URI）到客户端，同时附上授权码(code)
D：客户端收到授权码,附上早先的重定向URL(Redirection URI)，向授权服务器申请令牌（access token），这一步在客户端的后台的服务器上完成，对用户不可见
E：授权服务器核对授权码（code）和重定向URI，确认无误后，向客户端发放（access token）和更新令牌（refresh token）
##### 在步骤A中客户端告知浏览器重定向到授权服务器的URI包含以下参数：

response_type：表示授权类型，必选项，此处的值固定为"code"
client_id：表示客户端的ID，必选项
client_secret：客户端的密码，可选
redirect_uri：表示重定向URI，可选项
scope：表示申请的权限范围，可选项
state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值。
GET /authorize?response_type=code&client_id=s6BhdRkqt3&state=xyz
&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb HTTP/1.1
Host: server.example.com

##### 在C步骤中授权服务器回应客户端的URI，包含以下参数：

code：表示授权码，该码有效期应该很短，通常10分钟，客户端只能使用一次，否则会被授权服务器拒绝，该码与客户端 ID 和 重定向 URI 是一一对应关系
state：如果客户端请求中包含这个参数，授权服务器的回应也必须一模一样包含这个参数
HTTP/1.1 302 Found
Location: https://client.example.com/cb?code=SplxlOBeZQQYbYS6WxSbIA
&state=xyz

##### 在D步骤中客户端向授权服务器申请令牌的HTTP请求，包含以下参数：

grant_type：表示使用的授权模式，必选，此处固定值为“authorization_code”
code：表示上一步获得的授权吗，必选
redirect_uri：重定向URI，必选，与步骤 A 中保持一致
client_id：表示客户端ID，必选
POST /token HTTP/1.1
Host: server.example.com
Authorization: Basic czZCaGRSa3F0MzpnWDFmQmF0M2JW
Content-Type: application/x-www-form-urlencoded
grant_type=authorization_code&code=SplxlOBeZQQYbYS6WxSbIA&client_id=s6BhdRkqt3
&redirect_uri=https%3A%2F%2Fclient%2Eexample%2Ecom%2Fcb

##### 在E步骤中授权服务器发送的HTTP回复，包含以下参数：

access_token：表示访问令牌，必选项。
token_type：表示令牌类型，该值大小写不敏感，必选项，可以是bearer类型或mac类型。
expires_in：表示过期时间，单位为秒。如果省略该参数，必须其他方式设置过期时间。
refresh_token：表示更新令牌，用来获取下一次的访问令牌，可选项。
scope：表示权限范围，如果与客户端申请的范围一致，此项可省略。
HTTP/1.1 200 OK
Content-Type: application/json;charset=UTF-8
Cache-Control: no-store
Pragma: no-cache
{
    "access_token":"2YotnFZFEjr1zCsicMWpAA",
    "token_type":"example",
    "expires_in":3600,
    "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA",
    "example_parameter":"example_value"
}

### 更新令牌

如果用户访问的时候，客户端的访问令牌access_token已经过期，则需要使用更新令牌refresh_token申请一个新的访问令牌。
客户端发出更新令牌的HTTP请求，包含以下参数：

grant_type：表示使用的授权模式，此处的值固定为”refresh_token”，必选项。
refresh_token：表示早前收到的更新令牌，必选项。
scope：表示申请的授权范围，不可以超出上一次申请的范围，如果省略该参数，则表示与上一次一致。
————————————————