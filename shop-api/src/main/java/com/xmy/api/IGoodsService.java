package com.xmy.api;

import com.xmy.entity.Result;
import com.xmy.pojo.ShopGoods;
import com.xmy.pojo.ShopOrderGoodsLog;

public interface IGoodsService {
    /**
     * 根据ID 查询 Goods
     *
     * @param goodsId
     * @return
     */
    ShopGoods findOne(Long goodsId);

    /**
     * 扣减库存
     *
     * @param orderGoodsLog
     * @return
     */
    Result reduceGoodsNum(ShopOrderGoodsLog orderGoodsLog);
}
