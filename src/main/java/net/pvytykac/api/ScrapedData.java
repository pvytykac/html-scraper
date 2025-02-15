package net.pvytykac.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * @author Paly
 * @since 2025-02-15
 */
@Value
public class ScrapedData {

    List<ScrapedEntry> fields;
    List<ScrapedEntry> metadata;

    @Value
    public static class ScrapedEntry {
        String name;
        String value;
    }
}
