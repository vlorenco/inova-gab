package br.com.fiap.inovagab.backend.config;

import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaPriority;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import br.com.fiap.inovagab.backend.model.Project;
import br.com.fiap.inovagab.backend.model.ProjectStatus;
import br.com.fiap.inovagab.backend.model.Role;
import br.com.fiap.inovagab.backend.model.Strategy;
import br.com.fiap.inovagab.backend.model.StrategyAction;
import br.com.fiap.inovagab.backend.model.StrategyHistory;
import br.com.fiap.inovagab.backend.model.User;
import br.com.fiap.inovagab.backend.repository.IdeaRepository;
import br.com.fiap.inovagab.backend.repository.ProjectRepository;
import br.com.fiap.inovagab.backend.repository.StrategyHistoryRepository;
import br.com.fiap.inovagab.backend.repository.StrategyRepository;
import br.com.fiap.inovagab.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Seed idempotente. Roda a cada boot, mas so cria o que ainda nao existe -
 * nada e duplicado e nenhum dado editado pelo usuario e sobrescrito.
 */
@Component
@Profile("!test")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String DEMO_PASSWORD = "123456";

    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;
    private final StrategyHistoryRepository strategyHistoryRepository;
    private final IdeaRepository ideaRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      StrategyRepository strategyRepository,
                      StrategyHistoryRepository strategyHistoryRepository,
                      IdeaRepository ideaRepository,
                      ProjectRepository projectRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.strategyRepository = strategyRepository;
        this.strategyHistoryRepository = strategyHistoryRepository;
        this.ideaRepository = ideaRepository;
        this.projectRepository = projectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        User operator = seedUser("Operador Demo", "operador@app.com", Role.OPERADOR);
        seedUser("Gestor Demo", "gestor@app.com", Role.GESTOR);
        User leader = seedUser("Lideranca Demo", "lider@app.com", Role.LIDERANCA);
        seedUser("Ana Souza", "ana.souza@app.com", Role.OPERADOR);

        List<Strategy> strategies = seedStrategies(leader);
        seedIdeas(operator, strategies);
        seedProjects(strategies);
    }

    private User seedUser(String name, String email, Role role) {
        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User user = new User(name, email, passwordEncoder.encode(DEMO_PASSWORD), role);
        User saved = userRepository.save(user);
        log.info("Usuario de demonstracao criado: {} ({})", email, role);
        return saved;
    }

    private List<Strategy> seedStrategies(User leader) {
        record Seed(String title, String description, String category, String campaign, boolean active) {
        }

        List<Seed> seeds = List.of(
                new Seed("Digitalizacao da operacao logistica",
                        "Incentivar solucoes que reduzam processos manuais e aumentem a eficiencia operacional.",
                        "Tecnologia", "Inova 2026", true),
                new Seed("Eficiencia operacional e reducao de custos",
                        "Buscar ideias que reduzam desperdicios, retrabalho e tempo de execucao.",
                        "Operacoes", "Inova 2026", true),
                new Seed("Seguranca e experiencia do colaborador",
                        "Promover iniciativas que melhorem a seguranca, a comunicacao e a rotina dos operadores.",
                        "Pessoas", "Inova 2026", true),
                new Seed("Sustentabilidade nas unidades",
                        "Reduzir consumo de papel, energia e recursos operacionais.",
                        "ESG", "Inova 2025", false)
        );

        return seeds.stream().map(seed -> strategyRepository.findByTitleIgnoreCase(seed.title())
                .orElseGet(() -> {
                    Strategy strategy = new Strategy();
                    strategy.setTitle(seed.title());
                    strategy.setDescription(seed.description());
                    strategy.setCategory(seed.category());
                    strategy.setCampaign(seed.campaign());
                    strategy.setDate("01/01/2026");
                    strategy.setActive(seed.active());
                    strategy.setCreatedAt(Instant.now());
                    strategy.setUpdatedAt(strategy.getCreatedAt());
                    strategy.setCreatedBy(leader.getId());

                    Strategy saved = strategyRepository.save(strategy);
                    strategyHistoryRepository.save(
                            StrategyHistory.snapshot(saved, StrategyAction.CRIADA, leader.getId()));
                    log.info("Orientacao estrategica de demonstracao criada: {}", saved.getTitle());
                    return saved;
                })).toList();
    }

    private void seedIdeas(User operator, List<Strategy> strategies) {
        if (ideaRepository.count() > 0) {
            return;
        }

        Idea first = new Idea();
        first.setTitle("Reduzir tempo de conferencia de cargas");
        first.setProblem("A conferencia manual gera filas, atrasos e retrabalho no patio.");
        first.setSolution("Criar checklist digital com leitura por QR Code no celular do conferente.");
        first.setArea("Logistica");
        first.setBenefit("Reducao do tempo de conferencia e aumento da produtividade da equipe.");
        first.setStatus(IdeaStatus.EM_ANALISE);
        first.setPriority(IdeaPriority.NORMAL);
        first.setStrategyId(strategies.get(0).getId());

        Idea second = new Idea();
        second.setTitle("Checklist digital para manutencao preventiva");
        second.setProblem("As inspecoes preventivas ainda sao registradas em papel.");
        second.setSolution("Formulario digital com fotos e alertas automaticos de pendencia.");
        second.setArea("Manutencao");
        second.setBenefit("Melhor controle preventivo e reducao de falhas operacionais.");
        second.setStatus(IdeaStatus.PRIORIZADA);
        second.setPriority(IdeaPriority.ALTA);
        second.setStrategyId(strategies.get(1).getId());

        for (Idea idea : List.of(first, second)) {
            idea.setOperatorId(operator.getId());
            idea.setOperatorName(operator.getName());
            idea.setCreatedAt(Instant.now());
            idea.setUpdatedAt(idea.getCreatedAt());
            // Marcado como ja pontuado: o seed nao infla o ranking.
            idea.setCreationPointsAwarded(true);
            ideaRepository.save(idea);
        }
        log.info("Ideias de demonstracao criadas.");
    }

    private void seedProjects(List<Strategy> strategies) {
        if (projectRepository.count() > 0) {
            return;
        }

        Project running = new Project();
        running.setName("Digitalizacao da conferencia operacional");
        running.setDescription("Implantacao de checklist digital para reduzir o tempo de conferencia.");
        running.setResponsible("Equipe de Operacoes");
        running.setStatus(ProjectStatus.EM_ANDAMENTO);
        running.setCurrentStage("Piloto em unidade operacional");
        running.setInvestment(10000.0);
        running.setFinancialReturn(25000.0);
        running.setCostReduction(5000.0);
        running.setProductivityGain(18.0);
        running.setDeadline("30/06/2026");
        running.setStrategyId(strategies.get(0).getId());

        Project done = new Project();
        done.setName("Automacao de alertas de manutencao");
        done.setDescription("Alertas preventivos para manutencao de veiculos e equipamentos.");
        done.setResponsible("Equipe de Manutencao");
        done.setStatus(ProjectStatus.CONCLUIDO);
        done.setCurrentStage("Concluido");
        done.setInvestment(8000.0);
        done.setFinancialReturn(21000.0);
        done.setCostReduction(6000.0);
        done.setProductivityGain(15.0);
        done.setDeadline("01/06/2026");
        done.setStrategyId(strategies.get(1).getId());

        for (Project project : List.of(running, done)) {
            project.setCreatedAt(Instant.now());
            project.setUpdatedAt(project.getCreatedAt());
            projectRepository.save(project);
        }
        log.info("Projetos de demonstracao criados.");
    }
}
