package net.pvytykac.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pvytykac.service.HtmlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Paly
 * @since 2025-02-15
 */
@Slf4j
@Component
public class HtmlServiceImpl implements HtmlService {

    @Override
    @Cacheable("html")
    public Html fetchDocument(String url) {
        log.info("fetching html from '{}'", url);

        var driver = new ChromeDriver();

        try {
            driver.get(url);
            return new Html(driver.getPageSource());
        } finally {
            driver.quit();
        }
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
