package org.example.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class PdfService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public String downloadAndExtractText(String arxivAbsUrl) throws IOException {
        // arXiv abstract URL ko PDF URL mein convert karo
        // Example: http://arxiv.org/abs/2306.07441v1 -> https://arxiv.org/pdf/2306.07441v1
        String pdfUrl = arxivAbsUrl.replace("/abs/", "/pdf/");

        Request request = new Request.Builder().url(pdfUrl).build();

        Path tempFile = Files.createTempFile("paper_", ".pdf");

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("PDF download failed: " + response.code());
            }
            Files.write(tempFile, response.body().bytes());
        }

        String text;
        try (PDDocument document = Loader.loadPDF(tempFile.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        }

        Files.deleteIfExists(tempFile);

        return text;
    }
}