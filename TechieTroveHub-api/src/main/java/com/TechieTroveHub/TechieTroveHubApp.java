package com.TechieTroveHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ClassName: TechieTroveHubApp
 * Description:
 *
 * @Author agility6
 * @Create 2024/3/14 16:48
 * @Version: 1.0
 */
@SpringBootApplication
public class TechieTroveHubApp {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(TechieTroveHubApp.class, args);
    }
}
