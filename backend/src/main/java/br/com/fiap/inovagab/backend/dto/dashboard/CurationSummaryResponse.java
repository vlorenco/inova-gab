package br.com.fiap.inovagab.backend.dto.dashboard;

import java.util.List;

/**
 * Indicadores da curadoria, na otica do gestor.
 *
 * Diferente de DashboardSummaryResponse, que olha o dinheiro do portfolio,
 * aqui o foco e o fluxo de trabalho: quantas ideias estao em cada etapa,
 * quantas viraram projeto e de onde elas vem.
 */
public record CurationSummaryResponse(
        long totalIdeas,
        long underAnalysis,
        long prioritized,
        long approved,
        long rejected,
        long convertedToProject,
        long withAiAnalysis,
        long totalProjects,
        long plannedProjects,
        long activeProjects,
        long completedProjects,
        long cancelledProjects,
        long contributingOperators,
        List<AreaCount> topAreas
) {

    /** Quantidade de ideias de uma area, para o recorte por origem. */
    public record AreaCount(String area, long total) {
    }
}
