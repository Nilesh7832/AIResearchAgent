package org.example.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ArxivService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public List<ArxivPaper> search(String query, int maxResults) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = "http://export.arxiv.org/api/query?search_query=all:"
                + encodedQuery + "&max_results=" + maxResults;

        Request request = new Request.Builder().url(url).build();

        int maxRetries = 5;
        int waitSeconds = 15;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (Response response = client.newCall(request).execute()) {
                if (response.code() == 429) {
                    System.out.println("arXiv rate limit hit, waiting " + waitSeconds
                            + "s (attempt " + attempt + "/" + maxRetries + ")...");
                    Thread.sleep(waitSeconds * 1000L);
                    waitSeconds *= 2; // exponential backoff
                    continue;
                }
                if (!response.isSuccessful()) {
                    throw new IOException("arXiv request failed: " + response.code());
                }
                String xml = response.body().string();
                return parseXml(xml);
            }
        }

        throw new IOException("arXiv request failed after " + maxRetries + " retries (rate limited).");
    }

    private List<ArxivPaper> parseXml(String xml) throws Exception {
        List<ArxivPaper> papers = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        NodeList entries = doc.getElementsByTagName("entry");

        for (int i = 0; i < entries.getLength(); i++) {
            Element entry = (Element) entries.item(i);

            String title = getTagText(entry, "title").replaceAll("\\s+", " ").trim();
            String summary = getTagText(entry, "summary").replaceAll("\\s+", " ").trim();
            String id = getTagText(entry, "id").trim();
            String published = getTagText(entry, "published").trim();

            List<String> authors = new ArrayList<>();
            NodeList authorNodes = entry.getElementsByTagName("author");
            for (int j = 0; j < authorNodes.getLength(); j++) {
                Element authorEl = (Element) authorNodes.item(j);
                authors.add(getTagText(authorEl, "name").trim());
            }

            papers.add(new ArxivPaper(title, summary, id, authors, published));
        }

        return papers;
    }

    private String getTagText(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent();
    }

    public record ArxivPaper(
            String title,
            String summary,
            String url,
            List<String> authors,
            String published
    ) {}
}