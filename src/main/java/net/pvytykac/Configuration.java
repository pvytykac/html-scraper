package net.pvytykac;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * @author Paly
 * @since 2025-02-15
 */
@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    @Qualifier("browser-url")
    public String getBrowserUrl() {
        return System.getenv("BROWSER_URL");
    }

}
