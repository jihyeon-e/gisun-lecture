package me.hyeon.springinit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SampleListener implements ApplicationRunner {

    @Autowired
    private String hello;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("===========");
        System.out.println(hello);
        System.out.println("===========");
    }
}
