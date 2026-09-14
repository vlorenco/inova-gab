package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.idea.AiAnalysisResponse;
import br.com.fiap.inovagab.backend.dto.idea.IdeaPriorityRequest;
import br.com.fiap.inovagab.backend.dto.idea.IdeaRequest;
import br.com.fiap.inovagab.backend.dto.idea.IdeaResponse;
import br.com.fiap.inovagab.backend.dto.idea.IdeaStatusRequest;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.security.CurrentUser;
import br.com.fiap.inovagab.backend.service.IdeaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ideias", description = "Cadastro pelo operador e curadoria pelo gestor")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping
    @Operation(summary = "Cadastra uma ideia (somente OPERADOR). O dono vem do JWT.")
    public ResponseEntity<IdeaResponse> create(@Valid @RequestBody IdeaRequest request) {
        IdeaResponse created = ideaService.create(request, CurrentUser.require());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my")
    @Operation(summary = "Lista apenas as ideias do operador autenticado")
    public ResponseEntity<List<IdeaResponse>> myIdeas() {
        return ResponseEntity.ok(ideaService.listMyIdeas(CurrentUser.require()));
    }

    @GetMapping
    @Operation(summary = "Lista todas as ideias, com filtro opcional por status (somente GESTOR)")
    public ResponseEntity<List<IdeaResponse>> listAll(
            @RequestParam(value = "status", required = false) IdeaStatus status) {
        return ResponseEntity.ok(ideaService.listAll(status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe da ideia. O operador so enxerga as proprias.")
    public ResponseEntity<IdeaResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ideaService.getById(id, CurrentUser.require()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita a propria ideia (somente OPERADOR dono)")
    public ResponseEntity<IdeaResponse> update(@PathVariable String id,
                                               @Valid @RequestBody IdeaRequest request) {
        return ResponseEntity.ok(ideaService.update(id, request, CurrentUser.require()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui a propria ideia (somente OPERADOR dono)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ideaService.delete(id, CurrentUser.require());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Define a prioridade da ideia (somente GESTOR)")
    public ResponseEntity<IdeaResponse> updatePriority(@PathVariable String id,
                                                       @Valid @RequestBody IdeaPriorityRequest request) {
        return ResponseEntity.ok(ideaService.updatePriority(id, request.priority()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Aprova, rejeita ou reclassifica a ideia (somente GESTOR)")
    public ResponseEntity<IdeaResponse> updateStatus(@PathVariable String id,
                                                     @Valid @RequestBody IdeaStatusRequest request) {
        return ResponseEntity.ok(ideaService.updateStatus(id, request.status()));
    }

    @PostMapping("/{id}/ai-analysis")
    @Operation(summary = "Pontuacao automatica da ideia pelo Gemini (somente GESTOR)")
    public ResponseEntity<AiAnalysisResponse> aiAnalysis(@PathVariable String id) {
        return ResponseEntity.ok(ideaService.runAiAnalysis(id, CurrentUser.require()));
    }
}
