package pers.zhixilang.lego.rpc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.zhixilang.lego.rpc.pojo.User;
import pers.zhixilang.lego.rpc.service.UserRpc;
import pers.zhixilang.lego.rpc.annotation.EnableRpc;

import javax.annotation.Resource;

/**
 * @author zhixilang
 * @version 1.0.0
 * date 2022-03-08 20:04
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@SpringBootConfiguration
@EnableRpc
public class ConsumerApplicationTest {

    @Resource
    private UserRpc userRpc;

    @Test
    public void main() {
        User user = userRpc.get("1");
        System.out.println(user);
    }
}
