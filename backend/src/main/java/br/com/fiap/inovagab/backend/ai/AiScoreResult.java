package br.com.fiap.inovagab.backend.ai;

/** Formato exato que pedimos ao Gemini. Qualquer desvio e rejeitado na validacao. */
public record AiScoreResult(
        int score,
        int impactScore,
        int feasibilityScore,
        int innovationScore,
        int strategicAlignmentScore,
        String recommendation,
        String summary
) {
}
