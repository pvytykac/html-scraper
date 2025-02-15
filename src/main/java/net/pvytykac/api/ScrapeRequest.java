package net.pvytykac.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.hibernate.validator.constraints.URL;

import java.util.List;

/**
 * @author Paly
 * @since 2025-02-15
 */
@Value
public class ScrapeRequest {

    @URL
    @NotNull
    String url;
    @Valid
    List<FieldRequest> fields;
    @Valid
    List<MetadataRequest> metadata;

    @Value
    public static class FieldRequest {
        @NotBlank
        String name;
        @NotBlank
        String selector;
    }

    @Value
    public static class MetadataRequest {
        @NotBlank
        String name;
    }
}
