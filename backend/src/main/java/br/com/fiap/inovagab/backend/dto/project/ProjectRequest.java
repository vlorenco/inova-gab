package br.com.fiap.inovagab.backend.dto.project;

import br.com.fiap.inovagab.backend.model.ProjectStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProjectRequest(

        @NotBlank(message = "Nome do projeto e obrigatorio.")
        @Size(max = 150, message = "Nome deve ter no maximo 150 caracteres.")
        String name,

        @Size(max = 2000, message = "Descricao deve ter no maximo 2000 caracteres.")
        String description,

        @Size(max = 120, message = "Responsavel deve ter no maximo 120 caracteres.")
        String responsible,

        ProjectStatus status,

        @Size(max = 150, message = "Etapa atual deve ter no maximo 150 caracteres.")
        String currentStage,

        @PositiveOrZero(message = "Investimento nao pode ser negativo.")
        Double investment,

        @PositiveOrZero(message = "Retorno financeiro nao pode ser negativo.")
        Double financialReturn,

        @PositiveOrZero(message = "Reducao de custos nao pode ser negativa.")
        Double costReduction,

        @PositiveOrZero(message = "Ganho de produtividade nao pode ser negativo.")
        @DecimalMax(value = "1000.0", message = "Ganho de produtividade deve ser no maximo 1000%.")
        Double productivityGain,

        @Size(max = 30, message = "Prazo deve ter no maximo 30 caracteres.")
        String deadline,

        String ideaId,

        String strategyId
) {
}
