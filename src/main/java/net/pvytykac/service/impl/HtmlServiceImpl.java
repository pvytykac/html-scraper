package net.pvytykac.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pvytykac.service.HtmlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Paly
 * @since 2025-02-15
 */
@Slf4j
@Component
public class HtmlServiceImpl implements HtmlService {

    private final String browserHostname;

    @Autowired
    public HtmlServiceImpl(@Qualifier("browser-url") String browserHostname) {
        this.browserHostname = browserHostname;
    }

    @Override
    @Cacheable("html")
    public Html fetchDocument(String url) {
        log.info("fetching html from '{}'", url);

        WebDriver driver = null;
        try {
            driver = new RemoteWebDriver(URI.create(browserHostname).toURL(), new ChromeOptions());
            driver.get(url);
            return new Html(driver.getPageSource());
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        } finally {
            Optional.ofNullable(driver).ifPresent(WebDriver::quit);
        }
    }

    @Scheduled(fixedRate = 5L, timeUnit = TimeUnit.MINUTES)
    @CacheEvict("html")
    public void evictCache() {
        log.info("dropping all cached html files from cache");
    }

    @Slf4j
    public static final class Html {

        private final Document document;

        public Html(String html) {
            log.debug("{}", html);
            this.document = Jsoup.parse(html);
        }

        public Optional<String> getTextByCss(String selector) {
            log.info("looking up element by css selector '{}'", selector);

            return Optional.ofNullable(document.select(selector).first())
                    .filter(Element::hasText)
                    .map(Element::text);
        }

        public Optional<String> getMetadataByName(String name) {
            log.info("looking up metadata by name '{}'", name);

            return Optional.ofNullable(document.selectXpath("//html/head/meta[@name='" + name + "']").first())
                    .map(meta -> meta.attr("content"));
        }
    }
}
