package br.com.fiap.inovagab.backend.dto.strategy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StrategyRequest(

        @NotBlank(message = "Titulo e obrigatorio.")
        @Size(max = 150, message = "Titulo deve ter no maximo 150 caracteres.")
        String title,

        @NotBlank(message = "Descricao e obrigatoria.")
        @Size(max = 2000, message = "Descricao deve ter no maximo 2000 caracteres.")
        String description,

        @Size(max = 30, message = "Data deve ter no maximo 30 caracteres.")
        String date,

        @Size(max = 60, message = "Categoria deve ter no maximo 60 caracteres.")
        String category,

        @Size(max = 120, message = "Campanha deve ter no maximo 120 caracteres.")
        String campaign,

        Boolean active
) {
}
