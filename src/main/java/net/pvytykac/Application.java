package net.pvytykac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Paly
 * @since 2025-02-15
 */
@EnableAutoConfiguration
@SpringBootApplication
@EnableCaching
@ComponentScan("net.pvytykac")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
