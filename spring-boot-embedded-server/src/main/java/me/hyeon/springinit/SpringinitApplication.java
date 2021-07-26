package me.hyeon.springinit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HyeonProperties.class)
public class SpringinitApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringinitApplication.class, args);
    }
}