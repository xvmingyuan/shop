package com.xmy;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.xmy.utils.IDWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
/**
 *
 * @author xmy
 * @date 2019-10-09 21:44
 */
@SpringBootApplication
@EnableDubboConfiguration
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public IDWorker getBean() {
        return new IDWorker(1, 1);
    }
}
