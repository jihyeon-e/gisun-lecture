package me.whiteship.springinit;


import org.junit.jupiter.api.Test;

public class TestTests {

    @Test
    void test() {
        Toto toto = new Toto();

        int plus = toto.plus(1, 3);
        assertEqua(plus, 5);

    }
}
