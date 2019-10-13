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
    
    8. 库存回退和扣减均存在并发问题,解决:
        方案1: 数据库乐观锁
        方案2: MQ+数据库乐观锁
        方案3: 引入分布式锁(Redis Zookeeper) (未使用)
        
    9. 8中解决方案2存在库存不足时,需要通过MQ异步通知订单服务问题
    
    10. RocketMQ分布式事务:
        方案1: rocketmq-client (推荐)
        方案2: rocketmq-spring-boot-starter(2.0.3) (事务有问题,未解决..)
        
        方案2问题测试:
            rocketMQTemplate.sendMessageInTransaction 方法发送的消息,
            在继承RocketMQLocalTransactionListener的类下方法
            executeLocalTransaction中的,RocketMQLocalTransactionState.ROLLBACK 状态回滚无效,
            消息仍然会被投递到方法 checkLocalTransaction,经过测试
            在checkLocalTransaction 执行RocketMQLocalTransactionState.ROLLBACK也不能回滚消息,
            消息被发送到队列,导致消费者消费了回滚消息.
            
        同类方案1中测试:
            TransactionMQProducer 的继承 TransactionListener的类下方法
            executeLocalTransaction中的,LocalTransactionState.ROLLBACK_MESSAGE 回滚消息,
            并不会将消息投递到方法 checkLocalTransaction,
            在消费者端消费不到回滚消息
            
         
            
    
        