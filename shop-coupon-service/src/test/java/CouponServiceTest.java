import com.xmy.CouponServiceApplication;
import com.xmy.api.ICouponService;
import com.xmy.pojo.ShopCoupon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CouponServiceApplication.class)
public class CouponServiceTest {
    @Autowired
    private ICouponService iCouponService;

    @Test
    public void confirmOrder() {
        Long couponId = 345988230098857984L;
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301376L;
//        ShopOrder order = new ShopOrder();
//        order.setGoodsId(goodsId);
//        order.setUserId(userId);
//        order.setCouponId(couponId);
//        order.setAddress("北京");
//        order.setGoodsNumber(1);
//        order.setGoodsPrice(new BigDecimal(1000));
//        order.setShippingFee(BigDecimal.ZERO);
//        order.setOrderAmount(new BigDecimal(1000));
//        order.setMoneyPaid(new BigDecimal(100));
        ShopCoupon one = iCouponService.findOne(couponId);
        System.out.println(one.getCouponPrice());
        System.out.println(one.getCouponId());


    }
}
