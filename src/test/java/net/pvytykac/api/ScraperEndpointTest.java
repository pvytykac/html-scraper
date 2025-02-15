package net.pvytykac.api;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.BrowserWebDriverContainer;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Paly
 * @since 2025-02-15
 */
@SpringBootTest(classes = {ScraperEndpointTest.TestConfigurationOverride.class})
@AutoConfigureMockMvc
class ScraperEndpointTest {

    private final MockMvc mvc;

    //todo: migrate to junit5 way
    @ClassRule
    public static BrowserWebDriverContainer<?> BROWSER = new BrowserWebDriverContainer<>()
            .withCapabilities(new ChromeOptions());

    static {
        BROWSER.start();
    }

    @Autowired
    public ScraperEndpointTest(MockMvc mvc) {
        this.mvc = mvc;
    }

    @Test
    void postScrape() throws Exception {
        var json = new JSONObject()
                .put("url", "https://dennikn.sk")
                .put("fields", new JSONArray()
                        .put(new JSONObject()
                                .put("name", "footer-text")
                                .put("selector", ".n3_footer_legal")))
                .put("metadata", new JSONArray()
                        .put(new JSONObject()
                                .put("name", "viewport")));

        var request = post("/v1/scraper/scrape-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fields[0].name", equalTo("footer-text")))
                .andExpect(jsonPath("$.fields[0].value", equalTo("Copyright © 2025 N Press, s. r. o. (informácie o ochrane autorských práv)")))
                .andExpect(jsonPath("$.metadata[0].name", equalTo("viewport")))
                .andExpect(jsonPath("$.metadata[0].value", equalTo("width=device-width, viewport-fit=cover, initial-scale=1.0")));
    }

    @Test
    void postScrape_InvalidPayload() throws Exception {
        var json = new JSONObject()
                .put("url", "invalid-url-lol")
                .put("fields", new JSONArray().put(new JSONObject()))
                .put("metadata", new JSONArray().put(new JSONObject()));

        var request = post("/v1/scraper/scrape-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toString());

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'url')].message", equalTo(singletonList("must be a valid URL"))))
                .andExpect(jsonPath("$.errors[?(@.field == 'fields[0].name')].message", equalTo(singletonList("must not be blank"))))
                .andExpect(jsonPath("$.errors[?(@.field == 'fields[0].selector')].message", equalTo(singletonList("must not be blank"))))
                .andExpect(jsonPath("$.errors[?(@.field == 'metadata[0].name')].message", equalTo(singletonList("must not be blank"))));
    }

    @TestConfiguration
    public static class TestConfigurationOverride {
        @Bean
        @Qualifier("browser-url")
        public String getBrowserUrl() {
            return ScraperEndpointTest.BROWSER.getSeleniumAddress().toString();
        }
    }
}