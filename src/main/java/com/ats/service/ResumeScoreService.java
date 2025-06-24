package com.ats.service;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class ResumeScoreService {

    private final Tika tika = new Tika();

    public String extractTextFromResume(InputStream inputStream) throws Exception {
        return tika.parseToString(inputStream);
    }

    // Base common keywords
    private static final Set<String> baseKeywords = Set.of(
        "html", "css", "bootstrap", "react", "github", "sql"
    );

    // Programming languages
    private static final Set<String> programmingLanguages = Set.of(
        "java", "c", "c++", "c#", "python", "javascript", "ruby"
    );

    // Additional keywords to boost the ATS score
    private static final Set<String> extraKeywords = Set.of(
        "agile", "scrum", "sdlc", "waterfall", "kanban", "jira", "confluence",
        "ui", "ux", "ui/ux", "figma", "wireframe", "tools", "tool", "page", "pages", "oops",
        "jsx", "rest api", "api", "swagger", "microservices", "postman",
        "unit testing", "integration testing", "git", "ci/cd", "mvc", "jwt"
    );

    private static final Set<String> extraSkillSet = Set.of(
        "sap", "power bi", "data", "data analytics", "web scraping", "ms office", "data mining",
        "artificial intelligence", "meachine learning", "ai", "iot"
    );

    // Role-specific keyword mapping
    private static final Map<String, Set<String>> roleKeywordsMap = Map.of(
        "java full stack developer", Set.of("mysql", "jdbc", "jsp", "servlets", "hibernate", "spring", "spring boot", "spring mvc"),
        "python full stack developer", Set.of("django", "flask", "mysql", "pandas"),
        "mern developer", Set.of("mongodb", "express js", "react", "next.js"),
        "database associate", Set.of("mysql", "mongo db", "oracle", "sql server"),
        "test engineer", Set.of("selenium", "cypress", "junit", "testng", "postman", "rest assured"),
        "software engineer", Set.of("problem solving", "data structures", "algorithms"),
        "cloud engineer", Set.of("aws", "azure", "gcp", "cloud"),
        "devops engineer", Set.of("docker", "kubernetes", "jenkins", "ansible", "cloud", "devops")
    );

    public int calculateScore(String content, String role) {
        content = content.toLowerCase();

        Set<String> roleKeywords = new HashSet<>();
        Set<String> expectedLangs = new HashSet<>();
        
        if (role.equalsIgnoreCase("entry level developer")) {
            Set<String> basicFrontend = Set.of("html", "css", "javascript", "bootstrap");
            Set<String> allowedLangs = Set.of("java", "python", "c", "c++", "c#", "js", "ruby");
            Set<String> allowedDBs = Set.of("mysql", "postgresql", "mongodb", "oracle", "sql server");

            int score = 0;
            long frontendMatched = basicFrontend.stream().filter(content::contains).count();
            score += frontendMatched * 5;
            boolean hasLang = allowedLangs.stream().anyMatch(content::contains);
            if (hasLang) score += 25;
            boolean hasDb = allowedDBs.stream().anyMatch(content::contains);
            if (hasDb) score += 25;
            if (content.contains("projects")) score += 10;
           // if (content.contains("intern")) score += 10;
            if (content.contains("certificates")) score += 10;
            if (content.contains("technical skills")) score += 5;
            if (content.contains("summary") || content.contains("profile")) score += 5;
            if (content.contains("education")) score += 5;

            return Math.min(score, 100);
        }


        switch (role.toLowerCase()) {
            case "java full stack developer" -> {
                roleKeywords.addAll(roleKeywordsMap.get("java full stack developer"));
                expectedLangs.add("java");
            }
            case "python full stack developer" -> {
                roleKeywords.addAll(roleKeywordsMap.get("python full stack developer"));
                expectedLangs.add("python");
            }
            case "mern developer" -> {
                roleKeywords.addAll(roleKeywordsMap.get("mern developer"));
                expectedLangs.add("javascript"); // JavaScript
            }
            case "test engineer" -> {
                roleKeywords.addAll(roleKeywordsMap.get("test engineer"));
                expectedLangs.add("java");
            }
            case "cloud engineer", "devops engineer" -> {
                roleKeywords.addAll(roleKeywordsMap.get(role.toLowerCase()));
                expectedLangs.addAll(Set.of("python", "bash", "java"));
            }
            case "database associate" -> {
                roleKeywords.addAll(roleKeywordsMap.get("database associate"));
                expectedLangs.add("sql");
            }
            default -> roleKeywords.addAll(Set.of());
        }

        // 1. Must contain at least one expected language
        boolean hasMainLanguage = expectedLangs.stream().anyMatch(content::contains);
        if (!hasMainLanguage) return 0;

        // 2. Count matched keywords
        int matchedKeywords = 0;
        int progLangMatched = 0;

        for (String keyword : baseKeywords) {
            if (content.contains(keyword)) matchedKeywords++;
        }

        for (String keyword : roleKeywords) {
            if (content.contains(keyword)) matchedKeywords++;
        }

        for (String lang : programmingLanguages) {
            if (content.contains(lang)) {
                matchedKeywords++;
                progLangMatched++;
            }
        }

        for (String keyword : extraKeywords) {
            if (content.contains(keyword)) matchedKeywords++;
        }

        // 3. Total keywords
        int totalKeywords = baseKeywords.size() + roleKeywords.size() + programmingLanguages.size() + extraKeywords.size();

        // 4. Score calculation
        double score = ((double) matchedKeywords / totalKeywords) * 100;

        // 5. Bonus: Additional expected language match
        long roleLangsMatched = expectedLangs.stream().filter(content::contains).count();
        if (progLangMatched > roleLangsMatched) {
            score += 1.75;
        }

        // 6. Bonus: Cloud/DevOps
        if (content.contains("cloud") || content.contains("devops")) {
            score += 2.5;
        }

        // 7. Bonus: Section titles
        if (content.contains("projects")) score += 2.5;
        if (content.contains("languages")) score += 2.5;
        if (content.contains("experience")) score += 2.5;
        if (content.contains("education")) score += 2.5;
        if (content.contains("front-end technologies")) score += 1.75;        
        if (content.contains("frameworks")) score += 1.75;       
        if (content.contains("deployment tools")) score += 1.75;       
        if (content.contains("databases")) score += 1.75;

        for(String extraSkills : extraSkillSet){
            if(content.contains(extraSkills)) score+=2;
        }

        // 8. Clamp score to 100
        return (int) Math.min(score+10, 100);
    }
}
