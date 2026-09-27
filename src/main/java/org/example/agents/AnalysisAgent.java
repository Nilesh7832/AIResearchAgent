package org.example.agents;

import org.example.services.OllamaService;

import java.io.IOException;

public class AnalysisAgent {

    private final OllamaService ollama;

    public AnalysisAgent(OllamaService ollama) {
        this.ollama = ollama;
    }

    public String analyze(String title, String fullText) throws IOException {
        // Bahut lamba text hone ki wajah se, first ~6000 characters use karte hain
        // (intro + methodology usually yahi hota hai, aur Ollama ke context limit ke liye safe hai)
        String textForAnalysis = fullText.length() > 4000
                ? fullText.substring(0, 4000)
                : fullText;

        String prompt = """
                Analyze this research paper and extract the following
                in a clear structured format:
                
                RESEARCH QUESTION: (what problem does it address)
                METHODOLOGY: (how did they approach it)
                KEY FINDING: (main result, in 1-2 sentences)
                LIMITATION: (any stated weakness or gap, or "Not stated" if none)
                
                Paper Title: %s
                Paper Text: %s
                """.formatted(title, textForAnalysis);

        return ollama.generate("llama3.1", prompt);
    }
}