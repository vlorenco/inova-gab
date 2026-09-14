package br.com.fiap.inovagab.backend.dto.idea;

import br.com.fiap.inovagab.backend.model.IdeaPriority;
import jakarta.validation.constraints.NotNull;

public record IdeaPriorityRequest(

        @NotNull(message = "Prioridade e obrigatoria (BAIXA, NORMAL ou ALTA).")
        IdeaPriority priority
) {
}
