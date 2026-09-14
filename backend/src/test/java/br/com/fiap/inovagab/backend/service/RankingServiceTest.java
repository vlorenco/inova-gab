package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.ranking.MyRankingResponse;
import br.com.fiap.inovagab.backend.dto.ranking.RankingEntryResponse;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.User;
import br.com.fiap.inovagab.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RankingServiceTest {

    @Mock
    private UserRepository userRepository;

    private RankingService rankingService;

    @BeforeEach
    void setUp() {
        rankingService = new RankingService(userRepository);
    }

    private User operator(String id, String name, int points) {
        User user = new User(name, name.toLowerCase().replace(" ", ".") + "@app.com", "hash", Role.OPERADOR);
        user.setId(id);
        user.setPoints(points);
        return user;
    }

    @Test
    @DisplayName("creditar pontos soma ao total do operador")
    void awardsPoints() {
        User user = operator("op-1", "Operador Demo", 10);
        when(userRepository.findById("op-1")).thenReturn(Optional.of(user));

        rankingService.awardPoints("op-1", RankingService.POINTS_IDEA_APPROVED);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPoints()).isEqualTo(60);
    }

    @Test
    @DisplayName("usuario inexistente nao quebra o fluxo de pontuacao")
    void ignoresUnknownUser() {
        when(userRepository.findById("fantasma")).thenReturn(Optional.empty());

        rankingService.awardPoints("fantasma", 50);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("id vazio ou zero pontos nao gera escrita no banco")
    void ignoresNoOpAwards() {
        rankingService.awardPoints(null, 50);
        rankingService.awardPoints("", 50);
        rankingService.awardPoints("op-1", 0);

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("ranking numera as posicoes a partir da ordem por pontos")
    void buildsRanking() {
        when(userRepository.findByRoleOrderByPointsDesc(Role.OPERADOR)).thenReturn(List.of(
                operator("op-1", "Operador Demo", 160),
                operator("op-2", "Ana Souza", 60),
                operator("op-3", "Carlos Lima", 0)
        ));

        List<RankingEntryResponse> ranking = rankingService.getRanking();

        assertThat(ranking).hasSize(3);
        assertThat(ranking.get(0).position()).isEqualTo(1);
        assertThat(ranking.get(0).points()).isEqualTo(160);
        assertThat(ranking.get(2).position()).isEqualTo(3);
    }

    @Test
    @DisplayName("minha posicao devolve o lugar do operador no ranking")
    void findsMyPosition() {
        when(userRepository.findByRoleOrderByPointsDesc(Role.OPERADOR)).thenReturn(List.of(
                operator("op-1", "Operador Demo", 160),
                operator("op-2", "Ana Souza", 60)
        ));

        MyRankingResponse me = rankingService.getMyPosition("op-2");

        assertThat(me.position()).isEqualTo(2);
        assertThat(me.totalOperators()).isEqualTo(2);
        assertThat(me.points()).isEqualTo(60);
    }

    @Test
    @DisplayName("gestor nao entra no ranking de operadores: posicao 0")
    void managerHasNoPosition() {
        User manager = new User("Gestor Demo", "gestor@app.com", "hash", Role.GESTOR);
        manager.setId("ge-1");
        when(userRepository.findByRoleOrderByPointsDesc(Role.OPERADOR))
                .thenReturn(List.of(operator("op-1", "Operador Demo", 160)));
        when(userRepository.findById("ge-1")).thenReturn(Optional.of(manager));

        MyRankingResponse me = rankingService.getMyPosition("ge-1");

        assertThat(me.position()).isZero();
        assertThat(me.name()).isEqualTo("Gestor Demo");
    }
}
