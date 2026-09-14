package br.com.fiap.inovagab.backend.dto.idea;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload de criacao/edicao de ideia. Nao existe operatorId aqui de proposito:
 * o dono da ideia sempre vem do JWT.
 */
public record IdeaRequest(

        @NotBlank(message = "Titulo e obrigatorio.")
        @Size(max = 150, message = "Titulo deve ter no maximo 150 caracteres.")
        String title,

        @NotBlank(message = "Problema e obrigatorio.")
        @Size(max = 2000, message = "Problema deve ter no maximo 2000 caracteres.")
        String problem,

        @NotBlank(message = "Solucao e obrigatoria.")
        @Size(max = 2000, message = "Solucao deve ter no maximo 2000 caracteres.")
        String solution,

        @Size(max = 100, message = "Area deve ter no maximo 100 caracteres.")
        String area,

        @Size(max = 1000, message = "Beneficio deve ter no maximo 1000 caracteres.")
        String benefit,

        String strategyId
) {
}
