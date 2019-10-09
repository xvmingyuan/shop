package com.xmy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xmy.api.IUserService;
import com.xmy.constant.ShopCode;
import com.xmy.entity.Result;
import com.xmy.exception.CastException;
import com.xmy.mapper.ShopUserMapper;
import com.xmy.mapper.ShopUserMoneyLogMapper;
import com.xmy.pojo.ShopUser;
import com.xmy.pojo.ShopUserMoneyLog;
import com.xmy.pojo.ShopUserMoneyLogExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@SuppressWarnings("ALL")
@Component
@Service(interfaceClass = IUserService.class)
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private ShopUserMapper userMapper;

    @Autowired
    private ShopUserMoneyLogMapper userMoneyLogMapper;

    @Override
    public ShopUser findOne(Long userId) {
        if (userId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public Result updateMoneyPaid(ShopUserMoneyLog userMoneyLog) {
        //1校验参数是否合法
        if (userMoneyLog == null ||
                userMoneyLog.getUserId() == null ||
                userMoneyLog.getOrderId() == null ||
                userMoneyLog.getUseMoney() == null ||
                userMoneyLog.getUseMoney().compareTo(BigDecimal.ZERO) <= 0) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        //2查询订单余额使用日志
        ShopUserMoneyLogExample userMoneyLogExample = new ShopUserMoneyLogExample();
        ShopUserMoneyLogExample.Criteria criteria = userMoneyLogExample.createCriteria();
        criteria.andOrderIdEqualTo(userMoneyLog.getOrderId());
        criteria.andUserIdEqualTo(userMoneyLog.getUserId());
        long r = userMoneyLogMapper.countByExample(userMoneyLogExample);

        ShopUser user = userMapper.selectByPrimaryKey(userMoneyLog.getUserId());

        //3扣减余额
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_PAID.getCode().intValue()) {
            if (r > 0) {//已经付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            //扣减余额
            user.setUserMoney(user.getUserMoney().subtract(userMoneyLog.getUseMoney()));
            userMapper.updateByPrimaryKey(user);
        }

        //4回退余额
        if (userMoneyLog.getMoneyLogType().intValue() == ShopCode.SHOP_USER_MONEY_REFUND.getCode().intValue()) {
            if (r < 0) {//没未付款
                CastException.cast(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
            }
            //防止多次退款
            ShopUserMoneyLogExample userMoneyLogExample1 = new ShopUserMoneyLogExample();
            ShopUserMoneyLogExample.Criteria criteria1 = userMoneyLogExample1.createCriteria();
            criteria1.andUserIdEqualTo(userMoneyLog.getUserId());
            criteria1.andOrderIdEqualTo(userMoneyLog.getOrderId());
            criteria1.andMoneyLogTypeEqualTo(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
            long r2 = userMoneyLogMapper.countByExample(userMoneyLogExample1);
            if (r2 > 0) {//已退过款
                CastException.cast(ShopCode.SHOP_USER_MONEY_REDUCE_ALREADY);
            }
            // 退款加余额
            user.setUserMoney(user.getUserMoney().add(userMoneyLog.getUseMoney()));
            userMapper.updateByPrimaryKey(user);
        }

        //5记录订单余额使用日志
        userMoneyLog.setCreateTime(new Date());
        Result result;
        try {
            userMoneyLogMapper.insert(userMoneyLog);
            result = new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
        } catch (Exception e) {
            log.info(e.toString());
            result = new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
        }
        return result;
    }
}
