package net.pvytykac.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pvytykac.service.HtmlService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
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
        try {
            var response = Jsoup.connect(url)
                    .ignoreHttpErrors(true)
                    .execute();

            if (response.statusCode() != 200) {
                log.warn("unexpected response from '{}': {} {}", url, response.statusCode(), response.body());
                throw new IllegalStateException("UnexpectedStatusCode");
            }

            return new Html(response.parse());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    //todo: implement actual TTL eviction
    @CacheEvict(value = "html", allEntries = true)
    @Scheduled(fixedRate = 30000L)
    public void clearCache() {
        log.info("clearing the html cache");
    }

    @Slf4j
    public static final class Html {

        private final Document document;

        public Html(Document document) {
            this.document = document;
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
