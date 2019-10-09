package com.xmy.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.xmy.api.ICouponService;
import com.xmy.constant.ShopCode;
import com.xmy.entity.Result;
import com.xmy.exception.CastException;
import com.xmy.mapper.ShopCouponMapper;
import com.xmy.pojo.ShopCoupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 *
 * @author xmy
 * @date 2019-10-09 21:44
 */
@Component
@Service(interfaceClass = ICouponService.class)
@Slf4j
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private ShopCouponMapper couponMapper;

    @Override
    public ShopCoupon findOne(Long couponId) {
        if (couponId == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        return couponMapper.selectByPrimaryKey(couponId);
    }

    @Override
    public Result updateCouponStatus(ShopCoupon coupon) {

        if (coupon == null || coupon.getCouponId() == null) {
            CastException.cast(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        Result result;
        try {
            //更新优惠券状态
            couponMapper.updateByPrimaryKey(coupon);
            result = new Result(ShopCode.SHOP_SUCCESS.getSuccess(), ShopCode.SHOP_SUCCESS.getMessage());
        } catch (Exception e) {
            log.info(e.toString());
            result = new Result(ShopCode.SHOP_FAIL.getSuccess(), ShopCode.SHOP_FAIL.getMessage());
        }

        return result;
    }
}
