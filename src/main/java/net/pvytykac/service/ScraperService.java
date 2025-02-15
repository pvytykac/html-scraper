package net.pvytykac.service;

import net.pvytykac.api.ScrapeRequest;
import net.pvytykac.api.ScrapedData;

/**
 * @author Paly
 * @since 2025-02-15
 */
public interface ScraperService {

    ScrapedData scrape(ScrapeRequest request);

}
