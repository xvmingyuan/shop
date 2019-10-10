package com.xmy;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import com.xmy.utils.IDWorker;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

    @Bean
    public ThreadPoolTaskExecutor getThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("Pool_A");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;

    }
}
