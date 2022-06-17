CREATE TABLE `shop_coupon` (
  `coupon_id` bigint(50) NOT NULL COMMENT '优惠券ID',
  `coupon_price` decimal(10,2) DEFAULT NULL COMMENT '优惠券金额',
  `user_id` bigint(50) DEFAULT NULL COMMENT '用户ID',
  `order_id` bigint(32) DEFAULT NULL COMMENT '订单ID',
  `is_used` int(1) DEFAULT NULL COMMENT '是否使用,0未使用,1使用',
  `used_time` timestamp NULL DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

CREATE TABLE `shop_goods` (
  `goods_id` bigint(50) NOT NULL COMMENT '主键',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '名称',
  `goods_number` int(11) DEFAULT NULL COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `goods_desc` varchar(255) DEFAULT NULL COMMENT '描述',
  `add_time` timestamp NULL DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

CREATE TABLE `shop_msg_consumer` (
  `msg_id` varchar(50) NOT NULL DEFAULT '',
  `group_name` varchar(100) NOT NULL DEFAULT '',
  `msg_tag` varchar(100) NOT NULL DEFAULT '',
  `msg_key` varchar(100) NOT NULL DEFAULT '',
  `msg_body` varchar(500) DEFAULT NULL,
  `consumer_status` int(1) DEFAULT NULL COMMENT '0:正在处理;1:处理成功;2处理失败',
  `consumer_times` int(1) DEFAULT NULL COMMENT '消费次数',
  `consumer_timestamp` timestamp NULL DEFAULT NULL COMMENT '消费时间',
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`group_name`,`msg_tag`,`msg_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息消费者表';

CREATE TABLE `shop_msg_provider` (
  `id` varchar(100) NOT NULL DEFAULT '',
  `group_name` varchar(100) NOT NULL DEFAULT '',
  `msg_topic` varchar(100) DEFAULT NULL,
  `msg_tag` varchar(100) NOT NULL DEFAULT '',
  `msg_key` varchar(100) NOT NULL DEFAULT '',
  `msg_body` varchar(500) DEFAULT NULL,
  `msg_status` int(1) DEFAULT NULL COMMENT '0:未处理;1:已处理',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`group_name`,`msg_tag`,`msg_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息生产者表';

CREATE TABLE `shop_order` (
  `order_id` bigint(50) NOT NULL,
  `user_id` bigint(50) DEFAULT NULL,
  `order_status` int(1) DEFAULT NULL COMMENT '订单状态 0未确认 1已确认 2已取消 3无效 4已退货 5已完成 6异常',
  `pay_status` int(1) DEFAULT NULL COMMENT '支付状态 0未支付 1支付中 2已支付',
  `shipping_status` int(11) DEFAULT NULL COMMENT '发货状态 0未发货 1已发货 2已退款 3未收货 4已收货',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `consignee` varchar(255) DEFAULT NULL COMMENT '收货人',
  `goods_id` bigint(50) DEFAULT NULL COMMENT '商品ID',
  `goods_number` int(11) DEFAULT NULL COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `goods_amount` decimal(10,2) DEFAULT NULL COMMENT '商品总价',
  `shipping_fee` decimal(10,2) DEFAULT NULL COMMENT '运费',
  `order_amount` decimal(10,2) DEFAULT NULL COMMENT '订单价格',
  `coupon_id` bigint(50) DEFAULT NULL COMMENT '优惠券ID',
  `coupon_paid` decimal(10,2) DEFAULT NULL COMMENT '优惠券',
  `money_paid` decimal(10,2) DEFAULT NULL COMMENT '已付金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `add_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `confirm_time` timestamp NULL DEFAULT NULL COMMENT '订单确认时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE `shop_order_goods_log` (
  `goods_id` bigint(50) NOT NULL COMMENT '商品ID',
  `order_id` bigint(32) NOT NULL COMMENT '订单ID',
  `goods_number` int(11) DEFAULT NULL COMMENT '库存数量',
  `log_time` datetime DEFAULT NULL COMMENT '记录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品日志表';

CREATE TABLE `shop_order_mq_status_log` (
  `order_id` bigint(50) unsigned NOT NULL,
  `goods_status` int(2) DEFAULT NULL COMMENT '1:成功 0:失败 -1:未知',
  `goods_result` varchar(255) DEFAULT NULL,
  `coupon_status` int(2) DEFAULT NULL COMMENT '1:成功 0:失败 -1:未知',
  `coupon_result` varchar(11) DEFAULT NULL,
  `user_money_status` int(2) DEFAULT NULL COMMENT '1:成功 0:失败 -1:未知',
  `user_result` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态记录表';

CREATE TABLE `shop_pay` (
  `pay_id` bigint(50) NOT NULL COMMENT '支付编号',
  `order_id` bigint(50) DEFAULT NULL COMMENT '订单编号',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_paid` int(1) DEFAULT NULL COMMENT '是否已支付 0未付款 1付款中 2已付款',
  PRIMARY KEY (`pay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付表';

CREATE TABLE `shop_user` (
  `user_id` bigint(50) NOT NULL COMMENT '用户ID',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `user_password` varchar(255) DEFAULT NULL COMMENT '密码',
  `user_mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `user_score` int(11) DEFAULT NULL COMMENT '积分',
  `user_reg_time` timestamp NULL DEFAULT NULL COMMENT '注册时间',
  `user_money` decimal(10,2) DEFAULT NULL COMMENT '用户余额',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `shop_user_money_log` (
  `user_id` bigint(50) NOT NULL,
  `order_id` bigint(50) NOT NULL DEFAULT '0',
  `money_log_type` int(1) unsigned DEFAULT NULL COMMENT '日志类型 1订单付款 2订单退款',
  `use_money` decimal(10,2) DEFAULT NULL COMMENT '操作金额',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '日志时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户余额日志表';

