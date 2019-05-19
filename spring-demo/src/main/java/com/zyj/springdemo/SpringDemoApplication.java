package com.zyj.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@EnableScheduling
@SpringBootApplication
public class SpringDemoApplication {


    @Scheduled(cron = "2/3 * * * * *")
    public void job1() {
        System.out.println("hello");
    }


    public static void main(String[] args) {
        SpringApplication.run(SpringDemoApplication.class, args);
    }

}
