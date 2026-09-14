package br.com.fiap.inovagab.backend.dto.idea;

import br.com.fiap.inovagab.backend.model.AiAnalysis;

import java.time.Instant;

public record AiAnalysisResponse(
        int score,
        int impactScore,
        int feasibilityScore,
        int innovationScore,
        int strategicAlignmentScore,
        String recommendation,
        String summary,
        Instant analyzedAt,
        String model
) {
    public static AiAnalysisResponse from(AiAnalysis analysis) {
        if (analysis == null) {
            return null;
        }
        return new AiAnalysisResponse(
                analysis.getScore(),
                analysis.getImpactScore(),
                analysis.getFeasibilityScore(),
                analysis.getInnovationScore(),
                analysis.getStrategicAlignmentScore(),
                analysis.getRecommendation(),
                analysis.getSummary(),
                analysis.getAnalyzedAt(),
                analysis.getModel()
        );
    }
}
