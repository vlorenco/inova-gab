package br.com.fiap.inovagab.backend.model;

import java.time.Instant;

/**
 * Resultado da analise automatica da ideia feita pelo Gemini.
 * Fica embutido no documento da ideia (subdocumento aiAnalysis).
 */
public class AiAnalysis {

    private int score;
    private int impactScore;
    private int feasibilityScore;
    private int innovationScore;
    private int strategicAlignmentScore;
    private String recommendation;
    private String summary;
    private Instant analyzedAt;
    private String analyzedBy;
    private String model;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(int impactScore) {
        this.impactScore = impactScore;
    }

    public int getFeasibilityScore() {
        return feasibilityScore;
    }

    public void setFeasibilityScore(int feasibilityScore) {
        this.feasibilityScore = feasibilityScore;
    }

    public int getInnovationScore() {
        return innovationScore;
    }

    public void setInnovationScore(int innovationScore) {
        this.innovationScore = innovationScore;
    }

    public int getStrategicAlignmentScore() {
        return strategicAlignmentScore;
    }

    public void setStrategicAlignmentScore(int strategicAlignmentScore) {
        this.strategicAlignmentScore = strategicAlignmentScore;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Instant getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(Instant analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public String getAnalyzedBy() {
        return analyzedBy;
    }

    public void setAnalyzedBy(String analyzedBy) {
        this.analyzedBy = analyzedBy;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
