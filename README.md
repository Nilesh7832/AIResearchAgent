# 🤖 AI Research Agent

An autonomous multi-agent research assistant that automatically searches academic papers, extracts key findings, cross-verifies sources, and generates a professional research report — powered entirely by a **local AI model** (via Ollama).
![Java](https://img.shields.io/badge/Java-25-orange)
![Maven](https://img.shields.io/badge/Maven-3.9-red)
![SQLite](https://img.shields.io/badge/SQLite-3.45-blue)
![Ollama](https://img.shields.io/badge/Ollama-llama3.1-green)
![License](https://img.shields.io/badge/License-CC%20BY--NC--ND%204.0-lightgrey)

## Overview

Give it a research question like *"Research the impact of AI on cybersecurity"*, and it autonomously:

- 🧠 **Plans** the research by breaking the query into concrete sub-tasks
- 🔎 **Searches** academic papers on arXiv
- 🌐 **Searches** the web for industry reports and news (via Brave Search API)
- 📄 **Downloads and extracts** full PDF text using Apache PDFBox
- 📊 **Analyzes** each source to extract research questions, methodology, findings, and limitations
- ⚖️ **Fact-checks** across sources to identify agreements and conflicting claims
- 📝 **Generates** a final report in both Markdown and styled HTML formats
- 💾 **Persists** all data in a local SQLite database

## Architecture
Query → Planner Agent → Search (arXiv + Web) → PDF Extraction
→ Analysis Agent → Fact-Check Agent → Report Agent → HTML/MD Report


Each agent is a focused, single-responsibility Java class that communicates with a local LLM via [Ollama](https://ollama.com).

## Tech Stack

| Component        | Technology                          |
|-------------------|--------------------------------------|
| Language          | Java 25                              |
| AI Model          | Ollama + local open-source model (llama3.1) |
| Academic Search   | arXiv API                            |
| Web Search        | Brave Search API                     |
| PDF Extraction    | Apache PDFBox                        |
| Database          | SQLite                               |
| HTTP Client       | OkHttp                               |
| JSON Parsing      | Jackson                              |
| Build Tool        | Maven                                |

## Project Structure

AIResearchAgent/
├── src/main/java/org/example/
│ ├── agents/
│ │ ├── PlannerAgent.java
│ │ ├── AnalysisAgent.java
│ │ ├── FactCheckAgent.java
│ │ └── ReportAgent.java
│ ├── services/
│ │ ├── OllamaService.java
│ │ ├── ArxivService.java
│ │ ├── WebSearchService.java
│ │ ├── PdfService.java
│ │ └── DbService.java
│ └── Main.java
├── reports/ # Generated sample reports
├── pom.xml
└── README.md


## Setup & Installation

### Prerequisites

- **Java 17+** (developed with JDK 25)
- **Maven** (bundled with IntelliJ IDEA)
- **[Ollama](https://ollama.com)** installed locally
- A **[Brave Search API](https://brave.com/search/api/)** key (free tier available)

### 1. Clone the repository

```bash
git clone https://github.com/Nilesh7832/AIResearchAgent.git
cd AIResearchAgent
```

### 2. Install and set up Ollama

```bash
ollama pull llama3.1
ollama run llama3.1 "test"
```

Confirm the Ollama API is running:
```bash
curl http://localhost:11434/api/tags
```

### 3. Set your Brave Search API key

This project reads the API key from an environment variable — **never hardcode it**.

**Windows (PowerShell):**
```powershell
$env:BRAVE_API_KEY="**************"
```

**macOS/Linux:**
```bash
export BRAVE_API_KEY="*************"
```

**In IntelliJ:**
Run → Edit Configurations → Environment Variables → add `BRAVE_API_KEY=***********

### 4. Build and run

```bash
mvn clean install
mvn exec:java -Dexec.mainClass="org.example.Main"
```

Or simply run `Main.java` from IntelliJ.

## Output

After running, check the `reports/` folder for:
- `cybersecurity_ai_report.md` — Markdown version
- `cybersecurity_ai_report.html` — Styled HTML version (open in any browser)

All paper metadata and analyses are also saved to `research.db` (SQLite) for future querying.

## Limitations

- Uses a local, smaller LLM (llama3.1) — analysis depth is not comparable to frontier models like GPT-4 or Claude.
- Currently sources academic papers only from arXiv (primarily CS/STEM fields).
- Generated reports should be reviewed by a human before being used for critical decisions — AI-generated summaries can contain inaccuracies.
- This is a research **assistant**, not a replacement for rigorous human research.

## Future Improvements

- [ ] Add Semantic Scholar / PubMed support for broader domain coverage
- [ ] Configurable research topics via CLI arguments
- [ ] Scheduled/automated recurring reports
- [ ] Support for stronger cloud-based LLMs as an option


## License

Copyright (c) 2026 Nilesh Kumar Mohanty. All rights reserved.

**License:** CC BY-NC-ND 4.0

Licensed under Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International. You may view and share this repository for non-commercial purposes only, with proper attribution, and without creating modified versions. See the [LICENSE](LICENSE) file for full details.

For commercial use or permission to create derivative works, please contact the author.

## Author

Built by Nilesh Kumar Mohanty as a personal project exploring multi-agent AI systems with local LLMs.

If you use or build upon this project, please credit the original author and link to this repository.

---

⭐ If you find this project useful, consider giving it a star!


