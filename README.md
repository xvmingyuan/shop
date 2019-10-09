微服务模式订单支付系统

    应用技术: SpringBoot
             Mybatis
             Dubbo
             RocketMQ
             Zookeeper
             MySQL
    应用工具: Twitter-Snowflake
             RestTemplate
             
注意事项:

    1. mybatis 逆向工程数据表名字不能太通用,容易引起于系统表名冲突
        正例:  shop_user
        反例:  user 
    2. mybatis 逆向工程 *.xml文件生成是追加式添加,在重新生成 *.xml 时,建议删除原 *.xml
        xml内容重复,会抛出异常:java.lang.IllegalArgumentException: Result Maps collection already contains
    3. pojo类 需要继承Serializable,才可以使用dubboRPC通信
    4. mapper接口 需要添加注解 @Mapper 
    5. service.impl 服务需要添加:
        @Component :添加注解 'spring @Service' , 使用原因: 避免与dubbo @Service冲突
        @Service(interfaceClass = xxxxxxService.class) :该 service 为dubbo service
    6. application 需要添加Dubbo配置 @EnableDubboConfiguration
    7. 微服务模式下 dubbo.protocol端口不能重复,否则启动报错