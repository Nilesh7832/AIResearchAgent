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

public class SemanticScholarService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public List<ArxivService.ArxivPaper> search(String query, int maxResults) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "https://api.semanticscholar.org/graph/v1/paper/search?query="
                + encodedQuery + "&limit=" + maxResults
                + "&fields=title,abstract,url,authors,year";

        Request request = new Request.Builder().url(url).build();

        int maxRetries = 5;
        int waitSeconds = 15;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 429 || response.code() == 503) {
                    System.out.println("Semantic Scholar rate limit hit, waiting " + waitSeconds
                            + "s (attempt " + attempt + "/" + maxRetries + ")...");
                    Thread.sleep(waitSeconds * 1000L);
                    waitSeconds *= 2;
                    continue;
                }
                if (!response.isSuccessful()) {
                    throw new IOException("Semantic Scholar request failed: " + response.code());
                }
                String body = response.body().string();
                return parseResults(body);
            }
        }

        throw new IOException("Semantic Scholar request failed after " + maxRetries + " retries (rate limited).");
    }

    private List<ArxivService.ArxivPaper> parseResults(String json) throws IOException {
        List<ArxivService.ArxivPaper> papers = new ArrayList<>();
        JsonNode root = mapper.readTree(json);
        JsonNode data = root.get("data");

        if (data == null) return papers;

        for (JsonNode paper : data) {
            String title = paper.has("title") ? paper.get("title").asText() : "Untitled";
            String abstractText = paper.has("abstract") && !paper.get("abstract").isNull()
                    ? paper.get("abstract").asText() : "No abstract available.";
            String url = paper.has("url") ? paper.get("url").asText() : "";
            String year = paper.has("year") && !paper.get("year").isNull()
                    ? paper.get("year").asText() : "Unknown";

            List<String> authors = new ArrayList<>();
            if (paper.has("authors")) {
                for (JsonNode author : paper.get("authors")) {
                    authors.add(author.get("name").asText());
                }
            }

            papers.add(new ArxivService.ArxivPaper(title, abstractText, url, authors, year));
        }

        return papers;
    }
}