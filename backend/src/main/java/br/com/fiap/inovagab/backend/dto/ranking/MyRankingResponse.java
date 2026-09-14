package br.com.fiap.inovagab.backend.dto.ranking;

public record MyRankingResponse(
        int position,
        int totalOperators,
        String userId,
        String name,
        int points
) {
}
