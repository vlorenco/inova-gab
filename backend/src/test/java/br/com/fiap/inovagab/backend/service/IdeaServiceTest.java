package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.ai.AiAnalysisService;
import br.com.fiap.inovagab.backend.dto.idea.IdeaRequest;
import br.com.fiap.inovagab.backend.dto.idea.IdeaResponse;
import br.com.fiap.inovagab.backend.exception.ForbiddenException;
import br.com.fiap.inovagab.backend.exception.NotFoundException;
import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaPriority;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.repository.IdeaRepository;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdeaServiceTest {

    private static final AuthenticatedUser OPERATOR =
            new AuthenticatedUser("op-1", "Operador Demo", "operador@app.com", Role.OPERADOR);

    private static final AuthenticatedUser OTHER_OPERATOR =
            new AuthenticatedUser("op-2", "Ana Souza", "ana@app.com", Role.OPERADOR);

    private static final AuthenticatedUser MANAGER =
            new AuthenticatedUser("ge-1", "Gestor Demo", "gestor@app.com", Role.GESTOR);

    @Mock
    private IdeaRepository ideaRepository;
    @Mock
    private StrategyService strategyService;
    @Mock
    private RankingService rankingService;
    @Mock
    private AiAnalysisService aiAnalysisService;

    private IdeaService ideaService;

    @BeforeEach
    void setUp() {
        ideaService = new IdeaService(ideaRepository, strategyService, rankingService, aiAnalysisService);
        // Devolve a propria entidade salva, como faria o MongoRepository.
        when(ideaRepository.save(any(Idea.class))).thenAnswer(call -> call.getArgument(0));
    }

    private Idea existingIdeaOf(String ownerId) {
        Idea idea = new Idea();
        idea.setId("idea-1");
        idea.setTitle("Fila unica de carregamento");
        idea.setProblem("Filas paralelas sem ordem.");
        idea.setSolution("Senha digital.");
        idea.setOperatorId(ownerId);
        idea.setOperatorName("Operador Demo");
        idea.setStatus(IdeaStatus.EM_ANALISE);
        idea.setPriority(IdeaPriority.NORMAL);
        return idea;
    }

    private IdeaRequest request() {
        return new IdeaRequest("Titulo", "Problema", "Solucao", "Patio", "Beneficio", null);
    }

    @Test
    @DisplayName("operador cria ideia e o dono vem do JWT, nao do corpo da requisicao")
    void createsIdeaUsingAuthenticatedOperator() {
        IdeaResponse response = ideaService.create(request(), OPERATOR);

        assertThat(response.operatorId()).isEqualTo("op-1");
        assertThat(response.operatorName()).isEqualTo("Operador Demo");
        assertThat(response.status()).isEqualTo("EM_ANALISE");
        assertThat(response.priority()).isEqualTo("NORMAL");
    }

    @Test
    @DisplayName("criar ideia credita 10 pontos ao operador uma unica vez")
    void awardsCreationPoints() {
        ideaService.create(request(), OPERATOR);

        verify(rankingService).awardPoints("op-1", RankingService.POINTS_IDEA_CREATED);

        ArgumentCaptor<Idea> saved = ArgumentCaptor.forClass(Idea.class);
        verify(ideaRepository, times(2)).save(saved.capture());
        assertThat(saved.getValue().isCreationPointsAwarded()).isTrue();
    }

    @Test
    @DisplayName("operador nao consegue ver a ideia de outro operador trocando o id na URL")
    void blocksReadingAnotherOperatorsIdea() {
        when(ideaRepository.findById("idea-1")).thenReturn(Optional.of(existingIdeaOf("op-1")));

        assertThatThrownBy(() -> ideaService.getById("idea-1", OTHER_OPERATOR))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("suas proprias ideias");
    }

    @Test
    @DisplayName("operador nao consegue editar nem excluir a ideia de outro operador")
    void blocksWritingAnotherOperatorsIdea() {
        when(ideaRepository.findById("idea-1")).thenReturn(Optional.of(existingIdeaOf("op-1")));

        assertThatThrownBy(() -> ideaService.update("idea-1", request(), OTHER_OPERATOR))
                .isInstanceOf(ForbiddenException.class);
        assertThatThrownBy(() -> ideaService.delete("idea-1", OTHER_OPERATOR))
                .isInstanceOf(ForbiddenException.class);

        verify(ideaRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("gestor enxerga a ideia de qualquer operador")
    void managerSeesAnyIdea() {
        when(ideaRepository.findById("idea-1")).thenReturn(Optional.of(existingIdeaOf("op-1")));

        IdeaResponse response = ideaService.getById("idea-1", MANAGER);

        assertThat(response.id()).isEqualTo("idea-1");
    }

    @Test
    @DisplayName("gestor aprova a ideia: status, data de aprovacao e 50 pontos")
    void managerApprovesIdea() {
        when(ideaRepository.findById("idea-1")).thenReturn(Optional.of(existingIdeaOf("op-1")));

        IdeaResponse response = ideaService.updateStatus("idea-1", IdeaStatus.APROVADA);

        assertThat(response.status()).isEqualTo("APROVADA");
        assertThat(response.approvedAt()).isNotNull();
        verify(rankingService).awardPoints("op-1", RankingService.POINTS_IDEA_APPROVED);
    }

    @Test
    @DisplayName("reaprovar a mesma ideia nao credita pontos de novo")
    void approvalPointsAreIdempotent() {
        Idea alreadyApproved = existingIdeaOf("op-1");
        alreadyApproved.setStatus(IdeaStatus.APROVADA);
        alreadyApproved.setApprovalPointsAwarded(true);
        when(ideaRepository.findById("idea-1")).thenReturn(Optional.of(alreadyApproved));

        ideaService.updateStatus("idea-1", IdeaStatus.APROVADA);

        verify(rankingService, never()).awardPoints(anyString(), anyInt());
    }

    @Test
    @DisplayName("priorizar como ALTA move a ideia de EM_ANALISE para PRIORIZADA")
    void prioritizingMovesStatus() {
        when(ideaRepository.findById("idea-1")).thenReturn(Optional.of(existingIdeaOf("op-1")));

        IdeaResponse response = ideaService.updatePriority("idea-1", IdeaPriority.ALTA);

        assertThat(response.priority()).isEqualTo("ALTA");
        assertThat(response.status()).isEqualTo("PRIORIZADA");
    }

    @Test
    @DisplayName("converter ideia em projeto credita 100 pontos uma unica vez")
    void conversionPointsAreIdempotent() {
        Idea idea = existingIdeaOf("op-1");

        ideaService.markConvertedToProject(idea);
        ideaService.markConvertedToProject(idea);

        assertThat(idea.isConvertedToProject()).isTrue();
        verify(rankingService, times(1)).awardPoints("op-1", RankingService.POINTS_IDEA_CONVERTED);
    }

    @Test
    @DisplayName("ideia inexistente resulta em 404")
    void missingIdeaIsNotFound() {
        when(ideaRepository.findById("nao-existe")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ideaService.getById("nao-existe", MANAGER))
                .isInstanceOf(NotFoundException.class);
    }
}
