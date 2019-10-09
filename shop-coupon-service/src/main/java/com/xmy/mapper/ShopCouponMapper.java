package com.xmy.mapper;

import com.xmy.pojo.ShopCoupon;
import com.xmy.pojo.ShopCouponExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ShopCouponMapper {
    long countByExample(ShopCouponExample example);

    int deleteByExample(ShopCouponExample example);

    int deleteByPrimaryKey(Long couponId);

    int insert(ShopCoupon record);

    int insertSelective(ShopCoupon record);

    List<ShopCoupon> selectByExample(ShopCouponExample example);

    ShopCoupon selectByPrimaryKey(Long couponId);

    int updateByExampleSelective(@Param("record") ShopCoupon record, @Param("example") ShopCouponExample example);

    int updateByExample(@Param("record") ShopCoupon record, @Param("example") ShopCouponExample example);

    int updateByPrimaryKeySelective(ShopCoupon record);

    int updateByPrimaryKey(ShopCoupon record);
}