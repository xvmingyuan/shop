import com.alibaba.fastjson.JSON;
import com.xmy.PayServiceApplication;
import com.xmy.api.IPayService;
import com.xmy.constant.ShopCode;
import com.xmy.entity.PayResult;
import com.xmy.entity.Result;
import com.xmy.pojo.ShopPay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Test
    public void createPaymentThread() throws Exception {
        ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch cdAnswer = new CountDownLatch(2);
        Thread t1 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.createPayment(A());
                if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                    String message = result.getMessage();
                    PayResult payResult = JSON.parseObject(message, PayResult.class);
                    Long orderId = payResult.getOrderId();
                    Long payId = payResult.getPayId();
                    ShopPay pay = new ShopPay();
                    pay.setPayId(payId);
                    pay.setOrderId(orderId);
                    pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                    Result result1 = payService.callbackPayment(pay);
                    System.out.println(result);
                    System.out.println(result1);
                }

            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.createPayment(B());
                if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                    String message = result.getMessage();
                    PayResult payResult = JSON.parseObject(message, PayResult.class);
                    Long orderId = payResult.getOrderId();
                    Long payId = payResult.getPayId();
                    ShopPay pay = new ShopPay();
                    pay.setPayId(payId);
                    pay.setOrderId(orderId);
                    pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                    Result result1 = payService.callbackPayment(pay);
                    System.out.println(result);
                    System.out.println(result1);
                }
            }
        };
        Thread t3 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.createPayment(C());
                if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                    String message = result.getMessage();
                    PayResult payResult = JSON.parseObject(message, PayResult.class);
                    Long orderId = payResult.getOrderId();
                    Long payId = payResult.getPayId();
                    ShopPay pay = new ShopPay();
                    pay.setPayId(payId);
                    pay.setOrderId(orderId);
                    pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                    Result result1 = payService.callbackPayment(pay);
                    System.out.println(result);
                    System.out.println(result1);
                }
            }
        };
        Thread t4 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.createPayment(D());
                if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                    String message = result.getMessage();
                    PayResult payResult = JSON.parseObject(message, PayResult.class);
                    Long orderId = payResult.getOrderId();
                    Long payId = payResult.getPayId();
                    ShopPay pay = new ShopPay();
                    pay.setPayId(payId);
                    pay.setOrderId(orderId);
                    pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                    Result result1 = payService.callbackPayment(pay);
                    System.out.println(result);
                    System.out.println(result1);
                }
            }
        };
        Thread t5 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.createPayment(E());
                if (result.getSuccess() == ShopCode.SHOP_SUCCESS.getSuccess()) {
                    String message = result.getMessage();
                    PayResult payResult = JSON.parseObject(message, PayResult.class);
                    Long orderId = payResult.getOrderId();
                    Long payId = payResult.getPayId();
                    ShopPay pay = new ShopPay();
                    pay.setPayId(payId);
                    pay.setOrderId(orderId);
                    pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
                    Result result1 = payService.callbackPayment(pay);
                    System.out.println(result);
                    System.out.println(result1);
                }
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

    @Test
    public void callbackPaymentThread() throws Exception {
        ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch cdAnswer = new CountDownLatch(2);
        Thread t1 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.callbackPayment(AA());
                System.out.println(result);
            }
        };
        Thread t2 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.callbackPayment(BB());
                System.out.println(result);
            }
        };
        Thread t3 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.callbackPayment(CC());
                System.out.println(result);
            }
        };
        Thread t4 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.callbackPayment(DD());
                System.out.println(result);
            }
        };
        Thread t5 = new Thread() {
            @Override
            public void run() {
                cdAnswer.countDown();
                Result result = payService.callbackPayment(EE());
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

    public ShopPay A() {
        Long orderId = 380765911960915968L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(700.00));
        return pay;
    }

    public ShopPay B() {
        Long orderId = 380765911948333056L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(900.00));
        return pay;
    }

    public ShopPay C() {
        Long orderId = 380765912036413440L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(600));
        return pay;
    }

    public ShopPay D() {
        Long orderId = 380765911990276096L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(900.00));
        return pay;
    }

    public ShopPay E() {
        Long orderId = 380765911960915969L;
        ShopPay pay = new ShopPay();
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        pay.setPayAmount(new BigDecimal(1000.00));
        return pay;
    }

    public ShopPay AA() {
        Long orderId = 380765911960915968L;
        Long payId = 379265591703379968L;
        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        return pay;
    }

    public ShopPay BB() {
        Long orderId = 380765911948333056L;
        Long payId = 379265591703379968L;
        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        return pay;
    }

    public ShopPay CC() {
        Long orderId = 380765912036413440L;
        Long payId = 379265591703379968L;
        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        return pay;
    }

    public ShopPay DD() {
        Long orderId = 380765911990276096L;
        Long payId = 379265591703379968L;
        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        return pay;
    }

    public ShopPay EE() {
        Long orderId = 380765911960915969L;
        Long payId = 379265591703379968L;
        ShopPay pay = new ShopPay();
        pay.setPayId(payId);
        pay.setOrderId(orderId);
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        return pay;
    }

}
