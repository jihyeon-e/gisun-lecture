package me.hyeon.springinit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringinitApplicationTests {

    @Autowired
    Environment environment;

    @Test
    public void contextLoads() {
        //assertThat(environment.getProperty("hyeon.name"))
        //.isEqualTo("hyeon");
    }

}
