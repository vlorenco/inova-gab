package br.com.fiap.inovagab.backend.dto.dashboard;

/**
 * Desempenho do proprio operador, para a home dele.
 *
 * Junta em uma chamada o que antes exigia duas (ranking/me e ideas/my) e evita
 * que a tela conte status de ideia na mao.
 *
 * Nao expoe nada de outros operadores alem de pontosDoLider, que ja e publico
 * pelo ranking.
 */
public record OperatorPerformanceResponse(
        long totalIdeas,
        long underAnalysis,
        long prioritized,
        long approved,
        long rejected,
        long convertedToProject,
        int points,
        int position,
        long totalOperators,
        int leaderPoints
) {
}
