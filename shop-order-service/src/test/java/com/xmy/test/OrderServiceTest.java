package com.xmy.test;

import com.xmy.OrderServiceApplication;
import com.xmy.api.IOrderService;
import com.xmy.entity.Result;
import com.xmy.pojo.ShopOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderServiceApplication.class)
public class OrderServiceTest {
    @Autowired
    private IOrderService orderService;

    @Test
    public void confirmOrder() throws Exception {
        ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch cdAnswer = new CountDownLatch(2);
        Thread t1 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = orderService.confirmOrder(A());
                System.out.println(result);
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = orderService.confirmOrder(B());
                System.out.println(result);
            }
        };
        Thread t3 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = orderService.confirmOrder(C());
                System.out.println(result);
            }
        };
        Thread t4 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = orderService.confirmOrder(D());
                System.out.println(result);
            }
        };
        Thread t5 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = orderService.confirmOrder(E());
                System.out.println(result);
            }
        };
        service.execute(t1);
        service.execute(t2);
        service.execute(t3);
        service.execute(t4);
        service.execute(t5);
        cdAnswer.await();

        System.in.read();


    }

    public static ShopOrder A() {
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301373L;
        ShopOrder order = new ShopOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setCouponId(null);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setGoodsAmount(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(0));
        return order;
    }

    public static ShopOrder B() {
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301374L;
        ShopOrder order = new ShopOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setCouponId(null);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setGoodsAmount(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(100));
        return order;
    }

    public static ShopOrder C() {
        Long couponId = 345988230098857981L;
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301375L;
        ShopOrder order = new ShopOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setCouponId(couponId);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setGoodsAmount(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(0));
        return order;
    }

    public static ShopOrder D() {
        Long couponId = 345988230098857984L;
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301376L;
        ShopOrder order = new ShopOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setCouponId(couponId);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setGoodsAmount(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(100));
        return order;
    }

    public static ShopOrder E() {
        Long couponId = 345988230098857982L;
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301377L;
        ShopOrder order = new ShopOrder();
        order.setGoodsId(goodsId);
        order.setUserId(userId);
        order.setCouponId(couponId);
        order.setAddress("北京");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000));
        order.setGoodsAmount(new BigDecimal(1000));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(1000));
        order.setMoneyPaid(new BigDecimal(100));
        return order;
    }


}
