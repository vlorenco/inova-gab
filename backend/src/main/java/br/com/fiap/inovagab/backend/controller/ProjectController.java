package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.project.ProjectRequest;
import br.com.fiap.inovagab.backend.dto.project.ProjectResponse;
import br.com.fiap.inovagab.backend.service.ProjectService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Projetos", description = "CRUD do gestor e consulta da lideranca")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Lista projetos (GESTOR e LIDERANCA)")
    public ResponseEntity<List<ProjectResponse>> list() {
        return ResponseEntity.ok(projectService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe do projeto (GESTOR e LIDERANCA)")
    public ResponseEntity<ProjectResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Cria projeto, opcionalmente a partir de uma ideia aprovada (somente GESTOR)")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza dados e resultados do projeto (somente GESTOR)")
    public ResponseEntity<ProjectResponse> update(@PathVariable String id,
                                                  @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um projeto (somente GESTOR)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
