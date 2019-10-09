package com.xmy.api;

import com.xmy.entity.Result;
import com.xmy.pojo.ShopUser;
import com.xmy.pojo.ShopUserMoneyLog;

public interface IUserService {
    /**
     * 根据ID查询用户
     *
     * @param userId
     * @return
     */
    ShopUser findOne(Long userId);

    /**
     * 更新用户余额
     *
     * @param userMoneyLog
     * @return
     */
    Result updateMoneyPaid(ShopUserMoneyLog userMoneyLog);
}
