package org.example.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WebSearchService {

    // TODO: Apni Brave API key yaha paste karo
    private static final String API_KEY =  System.getenv("BRAVE_API_KEY");

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public record WebResult(String title, String url, String description) {}

    public List<WebResult> search(String query, int maxResults) throws IOException {
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IOException("BRAVE_API_KEY environment variable not set. See README for setup instructions.");
        }

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://api.search.brave.com/res/v1/web/search?q="
                + encodedQuery + "&count=" + maxResults;



        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Subscription-Token", API_KEY)
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Brave Search request failed: " + response.code());
            }
            String body = response.body().string();
            return parseResults(body);
        }
    }

    private List<WebResult> parseResults(String json) throws IOException {
        List<WebResult> results = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        JsonNode webResults = root.path("web").path("results");

        for (JsonNode item : webResults) {
            String title = item.has("title") ? item.get("title").asText() : "Untitled";
            String url = item.has("url") ? item.get("url").asText() : "";
            String description = item.has("description") ? item.get("description").asText() : "";
            results.add(new WebResult(title, url, description));
        }

        return results;
    }
}