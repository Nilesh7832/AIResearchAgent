package org.example.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbService {

    private static final String URL = "jdbc:sqlite:research.db";

    public void init() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS papers (
                    id TEXT PRIMARY KEY,
                    query TEXT,
                    title TEXT,
                    authors TEXT,
                    published TEXT,
                    summary TEXT,
                    analysis TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reports (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    query TEXT,
                    fact_check TEXT,
                    report_content TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
            """);
        }
        System.out.println("Database initialized.");
    }

    public void savePaper(String query, String url, String title, String authors,
                          String published, String summary, String analysis) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO papers (id, query, title, authors, published, summary, analysis)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setString(2, query);
            ps.setString(3, title);
            ps.setString(4, authors);
            ps.setString(5, published);
            ps.setString(6, summary);
            ps.setString(7, analysis);
            ps.executeUpdate();
        }
    }

    public void saveReport(String query, String factCheck, String reportContent) throws SQLException {
        String sql = """
            INSERT INTO reports (query, fact_check, report_content)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, query);
            ps.setString(2, factCheck);
            ps.setString(3, reportContent);
            ps.executeUpdate();
        }
    }

    public void printAllPapers() throws SQLException {
        String sql = "SELECT title, query, created_at FROM papers";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Papers in DB ---");
            while (rs.next()) {
                System.out.println("- " + rs.getString("title")
                        + " | query: " + rs.getString("query")
                        + " | saved: " + rs.getString("created_at"));
            }
        }
    }
}