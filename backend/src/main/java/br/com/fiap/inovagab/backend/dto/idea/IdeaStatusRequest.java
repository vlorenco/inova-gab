package br.com.fiap.inovagab.backend.dto.idea;

import br.com.fiap.inovagab.backend.model.IdeaStatus;
import jakarta.validation.constraints.NotNull;

public record IdeaStatusRequest(

        @NotNull(message = "Status e obrigatorio (EM_ANALISE, PRIORIZADA, APROVADA ou REJEITADA).")
        IdeaStatus status
) {
}
