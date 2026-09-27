package org.example.agents;

import org.example.services.ArxivService.ArxivPaper;
import org.example.services.OllamaService;

import java.io.IOException;
import java.util.List;

public class FactCheckAgent {

    private final OllamaService ollama;

    public FactCheckAgent(OllamaService ollama) {
        this.ollama = ollama;
    }

    public String compare(List<ArxivPaper> papers) throws IOException {
        StringBuilder sourcesText = new StringBuilder();
        int index = 1;
        for (ArxivPaper paper : papers) {
            sourcesText.append("SOURCE ").append(index).append(" (")
                    .append(paper.title()).append("):\n")
                    .append(paper.summary()).append("\n\n");
            index++;
        }

        String prompt = """
                Compare the following research sources. Identify:
                
                AGREEMENT: points where sources agree
                CONFLICT: points where sources disagree or present contradictory claims
                CONFIDENCE: your confidence level (High/Medium/Low) in the overall consistency of these sources
                
                Be concise. If there is no clear conflict, say so explicitly.
                
                %s
                """.formatted(sourcesText.toString());

        return ollama.generate("llama3.1", prompt);
    }
}