package org.example.agents;

import org.example.services.ArxivService.ArxivPaper;
import org.example.services.WebSearchService.WebResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class ReportAgent {

    public String buildReport(String query,
                              List<String> tasks,
                              List<ArxivPaper> papers,
                              List<String> analyses,
                              String factCheckResult) {

        StringBuilder md = new StringBuilder();

        md.append("# Research Report\n\n");
        md.append("**Query:** ").append(query).append("\n\n");
        md.append("**Date:** ").append(LocalDate.now()).append("\n\n");

        md.append("## Research Plan\n\n");
        for (String task : tasks) {
            md.append("- ").append(task).append("\n");
        }
        md.append("\n");

        md.append("## Sources Found\n\n");
        for (ArxivPaper p : papers) {
            md.append("- **").append(p.title()).append("** — ")
                    .append(p.authors()).append(" (").append(p.published()).append(")\n")
                    .append("  ").append(p.url()).append("\n");
        }
        md.append("\n");

        md.append("## Findings\n\n");
        for (int i = 0; i < papers.size(); i++) {
            md.append("### ").append(papers.get(i).title()).append("\n\n");
            md.append(analyses.get(i)).append("\n\n");
        }

        md.append("## Cross-Source Comparison\n\n");
        md.append(factCheckResult).append("\n\n");

        md.append("## References\n\n");
        int refNum = 1;
        for (ArxivPaper p : papers) {
            md.append(refNum++).append(". ").append(p.title())
                    .append(". ").append(p.url()).append("\n");
        }

        return md.toString();
    }

    public String buildHtmlReport(String query,
                                  List<String> tasks,
                                  List<ArxivPaper> papers,
                                  List<String> analyses,
                                  String factCheckResult,
                                  List<WebResult> webResults) {

        StringBuilder html = new StringBuilder();

        html.append("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
            <meta charset="UTF-8">
            <title>AI Research Report</title>
            <style>
                body {
                    font-family: 'Segoe UI', Arial, sans-serif;
                    max-width: 900px;
                    margin: 40px auto;
                    padding: 20px;
                    background: #f5f5f7;
                    color: #1a1a1a;
                    line-height: 1.6;
                }
                h1 {
                    color: #2c3e50;
                    border-bottom: 3px solid #3498db;
                    padding-bottom: 10px;
                }
                h2 {
                    color: #34495e;
                    margin-top: 40px;
                    border-left: 4px solid #3498db;
                    padding-left: 10px;
                }
                h3 {
                    color: #2c3e50;
                    margin-top: 25px;
                }
                .meta {
                    color: #7f8c8d;
                    font-size: 14px;
                    margin-bottom: 20px;
                }
                .card {
                    background: white;
                    border-radius: 8px;
                    padding: 20px;
                    margin: 15px 0;
                    box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                }
                a {
                    color: #3498db;
                    text-decoration: none;
                }
                a:hover {
                    text-decoration: underline;
                }
                ul {
                    padding-left: 20px;
                }
                .analysis-text {
                    white-space: pre-wrap;
                    background: #fafafa;
                    padding: 15px;
                    border-radius: 6px;
                    border-left: 3px solid #2ecc71;
                }
                .factcheck-text {
                    white-space: pre-wrap;
                    background: #fff8e1;
                    padding: 15px;
                    border-radius: 6px;
                    border-left: 3px solid #f39c12;
                }
            </style>
            </head>
            <body>
            """);

        html.append("<h1>Research Report</h1>");
        html.append("<div class='meta'><strong>Query:</strong> ").append(escapeHtml(query)).append("<br>");
        html.append("<strong>Date:</strong> ").append(LocalDate.now()).append("</div>");

        html.append("<h2>Research Plan</h2><div class='card'><ul>");
        for (String task : tasks) {
            html.append("<li>").append(escapeHtml(task)).append("</li>");
        }
        html.append("</ul></div>");

        html.append("<h2>Academic Sources (arXiv)</h2><div class='card'><ul>");
        for (ArxivPaper p : papers) {
            html.append("<li><strong>").append(escapeHtml(p.title())).append("</strong><br>")
                    .append(escapeHtml(p.authors().toString())).append(" (").append(p.published()).append(")<br>")
                    .append("<a href='").append(p.url()).append("' target='_blank'>").append(p.url()).append("</a></li><br>");
        }
        html.append("</ul></div>");

        html.append("<h2>Findings</h2>");
        for (int i = 0; i < papers.size(); i++) {
            html.append("<h3>").append(escapeHtml(papers.get(i).title())).append("</h3>");
            html.append("<div class='card analysis-text'>").append(escapeHtml(analyses.get(i))).append("</div>");
        }

        html.append("<h2>Cross-Source Comparison</h2>");
        html.append("<div class='card factcheck-text'>").append(escapeHtml(factCheckResult)).append("</div>");

        if (webResults != null && !webResults.isEmpty()) {
            html.append("<h2>Industry Reports & News</h2><div class='card'><ul>");
            for (WebResult r : webResults) {
                html.append("<li><strong>").append(escapeHtml(r.title())).append("</strong><br>")
                        .append("<a href='").append(r.url()).append("' target='_blank'>").append(r.url()).append("</a><br>")
                        .append(escapeHtml(r.description())).append("</li><br>");
            }
            html.append("</ul></div>");
        }

        html.append("<h2>References</h2><div class='card'><ol>");
        for (ArxivPaper p : papers) {
            html.append("<li>").append(escapeHtml(p.title())).append(" — ")
                    .append("<a href='").append(p.url()).append("' target='_blank'>").append(p.url()).append("</a></li>");
        }
        html.append("</ol></div>");

        html.append("</body></html>");

        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public void saveToFile(String content, String filename) throws Exception {
        Path outputDir = Path.of("reports");
        Files.createDirectories(outputDir);
        Path filePath = outputDir.resolve(filename);
        Files.writeString(filePath, content);
        System.out.println("Report saved to: " + filePath.toAbsolutePath());
    }
}