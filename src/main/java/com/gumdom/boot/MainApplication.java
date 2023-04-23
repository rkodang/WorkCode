package com.gumdom.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * SpringBoot Application ->Main Program
 */
@SpringBootApplication
public class MainApplication {
    public static void main(String[] args) {
        //1.返回所有的IOC容器
        ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class);
        //2.查看容器里面的组件
        String[] beanDefinitionNames = run.getBeanDefinitionNames();
    }
}
