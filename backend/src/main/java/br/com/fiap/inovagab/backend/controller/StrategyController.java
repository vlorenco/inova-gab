package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.strategy.StrategyHistoryResponse;
import br.com.fiap.inovagab.backend.dto.strategy.StrategyRequest;
import br.com.fiap.inovagab.backend.dto.strategy.StrategyResponse;
import br.com.fiap.inovagab.backend.security.CurrentUser;
import br.com.fiap.inovagab.backend.service.StrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/strategies")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orientacoes estrategicas", description = "CRUD da lideranca e consulta dos demais perfis")
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @GetMapping
    @Operation(summary = "Lista orientacoes (OPERADOR, GESTOR, LIDERANCA)")
    public ResponseEntity<List<StrategyResponse>> list(
            @RequestParam(value = "activeOnly", required = false) Boolean activeOnly) {
        return ResponseEntity.ok(strategyService.list(activeOnly));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe de uma orientacao (OPERADOR, GESTOR, LIDERANCA)")
    public ResponseEntity<StrategyResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(strategyService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma orientacao (somente LIDERANCA)")
    public ResponseEntity<StrategyResponse> create(@Valid @RequestBody StrategyRequest request) {
        StrategyResponse created = strategyService.create(request, CurrentUser.require());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma orientacao (somente LIDERANCA)")
    public ResponseEntity<StrategyResponse> update(@PathVariable String id,
                                                   @Valid @RequestBody StrategyRequest request) {
        return ResponseEntity.ok(strategyService.update(id, request, CurrentUser.require()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui uma orientacao (somente LIDERANCA)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        strategyService.delete(id, CurrentUser.require());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Historico de alteracoes da orientacao (somente LIDERANCA)")
    public ResponseEntity<List<StrategyHistoryResponse>> history(@PathVariable String id) {
        return ResponseEntity.ok(strategyService.history(id));
    }
}
