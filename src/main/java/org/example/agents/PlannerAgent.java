package org.example.agents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.services.OllamaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlannerAgent {

    private final OllamaService ollama;
    private final ObjectMapper mapper = new ObjectMapper();

    public PlannerAgent(OllamaService ollama) {
        this.ollama = ollama;
    }

    public List<String> plan(String query) throws IOException {
        String prompt = """
                You are a research planner. Break the following research query
                into exactly 5 concrete, specific research tasks.
                
                Respond with ONLY valid JSON in this exact format, nothing else:
                {"tasks": ["task 1", "task 2", "task 3", "task 4", "task 5"]}
                
                Query: %s
                """.formatted(query);

        String response = ollama.generateJson("llama3.1", prompt);
        return parseTasks(response);
    }

    private List<String> parseTasks(String jsonResponse) {
        List<String> tasks = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode tasksNode = root.get("tasks");
            if (tasksNode != null && tasksNode.isArray()) {
                for (JsonNode taskNode : tasksNode) {
                    tasks.add(taskNode.asText());
                }
            }
        } catch (Exception e) {
            System.out.println("Warning: Failed to parse JSON tasks, response was: " + jsonResponse);
        }
        return tasks;
    }
}