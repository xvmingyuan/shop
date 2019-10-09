import com.xmy.UserServiceApplication;
import com.xmy.api.IUserService;
import com.xmy.pojo.ShopUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserServiceApplication.class)
public class UserServiceTest {
    @Autowired
    private IUserService iUserService;

    @Test
    public void findOne() {
        Long couponId = 345988230098857984L;
        Long goodsId = 378715381063495688L;
        Long userId = 378715381059301376L;
        ShopUser user = iUserService.findOne(userId);
        System.out.println(user.getUserName());
        System.out.println(user.getUserMobile());
        System.out.println(user.getUserId());


    }
}
