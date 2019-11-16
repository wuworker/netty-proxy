# 基于netty实现的代理服务器
支持tcp代理以及http代理

# 菜单

* [快速开始](#快速开始)  
  * [修改配置文件](#修改配置文件)  
  * [启动](#启动)  
* [配置](#配置)
  * [配置tcp代理](#配置tcp代理)  
  * [配置http代理](#配置http代理)  
  * [配置https解密](#配置https解密)
  * [配置http二级代理](#配置http二级代理)
* [管理员](#管理员)
  * [telnet](#telent)
  * [命令](#命令)
  * [授权](#授权)

## 快速开始

### 修改配置文件

同时启动mysql代理，和http代理
```yaml
proxy:
  boss-threads: 0
  work-threads: 16
  tcp:
    enabled: true
    server:
# mysql代理
      mysql-proxy:
        bind-port: 13306
        remote-host: remotehost
        remote-port: 3306
# http代理
  http:
    enabled: true
    bind-port: 8888
```

### 启动

前台启动
```bash
sh ./jproxy.sh start 

```
后台启动
```bash
sh ./jproxy.sh start -d
```

## 配置

### 配置tcp代理

```yaml
proxy:
  tcp:
    enabled: true
    server:
      mysql-proxy:
        bind-port: 13306
        remote-host: remotehost
        remote-port: 3306
      redis-proxy:
        bind-port: 16379
        remote-host: remotehost
        remote-port: 6379
      ...
```
proxy.tcp.server节点下可以增加任意多tcp代理,
需要注意bind-port不能相同

### 配置http代理

```yaml
proxy:
  http:
    enabled: true
    bind-port: 8888
    connect-timeout: 5s
    log: false
```
log是否开启http详细日志

### 配置https解密

```yaml
proxy:
  http:
    enabled: true
    bind-port: 8888
    ssl:
      ca-cert-path: 'file:ca/root_ca.cer'
      ca-private-key-path: 'file:ca/ca_private.der'
```
需要在浏览器导入root_ca.cer为可信    

#### 生成自签名证书步骤:

1. 生成rsa私钥
```bash
openssl genrsa -out private_key.pem 2048
```
2. 转换为pkcs8,der格式
```bash
openssl pkcs8 -topk8 -in private_key.pem -out ca_private.der -nocrypt -outform der
```
3. 生成自签名证书
```bash
openssl req -new -x509 -days 365 -key private_key.pem -out root_ca.cer
```

### 配置http二级代理

```yaml
proxy:
  http:
    enabled: true
    bind-port: 8888
    second-proxy:
      host: remotehost
      port: 1080
      type: socks5
```

## 管理员

通过telnet服务对代理进行管理

### telnet

默认启动telnet服务，默认端口绑定在23456
```yaml
admin:
  enabled: true
  server-name: admin-manager
  bind-port: 23456
```
通过telnet进行连接
```
➜  ~ telnet 127.0.0.1 23456
Trying 127.0.0.1...
Connected to 127.0.0.1.
Escape character is '^]'.
 _
|_._ _    /\ _._ _ o._
| | (_>\//--(_| | ||| |
       /

admin> 
```

### 命令

|command    |   description |
|-----------|----------------|
|  ls       | 列出所有代理    |
| start     | 启动代理        |
| alias     | 命名起别名      |
|create     | 动态创建代理    |
| quit      | 离开           | 
| close     | 关闭代理       |

### 授权
增加配置password
```yaml
admin:
  password: 123456

```

# 参考
[[netty官方文档]](https://netty.io/4.1/xref/io/netty/example/proxy/package-summary.html)   
[[github]](https://github.com/monkeyWie/proxyee)

