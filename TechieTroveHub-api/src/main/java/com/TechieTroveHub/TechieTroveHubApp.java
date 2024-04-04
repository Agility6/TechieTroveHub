package com.TechieTroveHub;

import com.TechieTroveHub.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ClassName: TechieTroveHubApp
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/14 16:48
 * @Version: 1.0
 */
@SpringBootApplication
@EnableAsync // 开启异步
@EnableScheduling // 定时任务
@EnableTransactionManagement // 事务管理
public class TechieTroveHubApp {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(TechieTroveHubApp.class, args);
        // websocket全局获取context
        WebSocketService.setApplicationContext(app);
    }
}
