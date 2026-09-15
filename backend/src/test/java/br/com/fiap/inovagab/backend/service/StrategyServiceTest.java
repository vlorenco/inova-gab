package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.strategy.StrategyRequest;
import br.com.fiap.inovagab.backend.dto.strategy.StrategyResponse;
import br.com.fiap.inovagab.backend.exception.BadRequestException;
import br.com.fiap.inovagab.backend.exception.NotFoundException;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.Strategy;
import br.com.fiap.inovagab.backend.model.StrategyAction;
import br.com.fiap.inovagab.backend.model.StrategyHistory;
import br.com.fiap.inovagab.backend.repository.StrategyHistoryRepository;
import br.com.fiap.inovagab.backend.repository.StrategyRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StrategyServiceTest {

    private static final AuthenticatedUser LEADER =
            new AuthenticatedUser("li-1", "Lideranca Demo", "lider@app.com", Role.LIDERANCA);

    @Mock
    private StrategyRepository strategyRepository;
    @Mock
    private StrategyHistoryRepository historyRepository;

    private StrategyService strategyService;

    @BeforeEach
    void setUp() {
        strategyService = new StrategyService(strategyRepository, historyRepository);
        when(strategyRepository.save(any(Strategy.class))).thenAnswer(call -> {
            Strategy strategy = call.getArgument(0);
            if (strategy.getId() == null) {
                strategy.setId("strat-1");
            }
            return strategy;
        });
    }

    private StrategyRequest request(String title, boolean active) {
        return new StrategyRequest(title, "Descricao da orientacao", "01/02/2026",
                "Operacoes", "Inova 2026", active);
    }

    private Strategy existing() {
        Strategy strategy = new Strategy();
        strategy.setId("strat-1");
        strategy.setTitle("Automacao de patio");
        strategy.setDescription("Original");
        strategy.setActive(true);
        return strategy;
    }

    @Test
    @DisplayName("criar orientacao grava o autor e registra historico CRIADA")
    void createRecordsHistory() {
        StrategyResponse response = strategyService.create(request("Automacao de patio", true), LEADER);

        assertThat(response.title()).isEqualTo("Automacao de patio");
        assertThat(response.createdBy()).isEqualTo("li-1");
        assertThat(response.active()).isTrue();

        ArgumentCaptor<StrategyHistory> captor = ArgumentCaptor.forClass(StrategyHistory.class);
        verify(historyRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo(StrategyAction.CRIADA);
        assertThat(captor.getValue().getChangedBy()).isEqualTo("li-1");
    }

    @Test
    @DisplayName("atualizar orientacao registra historico ATUALIZADA")
    void updateRecordsHistory() {
        when(strategyRepository.findById("strat-1")).thenReturn(Optional.of(existing()));

        StrategyResponse response = strategyService.update("strat-1", request("Automacao de patio", true), LEADER);

        assertThat(response.description()).isEqualTo("Descricao da orientacao");

        ArgumentCaptor<StrategyHistory> captor = ArgumentCaptor.forClass(StrategyHistory.class);
        verify(historyRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo(StrategyAction.ATUALIZADA);
    }

    @Test
    @DisplayName("desativar uma orientacao vigente registra historico DESATIVADA")
    void deactivationIsTrackedSeparately() {
        when(strategyRepository.findById("strat-1")).thenReturn(Optional.of(existing()));

        strategyService.update("strat-1", request("Automacao de patio", false), LEADER);

        ArgumentCaptor<StrategyHistory> captor = ArgumentCaptor.forClass(StrategyHistory.class);
        verify(historyRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo(StrategyAction.DESATIVADA);
        assertThat(captor.getValue().isActive()).isFalse();
    }

    @Test
    @DisplayName("excluir registra o snapshot antes de apagar, preservando o historico")
    void deleteRecordsHistoryBeforeRemoving() {
        when(strategyRepository.findById("strat-1")).thenReturn(Optional.of(existing()));

        strategyService.delete("strat-1", LEADER);

        ArgumentCaptor<StrategyHistory> captor = ArgumentCaptor.forClass(StrategyHistory.class);
        verify(historyRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo(StrategyAction.EXCLUIDA);
        verify(strategyRepository).deleteById("strat-1");
    }

    @Test
    @DisplayName("activeOnly filtra apenas as orientacoes vigentes")
    void listsOnlyActiveWhenRequested() {
        when(strategyRepository.findByActiveOrderByCreatedAtDesc(true)).thenReturn(List.of(existing()));

        List<StrategyResponse> result = strategyService.list(true);

        assertThat(result).hasSize(1);
        verify(strategyRepository).findByActiveOrderByCreatedAtDesc(true);
    }

    @Test
    @DisplayName("historico e devolvido do mais recente para o mais antigo")
    void returnsHistory() {
        StrategyHistory entry = StrategyHistory.snapshot(existing(), StrategyAction.CRIADA, "li-1");
        when(historyRepository.findByStrategyIdOrderByChangedAtDesc("strat-1")).thenReturn(List.of(entry));

        assertThat(strategyService.history("strat-1")).hasSize(1);
    }

    @Test
    @DisplayName("orientacao inexistente resulta em 404")
    void missingStrategyIsNotFound() {
        when(strategyRepository.findById("nao-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> strategyService.getById("nao-existe"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("vincular ideia/projeto a uma estrategia inexistente e rejeitado")
    void validatesStrategyReference() {
        when(strategyRepository.findById("fantasma")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> strategyService.validateLink("fantasma", null))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("o vinculo com a orientacao estrategica e obrigatorio")
    void requiresStrategyLink() {
        assertThatThrownBy(() -> strategyService.validateLink(null, null))
                .isInstanceOf(BadRequestException.class);

        assertThatThrownBy(() -> strategyService.validateLink("   ", null))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("um vinculo novo so aceita orientacao vigente")
    void refusesLinkToInactiveStrategy() {
        Strategy inactive = new Strategy();
        inactive.setId("est-desativada");
        inactive.setTitle("Sustentabilidade nas unidades");
        inactive.setActive(false);
        when(strategyRepository.findById("est-desativada")).thenReturn(Optional.of(inactive));

        assertThatThrownBy(() -> strategyService.validateLink("est-desativada", null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("nao esta vigente");
    }

    @Test
    @DisplayName("editar um registro antigo nao quebra se a orientacao dele foi desativada depois")
    void keepsExistingLinkToInactiveStrategy() {
        Strategy inactive = new Strategy();
        inactive.setId("est-desativada");
        inactive.setTitle("Sustentabilidade nas unidades");
        inactive.setActive(false);
        when(strategyRepository.findById("est-desativada")).thenReturn(Optional.of(inactive));

        // Mesmo id antes e depois: o vinculo nao mudou, entao a edicao segue.
        strategyService.validateLink("est-desativada", "est-desativada");
    }
}
