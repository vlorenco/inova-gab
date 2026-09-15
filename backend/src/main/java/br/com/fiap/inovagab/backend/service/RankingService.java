package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.ranking.MyRankingResponse;
import br.com.fiap.inovagab.backend.dto.ranking.RankingEntryResponse;
import br.com.fiap.inovagab.backend.exception.NotFoundException;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.User;
import br.com.fiap.inovagab.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Regras de pontuacao dos operadores, centralizadas no backend.
 *
 * criar ideia .................. +10
 * ideia aprovada ............... +50
 * ideia virou projeto .......... +100
 *
 * A idempotencia (nao pontuar duas vezes pela mesma operacao) e garantida
 * pelos marcadores gravados na propria ideia - ver IdeaService.
 */
@Service
public class RankingService {

    public static final int POINTS_IDEA_CREATED = 10;
    public static final int POINTS_IDEA_APPROVED = 50;
    public static final int POINTS_IDEA_CONVERTED = 100;

    private final UserRepository userRepository;

    public RankingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Credita pontos ao operador. Silencioso se o usuario nao existir mais. */
    public void awardPoints(String userId, int points) {
        if (userId == null || userId.isBlank() || points == 0) {
            return;
        }
        userRepository.findById(userId).ifPresent(user -> {
            user.setPoints(Math.max(0, user.getPoints() + points));
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        });
    }

    public List<RankingEntryResponse> getRanking() {
        List<User> operators = userRepository.findByRoleOrderByPointsDesc(Role.OPERADOR);
        List<RankingEntryResponse> ranking = new ArrayList<>(operators.size());
        for (int i = 0; i < operators.size(); i++) {
            User user = operators.get(i);
            ranking.add(new RankingEntryResponse(
                    i + 1,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPoints()
            ));
        }
        return ranking;
    }

    public MyRankingResponse getMyPosition(String userId) {
        List<RankingEntryResponse> ranking = getRanking();
        return ranking.stream()
                .filter(entry -> entry.userId().equals(userId))
                .findFirst()
                .map(entry -> new MyRankingResponse(
                        entry.position(), ranking.size(), entry.userId(), entry.name(), entry.points()))
                .orElseGet(() -> {
                    // Gestor/lideranca nao entram no ranking de operadores.
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
                    return new MyRankingResponse(0, ranking.size(), user.getId(), user.getName(), user.getPoints());
                });
    }
}
