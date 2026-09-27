package org.example;

import org.example.agents.*;
import org.example.services.*;
import org.example.services.ArxivService.ArxivPaper;
import org.example.services.WebSearchService.WebResult;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        String query = "Research the impact of AI on cybersecurity.";

        OllamaService ollama = new OllamaService();
        ArxivService arxiv = new ArxivService();
        PdfService pdfService = new PdfService();
        WebSearchService webSearch = new WebSearchService();
        DbService db = new DbService();
        PlannerAgent planner = new PlannerAgent(ollama);
        AnalysisAgent analysisAgent = new AnalysisAgent(ollama);
        FactCheckAgent factCheckAgent = new FactCheckAgent(ollama);
        ReportAgent reportAgent = new ReportAgent();

        db.init();

        System.out.println("STEP 1: Planning...");
        List<String> tasks = planner.plan(query);
        System.out.println("Generated " + tasks.size() + " tasks.\n");

        System.out.println("STEP 2: Searching arXiv (academic papers)...");
        List<ArxivPaper> papers = arxiv.search("AI cybersecurity threat detection", 2);
        System.out.println("Found " + papers.size() + " academic papers.\n");

        System.out.println("STEP 2b: Searching web (industry reports)...");
        List<WebResult> webResults = webSearch.search("AI cybersecurity industry report 2026", 3);
        System.out.println("Found " + webResults.size() + " web results.\n");

        System.out.println("STEP 3: Downloading PDFs and analyzing full text...");
        List<String> analyses = new ArrayList<>();
        for (ArxivPaper paper : papers) {
            System.out.println("  Processing: " + paper.title());

            String fullText;
            try {
                fullText = pdfService.downloadAndExtractText(paper.url());
                System.out.println("    Extracted " + fullText.length() + " characters from PDF.");
            } catch (Exception e) {
                System.out.println("    PDF download failed, falling back to abstract.");
                fullText = paper.summary();
            }

            String analysis = analysisAgent.analyze(paper.title(), fullText);
            analyses.add(analysis);

            db.savePaper(query, paper.url(), paper.title(),
                    paper.authors().toString(), paper.published(),
                    paper.summary(), analysis);
        }
        System.out.println();

        System.out.println("STEP 4: Fact-checking across academic sources...");
        String factCheckResult = factCheckAgent.compare(papers);
        System.out.println();

        System.out.println("STEP 5: Building report...");
        String report = reportAgent.buildReport(query, tasks, papers, analyses, factCheckResult);
        reportAgent.saveToFile(report, "cybersecurity_ai_report.md");

        String htmlReport = reportAgent.buildHtmlReport(query, tasks, papers, analyses, factCheckResult, webResults);
        reportAgent.saveToFile(htmlReport, "cybersecurity_ai_report.html");

        db.saveReport(query, factCheckResult, report);

        db.printAllPapers();

        System.out.println("\nDONE. Pipeline completed successfully. Data saved to research.db");
        System.out.println("HTML report ready — open reports/cybersecurity_ai_report.html in your browser.");
    }
}