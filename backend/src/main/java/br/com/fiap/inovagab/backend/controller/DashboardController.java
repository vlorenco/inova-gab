package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.dashboard.DashboardSummaryResponse;
import br.com.fiap.inovagab.backend.dto.dashboard.ProjectDashboardResponse;
import br.com.fiap.inovagab.backend.dto.dashboard.StrategyDashboardResponse;
import br.com.fiap.inovagab.backend.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Dashboard", description = "Indicadores consolidados - exclusivo da LIDERANCA")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Indicadores gerais do portfolio, incluindo ROI")
    public ResponseEntity<DashboardSummaryResponse> summary() {
        return ResponseEntity.ok(dashboardService.summary());
    }

    @GetMapping("/strategies/{strategyId}")
    @Operation(summary = "Indicadores de uma orientacao estrategica")
    public ResponseEntity<StrategyDashboardResponse> byStrategy(@PathVariable String strategyId) {
        return ResponseEntity.ok(dashboardService.byStrategy(strategyId));
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Indicadores de um projeto")
    public ResponseEntity<ProjectDashboardResponse> byProject(@PathVariable String projectId) {
        return ResponseEntity.ok(dashboardService.byProject(projectId));
    }
}
