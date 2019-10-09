import com.xmy.PayServiceApplication;
import com.xmy.api.IPayService;
import com.xmy.constant.ShopCode;
import com.xmy.pojo.ShopPay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServiceApplication.class)
public class PayServiceTest {

    @Autowired
    private IPayService payService;

    @Test
    public void createPayment() throws IOException {
        Long orderId = 379264159239507968L;

        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(600));
        payService.createPayment(pay);

        System.in.read();

    }

    @Test
    public void callbackPayment() throws IOException {
        Long orderId = 379264159239507968L;
        Long payId = 379265591703379968L;

        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        payService.callbackPayment(pay);

        System.in.read();

    }


}
