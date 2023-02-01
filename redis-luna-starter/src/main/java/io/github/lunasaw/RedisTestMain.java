package io.github.lunasaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * @author chenzhangyue
 * ${DATE}
 */
@SpringBootApplication
@ShellComponent
public class RedisTestMain {
    public static void main(String[] args) {
        SpringApplication.run(RedisTestMain.class, args);
    }

    @ShellMethod("Say hello")
    public void hello(String name) {
        System.out.println("hello, " + name + "!");
    }
}