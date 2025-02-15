package net.pvytykac.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.pvytykac.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Paly
 * @since 2025-02-15
 */
@RestController
@RequestMapping("/v1/scraper")
public class ScraperEndpoint {

    private final ScraperService service;

    @Autowired
    public ScraperEndpoint(ScraperService service) {
        this.service = service;
    }

    @PostMapping("/scrape-data")
    public ScrapedData scrapeData(@RequestBody @Valid @NotNull ScrapeRequest payload) {
        return service.scrape(payload);
    }
}
