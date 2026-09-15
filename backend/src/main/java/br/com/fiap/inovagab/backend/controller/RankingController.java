package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.ranking.MyRankingResponse;
import br.com.fiap.inovagab.backend.dto.ranking.RankingEntryResponse;
import br.com.fiap.inovagab.backend.security.CurrentUser;
import br.com.fiap.inovagab.backend.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ranking", description = "Pontuacao dos operadores inovadores")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    @Operation(summary = "Operadores ordenados por pontos (desc)")
    public ResponseEntity<List<RankingEntryResponse>> ranking() {
        return ResponseEntity.ok(rankingService.getRanking());
    }

    @GetMapping("/me")
    @Operation(summary = "Posicao e pontos do usuario autenticado")
    public ResponseEntity<MyRankingResponse> myPosition() {
        return ResponseEntity.ok(rankingService.getMyPosition(CurrentUser.require().getId()));
    }
}
