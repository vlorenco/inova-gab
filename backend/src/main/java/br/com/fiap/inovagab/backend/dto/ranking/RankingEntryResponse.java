package br.com.fiap.inovagab.backend.dto.ranking;

public record RankingEntryResponse(
        int position,
        String userId,
        String name,
        String email,
        int points
) {
}
