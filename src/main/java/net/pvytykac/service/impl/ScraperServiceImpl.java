package net.pvytykac.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.pvytykac.api.ScrapeRequest;
import net.pvytykac.api.ScrapedData;
import net.pvytykac.api.ScrapedData.ScrapedEntry;
import net.pvytykac.service.HtmlService;
import net.pvytykac.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * @author Paly
 * @since 2025-02-15
 */
@Slf4j
@Component
public class ScraperServiceImpl implements ScraperService {

    private final HtmlService service;

    @Autowired
    public ScraperServiceImpl(HtmlService service) {
        this.service = service;
    }

    @Override
    public ScrapedData scrape(ScrapeRequest request) {
        log.info("scraping '{}' for fields '{}' and metadata '{}'", request.getUrl(), request.getFields(),
                request.getMetadata());

        var html = service.fetchDocument(request.getUrl());

        var fields = scrapeFields(html, getListOrEmpty(request.getFields()));
        var metadata = scrapeMetadata(html, getListOrEmpty(request.getMetadata()));


        return new ScrapedData(fields, metadata);
    }

    private List<ScrapedEntry> scrapeFields(HtmlServiceImpl.Html html, List<ScrapeRequest.FieldRequest> requests) {
        return requests.stream()
                .map(field -> new ScrapedEntry(field.getName(), html.getTextByCss(field.getSelector()).orElse(null)))
                .toList();
    }

    private List<ScrapedEntry> scrapeMetadata(HtmlServiceImpl.Html html, List<ScrapeRequest.MetadataRequest> requests) {
        return requests.stream()
                .map(md -> new ScrapedEntry(md.getName(), html.getMetadataByName(md.getName()).orElse(null)))
                .toList();
    }

    private static <T> List<T> getListOrEmpty(List<T> list) {
        return Optional.ofNullable(list)
                .orElse(emptyList());
    }
}
