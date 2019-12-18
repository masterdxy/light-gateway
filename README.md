### Light-Gateway
> Light-Gateway 是一个轻量级的服务网关，支持鉴权、限流、负载均衡的特性，上游支持REST、Dubbo等RPC协议。
#### 架构图
> TODO

#### 特性 
- 支持多种后端服务如Dubbo、SpringCloud、REST、GRPC(TODO)。
- 支持分布式限流，随机、加权轮训负载均衡算法(TODO)。
- 支持参数校验、请求过滤、地址重写、灰度发布、Mock测试、AK/PK认证等插件。
- 支持自动集群组建。
- 支持优雅停机。

#### 性能
1. Case 1 8Core 16G 后端Dubbo服务
2. Case 2 8Core 16G 后端SpringCloud服务
3. Case 3 8Core 16G Mock
#### 部署
> 所需环境: JDK1.8 MySQL Nacos
1. 编译代码: mvn clean package -DskipTests;
2. 按需修改application.properties配置文件;
3. 在nacos中新建namespace，并将nacos.data中的配置修改后保存;
4. 导入gateway.sql文件到MySQL中;
5. 以上操作检查完毕后，启动Gateway: sh bin/start.sh -daemon -env=dev
6. 终端执行curl --request GET \ --url http://gateway-host:8081/check_startup 如果返回 OK 则表明启动成功，否则根据返回错误进行修改;

#### Gateway管理
> 参考light-gateway-admin手册进行管理