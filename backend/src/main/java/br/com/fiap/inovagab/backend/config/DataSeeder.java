package br.com.fiap.inovagab.backend.config;

import br.com.fiap.inovagab.backend.model.AiAnalysis;
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
import br.com.fiap.inovagab.backend.service.RankingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Massa de demonstracao do INOVA+.
 *
 * Idempotente por chave natural: usuario pelo e-mail, orientacao e ideia pelo
 * titulo, projeto pelo nome. Rodar de novo so acrescenta o que falta - nada e
 * duplicado e nada que o usuario editou pelo app e sobrescrito.
 *
 * Os numeros nao sao aleatorios. Eles foram escolhidos para que as telas
 * tenham o que mostrar:
 *  - o funil da lideranca afunila mesmo (30 ideias > 14 em analise > 10 aprovadas);
 *  - os quatro status de projeto aparecem na barra empilhada;
 *  - o ranking tem podio, meio de tabela e lanterna, sem empate no topo.
 */
@Component
@Profile("!test")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String DEMO_PASSWORD = "123456";

    /** Modelo registrado nas analises de demonstracao. */
    private static final String DEMO_AI_MODEL = "gemini-3.5-flash";

    // ── Contas fixas ────────────────────────────────────────────────────────

    private static final String OPERADOR = "operador@app.com";
    private static final String ANA = "ana.souza@app.com";
    private static final String CARLOS = "carlos.nunes@app.com";
    private static final String JULIANA = "juliana.prado@app.com";
    private static final String MARCOS = "marcos.vieira@app.com";
    private static final String BEATRIZ = "beatriz.lima@app.com";

    private static final List<OperatorSeed> OPERATOR_SEEDS = List.of(
            new OperatorSeed("Operador Demo", OPERADOR),
            new OperatorSeed("Ana Souza", ANA),
            new OperatorSeed("Carlos Nunes", CARLOS),
            new OperatorSeed("Juliana Prado", JULIANA),
            new OperatorSeed("Marcos Vieira", MARCOS),
            new OperatorSeed("Beatriz Lima", BEATRIZ)
    );

    // ── Orientacoes estrategicas ────────────────────────────────────────────

    private static final String EST_LOGISTICA = "logistica";
    private static final String EST_EFICIENCIA = "eficiencia";
    private static final String EST_PESSOAS = "pessoas";
    private static final String EST_ESG = "esg";
    private static final String EST_FROTA = "frota";
    private static final String EST_CLIENTE = "cliente";
    private static final String EST_DADOS = "dados";

    private static final List<StrategySeed> STRATEGY_SEEDS = List.of(
            new StrategySeed(EST_LOGISTICA,
                    "Digitalização da operação logística",
                    "Incentivar soluções que reduzam processos manuais e aumentem a eficiência operacional.",
                    "Tecnologia", "Inova 2026", "01/01/2026", true),
            new StrategySeed(EST_EFICIENCIA,
                    "Eficiência operacional e redução de custos",
                    "Buscar ideias que reduzam desperdícios, retrabalho e tempo de execução.",
                    "Operações", "Inova 2026", "01/01/2026", true),
            new StrategySeed(EST_PESSOAS,
                    "Segurança e experiência do colaborador",
                    "Promover iniciativas que melhorem a segurança, a comunicação e a rotina dos operadores.",
                    "Pessoas", "Inova 2026", "01/01/2026", true),
            new StrategySeed(EST_ESG,
                    "Sustentabilidade nas unidades",
                    "Reduzir consumo de papel, energia e recursos operacionais.",
                    "ESG", "Inova 2025", "01/01/2026", false),
            new StrategySeed(EST_FROTA,
                    "Gestão inteligente da frota",
                    "Usar dados de telemetria e manutenção para reduzir custo por quilômetro rodado.",
                    "Frota", "Inova 2026", "15/01/2026", true),
            new StrategySeed(EST_CLIENTE,
                    "Experiência do cliente ponta a ponta",
                    "Simplificar o contato do cliente com a empresa, do orçamento à entrega.",
                    "Comercial", "Inova 2026", "01/02/2026", true),
            new StrategySeed(EST_DADOS,
                    "Cultura de dados e decisão orientada",
                    "Levar indicadores confiáveis para quem decide, no momento em que a decisão é tomada.",
                    "Tecnologia", "Inova 2026", "01/02/2026", true)
    );

    // ── Ideias ──────────────────────────────────────────────────────────────

    private static final List<IdeaSeed> IDEA_SEEDS = List.of(

            // Aprovadas -----------------------------------------------------
            new IdeaSeed("Reduzir tempo de conferência de cargas",
                    "A conferência manual gera filas, atrasos e retrabalho no pátio.",
                    "Criar checklist digital com leitura por QR Code no celular do conferente.",
                    "Logística",
                    "Redução do tempo de conferência e aumento da produtividade da equipe.",
                    IdeaStatus.APROVADA, IdeaPriority.ALTA, EST_LOGISTICA, OPERADOR, 120, true,
                    new AiSeed(88, 90, 85, 80, 95, "ALTA_PRIORIDADE",
                            "Problema bem delimitado e solução de baixa complexidade técnica. "
                                    + "O ganho de tempo no pátio se converte diretamente em capacidade "
                                    + "de atendimento, com forte aderência à orientação de digitalização.")),

            new IdeaSeed("Checklist digital para manutenção preventiva",
                    "As inspeções preventivas ainda são registradas em papel.",
                    "Formulário digital com fotos e alertas automáticos de pendência.",
                    "Manutenção",
                    "Melhor controle preventivo e redução de falhas operacionais.",
                    IdeaStatus.APROVADA, IdeaPriority.ALTA, EST_EFICIENCIA, OPERADOR, 110, true,
                    new AiSeed(84, 82, 88, 74, 90, "ALTA_PRIORIDADE",
                            "Substituição direta de papel por registro estruturado. O maior valor "
                                    + "está no histórico gerado, que permite antecipar falhas em vez "
                                    + "de apenas registrá-las.")),

            new IdeaSeed("Roteirização automática de entregas urbanas",
                    "As rotas do dia são montadas manualmente e dependem da experiência do programador.",
                    "Motor de roteirização que considera janela de entrega, restrição de circulação e peso.",
                    "Logística",
                    "Menos quilômetros rodados por entrega e maior previsibilidade de horário.",
                    IdeaStatus.APROVADA, IdeaPriority.ALTA, EST_LOGISTICA, ANA, 98, true,
                    new AiSeed(91, 95, 78, 88, 92, "ALTA_PRIORIDADE",
                            "Alto impacto financeiro e operacional. Exige integração com o sistema "
                                    + "de pedidos, o que reduz a nota de viabilidade, mas o retorno "
                                    + "por quilômetro economizado justifica o esforço.")),

            new IdeaSeed("Telemetria para reduzir consumo de combustível",
                    "Não há visibilidade de aceleração brusca, marcha lenta excessiva e excesso de velocidade.",
                    "Instalar telemetria embarcada e devolver o indicador de condução ao próprio motorista.",
                    "Frota",
                    "Redução do consumo por quilômetro e queda no desgaste de freios e pneus.",
                    IdeaStatus.APROVADA, IdeaPriority.ALTA, EST_FROTA, CARLOS, 92, true,
                    new AiSeed(93, 96, 82, 79, 97, "ALTA_PRIORIDADE",
                            "Combustível é a maior linha de custo variável da operação. A devolução "
                                    + "do indicador ao motorista transforma o dado em mudança de "
                                    + "comportamento, que é onde o ganho realmente aparece.")),

            new IdeaSeed("Portal único de segunda via de documentos",
                    "O cliente liga para a central para pedir segunda via de nota e comprovante de entrega.",
                    "Área no site onde o cliente busca por CNPJ e baixa os documentos sozinho.",
                    "Atendimento",
                    "Menos ligações repetitivas na central e resposta imediata ao cliente.",
                    IdeaStatus.APROVADA, IdeaPriority.NORMAL, EST_CLIENTE, JULIANA, 85, false, null),

            new IdeaSeed("Painel de indicadores em tempo real nas unidades",
                    "Cada unidade fecha os números no fim do mês e descobre o desvio tarde demais.",
                    "Televisor na unidade com indicadores do dia atualizados automaticamente.",
                    "Tecnologia",
                    "Correção de rota dentro do próprio mês, com a equipe enxergando o resultado.",
                    IdeaStatus.APROVADA, IdeaPriority.NORMAL, EST_DADOS, ANA, 77, true,
                    new AiSeed(79, 74, 85, 70, 92, "ALTA_PRIORIDADE",
                            "Baixa complexidade e efeito cultural relevante. O risco é o painel "
                                    + "virar enfeite: precisa de poucos indicadores e de dono claro "
                                    + "para cada um.")),

            new IdeaSeed("Agendamento digital de docas",
                    "Caminhões chegam sem hora marcada e formam fila na entrada do centro de distribuição.",
                    "Agenda online de docas com confirmação por mensagem no celular do motorista.",
                    "Logística",
                    "Redução do tempo de espera do motorista e melhor uso das docas ao longo do dia.",
                    IdeaStatus.APROVADA, IdeaPriority.ALTA, EST_LOGISTICA, MARCOS, 70, true,
                    new AiSeed(86, 88, 84, 76, 90, "ALTA_PRIORIDADE",
                            "Resolve um gargalo visível e mensurável. Depende da adesão das "
                                    + "transportadoras parceiras, que deve ser tratada como parte "
                                    + "do projeto e não como premissa.")),

            new IdeaSeed("Conferência cega no recebimento",
                    "O conferente vê a quantidade esperada na nota e tende a confirmar sem contar.",
                    "Ocultar a quantidade esperada até o conferente digitar o que realmente contou.",
                    "Logística",
                    "Divergências de estoque identificadas na porta, e não no inventário.",
                    IdeaStatus.APROVADA, IdeaPriority.NORMAL, EST_EFICIENCIA, ANA, 68, false, null),

            new IdeaSeed("Treinamento por microvídeos no celular",
                    "O treinamento presencial tira o operador da escala e é difícil de repetir.",
                    "Trilha de vídeos de três minutos, assistidos no celular, com quiz ao final.",
                    "Pessoas",
                    "Reciclagem contínua sem parar a operação e registro de quem concluiu cada trilha.",
                    IdeaStatus.APROVADA, IdeaPriority.NORMAL, EST_PESSOAS, BEATRIZ, 64, false, null),

            new IdeaSeed("Inspeção de pneus por foto com leitura automática",
                    "A medição de sulco depende de parada em box e nem sempre é registrada.",
                    "Foto do pneu pelo celular com leitura automática do sulco e alerta de troca.",
                    "Manutenção",
                    "Troca no momento certo, com menos pneus descartados antes da hora e menos risco de estouro.",
                    IdeaStatus.APROVADA, IdeaPriority.ALTA, EST_FROTA, CARLOS, 58, false,
                    new AiSeed(81, 84, 68, 90, 88, "ALTA_PRIORIDADE",
                            "Solução inovadora e com bom impacto em custo e segurança. A leitura "
                                    + "automática por foto é a parte incerta: vale um piloto em uma "
                                    + "filial antes de escalar.")),

            // Priorizadas ---------------------------------------------------
            new IdeaSeed("Previsão de demanda por histórico de embarques",
                    "O planejamento de frota para o pico do mês é feito por sensibilidade.",
                    "Modelo simples de previsão usando o histórico dos últimos três anos.",
                    "Comercial",
                    "Frota dimensionada com antecedência, com menos frete de terceiro no pico.",
                    IdeaStatus.PRIORIZADA, IdeaPriority.ALTA, EST_DADOS, ANA, 51, false,
                    new AiSeed(77, 80, 66, 82, 86, "ALTA_PRIORIDADE",
                            "Depende da qualidade do histórico, que precisa ser avaliada antes de "
                                    + "qualquer promessa de acurácia. Começar com uma única rota "
                                    + "reduz o risco.")),

            new IdeaSeed("Alerta de fadiga do motorista por sensor",
                    "A fadiga em viagens longas só é percebida depois do incidente.",
                    "Sensor de atenção na cabine com alerta sonoro e registro para o gestor de frota.",
                    "Segurança",
                    "Redução de acidentes por desatenção e base objetiva para ajustar escalas.",
                    IdeaStatus.PRIORIZADA, IdeaPriority.ALTA, EST_PESSOAS, CARLOS, 45, false,
                    new AiSeed(83, 92, 62, 84, 89, "ALTA_PRIORIDADE",
                            "Impacto direto em vidas, o que por si só sustenta a prioridade. "
                                    + "Viabilidade menor pelo custo por veículo e pela necessidade "
                                    + "de acordo com a equipe sobre o uso dos dados.")),

            new IdeaSeed("Etiqueta inteligente para volumes fracionados",
                    "Volumes fracionados se perdem na transferência entre filiais.",
                    "Etiqueta com código único lido em cada ponto de passagem do volume.",
                    "Logística",
                    "Rastreio do volume dentro da malha e queda no índice de extravio.",
                    IdeaStatus.PRIORIZADA, IdeaPriority.ALTA, EST_LOGISTICA, OPERADOR, 43, false, null),

            new IdeaSeed("Reaproveitamento de pallets entre filiais",
                    "Pallets em bom estado são descartados em uma filial enquanto outra compra novos.",
                    "Cadastro compartilhado de pallets disponíveis, com transferência na carga de retorno.",
                    "ESG",
                    "Menos compra de pallet novo e menos descarte de madeira em bom estado.",
                    IdeaStatus.PRIORIZADA, IdeaPriority.NORMAL, EST_ESG, BEATRIZ, 38, false, null),

            // Em analise ----------------------------------------------------
            new IdeaSeed("Assinatura digital de canhotos de entrega",
                    "O canhoto de papel se perde no caminho de volta e atrasa o faturamento.",
                    "Assinatura do recebedor na tela do celular do motorista, com envio imediato.",
                    "Logística",
                    "Faturamento mais rápido e fim da busca por canhoto extraviado.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_LOGISTICA, OPERADOR, 32, false, null),

            new IdeaSeed("Central de peças compartilhada entre oficinas",
                    "Cada oficina mantém seu próprio estoque e algumas peças envelhecem na prateleira.",
                    "Visão única do estoque de peças com transferência rápida entre oficinas próximas.",
                    "Manutenção",
                    "Menos capital parado em peça e menos veículo esperando reposição.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_EFICIENCIA, MARCOS, 27, false, null),

            new IdeaSeed("Registro de ocorrências de entrega por foto",
                    "Avarias são descritas por texto no rádio e geram discussão sobre responsabilidade.",
                    "Registro da ocorrência com foto, horário e local direto no aplicativo do motorista.",
                    "Logística",
                    "Prova objetiva da ocorrência e tratativa mais rápida com o cliente.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_LOGISTICA, OPERADOR, 24, false, null),

            new IdeaSeed("Chat de suporte interno com base de conhecimento",
                    "Duvidas de sistema entopem o telefone do suporte com perguntas repetidas.",
                    "Chat interno que responde às dúvidas frequentes a partir dos manuais já existentes.",
                    "Tecnologia",
                    "Resposta imediata para o operador e suporte livre para o que é realmente novo.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.BAIXA, EST_DADOS, JULIANA, 21, false, null),

            new IdeaSeed("Leitura automática de placas na portaria",
                    "O registro de entrada e saída de veículos é digitado pelo porteiro.",
                    "Câmera com leitura de placa que registra a passagem automaticamente.",
                    "Segurança",
                    "Fila menor na portaria e registro confiável de permanência no pátio.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_LOGISTICA, MARCOS, 19, false, null),

            new IdeaSeed("Mapa de calor de ocorrências de segurança",
                    "As ocorrências são registradas, mas ninguém enxerga onde elas se concentram.",
                    "Mapa por unidade e por turno destacando os pontos de maior recorrência.",
                    "Segurança",
                    "Ação preventiva dirigida ao ponto certo em vez de campanha genérica.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_PESSOAS, CARLOS, 16, false, null),

            new IdeaSeed("Reuso de água no lavador de veículos",
                    "A lavagem da frota usa água tratada e o volume mensal é alto.",
                    "Sistema de captação e filtragem para reutilizar a água da própria lavagem.",
                    "ESG",
                    "Redução do consumo de água tratada e da conta mensal da unidade.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_ESG, CARLOS, 14, false, null),

            new IdeaSeed("Guia rápido de procedimentos no crachá QR",
                    "O procedimento correto está no manual, que ninguém consulta no meio do turno.",
                    "QR Code no crachá que abre o guia rápido da função no celular.",
                    "Pessoas",
                    "Procedimento na mão no momento da dúvida, sem precisar procurar o supervisor.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.BAIXA, EST_PESSOAS, OPERADOR, 12, false, null),

            new IdeaSeed("Programa de carona corporativa entre filiais",
                    "Colaboradores de filiais próximas fazem o mesmo trajeto em carros separados.",
                    "Mural interno de caronas com pontos de encontro e horários fixos.",
                    "ESG",
                    "Menos carros no trajeto, menos emissão e economia para o colaborador.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.BAIXA, EST_ESG, ANA, 11, false, null),

            new IdeaSeed("Catálogo digital de serviços para o cliente",
                    "O cliente não sabe quais serviços a empresa oferece além do que já contrata.",
                    "Catálogo digital enviado no primeiro contato, com prazo e cobertura de cada serviço.",
                    "Comercial",
                    "Mais serviços por cliente e menos dúvida no momento da cotação.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.BAIXA, EST_CLIENTE, ANA, 9, false, null),

            new IdeaSeed("Formulário digital de reembolso de despesas",
                    "O reembolso de viagem circula em papel e demora a chegar ao financeiro.",
                    "Formulário digital com foto do comprovante e aprovação pelo celular do gestor.",
                    "Financeiro",
                    "Reembolso pago no prazo e rastreabilidade de cada aprovação.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.BAIXA, EST_EFICIENCIA, JULIANA, 8, false, null),

            new IdeaSeed("Quiosque de autoatendimento na rodoviária",
                    "A fila do guichê cresce nos horários de pico e o passageiro perde o embarque.",
                    "Quiosque para compra e impressão de passagem sem passar pelo guichê.",
                    "Atendimento",
                    "Fila menor no pico e atendente livre para o caso que exige conversa.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_CLIENTE, BEATRIZ, 6, false, null),

            new IdeaSeed("Painel de metas visível no refeitório",
                    "A equipe operacional só sabe o resultado da unidade quando o supervisor comenta.",
                    "Quadro simples no refeitório com a meta do mês e o realizado até o dia.",
                    "Pessoas",
                    "Equipe acompanhando o próprio resultado sem depender de reunião.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.BAIXA, EST_PESSOAS, JULIANA, 4, false, null),

            new IdeaSeed("Banco de talentos interno para vagas",
                    "Vagas são abertas ao mercado sem olhar quem já está na casa.",
                    "Cadastro interno de interesse e competências, consultado antes da divulgação externa.",
                    "Pessoas",
                    "Mais movimentação interna, integração mais rápida e retenção de quem quer crescer.",
                    IdeaStatus.EM_ANALISE, IdeaPriority.NORMAL, EST_PESSOAS, BEATRIZ, 3, false, null),

            // Rejeitadas ----------------------------------------------------
            new IdeaSeed("Sorteio mensal de brindes para engajar equipes",
                    "A participação no programa de ideias cai depois do primeiro mês.",
                    "Sorteio mensal de brindes entre todos que enviarem qualquer ideia.",
                    "Pessoas",
                    "Aumento do número de ideias enviadas por mês.",
                    IdeaStatus.REJEITADA, IdeaPriority.BAIXA, EST_PESSOAS, MARCOS, 74, false,
                    new AiSeed(38, 30, 72, 25, 28, "BAIXA_PRIORIDADE",
                            "Premiar volume tende a gerar ideias de baixa qualidade e não ataca a "
                                    + "causa da queda de participação, que costuma ser a falta de "
                                    + "retorno sobre as ideias já enviadas.")),

            new IdeaSeed("Substituir a frota inteira por veículos elétricos em seis meses",
                    "A frota a diesel responde pela maior parte da emissão da operação.",
                    "Trocar todos os veículos por elétricos no prazo de seis meses.",
                    "Frota",
                    "Redução imediata da emissão de carbono da operação.",
                    IdeaStatus.REJEITADA, IdeaPriority.BAIXA, EST_FROTA, JULIANA, 40, false,
                    new AiSeed(41, 88, 8, 60, 70, "REVISAR",
                            "O objetivo é correto, o prazo e o escopo não são. Não há autonomia, "
                                    + "infraestrutura de recarga nem capital para a troca total em "
                                    + "seis meses. Vale reapresentar como piloto em rota urbana curta."))
    );

    // ── Projetos ────────────────────────────────────────────────────────────

    private static final List<ProjectSeed> PROJECT_SEEDS = List.of(

            new ProjectSeed("Digitalização da conferência operacional",
                    "Implantação de checklist digital para reduzir o tempo de conferência.",
                    "Equipe de Operações", ProjectStatus.EM_ANDAMENTO, "Piloto em unidade operacional",
                    10_000, 25_000, 5_000, 18.0, "30/06/2026",
                    EST_LOGISTICA, "Reduzir tempo de conferência de cargas", 112),

            new ProjectSeed("Automação de alertas de manutenção",
                    "Alertas preventivos para manutenção de veículos e equipamentos.",
                    "Equipe de Manutenção", ProjectStatus.CONCLUIDO, "Concluído",
                    8_000, 21_000, 6_000, 15.0, "01/06/2026",
                    EST_EFICIENCIA, "Checklist digital para manutenção preventiva", 104),

            new ProjectSeed("Roteirização inteligente de entregas urbanas",
                    "Motor de roteirização integrado ao sistema de pedidos, com piloto em três cidades.",
                    "Planejamento Logístico", ProjectStatus.EM_ANDAMENTO, "Piloto em três cidades",
                    45_000, 132_000, 28_000, 22.0, "30/09/2026",
                    EST_LOGISTICA, "Roteirização automática de entregas urbanas", 90),

            new ProjectSeed("Telemetria embarcada na frota pesada",
                    "Telemetria instalada em toda a frota pesada, com indicador de condução por motorista.",
                    "Gestão de Frota", ProjectStatus.CONCLUIDO, "Concluído",
                    120_000, 310_000, 74_000, 12.0, "15/03/2026",
                    EST_FROTA, "Telemetria para reduzir consumo de combustível", 84),

            new ProjectSeed("Painel executivo de indicadores",
                    "Indicadores operacionais e financeiros consolidados em painel único por unidade.",
                    "Tecnologia da Informação", ProjectStatus.EM_ANDAMENTO, "Integração das fontes de dados",
                    32_000, 68_000, 9_000, 20.0, "31/10/2026",
                    EST_DADOS, "Painel de indicadores em tempo real nas unidades", 70),

            new ProjectSeed("Agendamento digital de docas",
                    "Agenda online de docas com confirmação por mensagem, começando pelo CD principal.",
                    "Centro de Distribuição", ProjectStatus.PLANEJADO, "Levantamento de requisitos",
                    28_000, 76_000, 19_000, 25.0, "31/12/2026",
                    EST_LOGISTICA, "Agendamento digital de docas", 60),

            new ProjectSeed("Modernização do WMS do centro de distribuição",
                    "Substituição do sistema de gestão de armazém, com endereçamento e inventário cíclico.",
                    "Centro de Distribuição", ProjectStatus.EM_ANDAMENTO, "Migração de dados",
                    210_000, 465_000, 96_000, 17.0, "30/11/2026",
                    EST_LOGISTICA, null, 150),

            new ProjectSeed("Programa de segurança comportamental",
                    "Ciclo de observação e feedback de comportamento seguro em todas as unidades.",
                    "Segurança do Trabalho", ProjectStatus.CONCLUIDO, "Concluído",
                    56_000, 143_000, 41_000, 9.0, "28/02/2026",
                    EST_PESSOAS, null, 200),

            new ProjectSeed("Nota fiscal eletrônica integrada ao cliente",
                    "Envio automático de nota e comprovante de entrega para o sistema do cliente.",
                    "Financeiro", ProjectStatus.CONCLUIDO, "Concluído",
                    38_000, 97_000, 22_000, 14.0, "31/01/2026",
                    EST_CLIENTE, null, 175),

            new ProjectSeed("Troca de iluminação para LED nas filiais",
                    "Substituição da iluminação de pátios e galpões por LED em doze filiais.",
                    "Manutenção Predial", ProjectStatus.CONCLUIDO, "Concluído",
                    64_000, 108_000, 44_000, 4.0, "20/12/2025",
                    EST_ESG, null, 240),

            new ProjectSeed("Aplicativo de autoatendimento do passageiro",
                    "Compra de passagem, remarcação e embarque pelo celular, sem passar pelo guichê.",
                    "Experiência do Cliente", ProjectStatus.PLANEJADO, "Aprovação de orçamento",
                    95_000, 240_000, 31_000, 16.0, "30/04/2027",
                    EST_CLIENTE, null, 35),

            new ProjectSeed("Plataforma de dados unificada",
                    "Consolidação das bases em uma plataforma única. Suspenso após revisão de escopo.",
                    "Tecnologia da Informação", ProjectStatus.CANCELADO, "Cancelado na fase de arquitetura",
                    18_000, 0, 0, 0.0, "30/08/2026",
                    EST_DADOS, null, 130)
    );

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
        User gestor = seedUser("Gestor Demo", "gestor@app.com", Role.GESTOR, 0);
        User leader = seedUser("Liderança Demo", "lider@app.com", Role.LIDERANCA, 0);

        Map<String, User> operators = new LinkedHashMap<>();
        for (OperatorSeed seed : OPERATOR_SEEDS) {
            operators.put(seed.email(),
                    seedUser(seed.name(), seed.email(), Role.OPERADOR, initialPointsOf(seed.email())));
        }

        Map<String, Strategy> strategies = seedStrategies(leader);
        seedIdeas(operators, strategies, gestor);
        seedProjects(strategies);
    }

    // ── Usuarios ────────────────────────────────────────────────────────────

    /**
     * Pontos iniciais do operador, calculados a partir das proprias ideias do
     * seed e das regras oficiais do {@link RankingService}. Assim o ranking de
     * demonstracao nunca contradiz a regra de pontuacao do sistema.
     */
    private static int initialPointsOf(String email) {
        int points = 0;
        for (IdeaSeed seed : IDEA_SEEDS) {
            if (!seed.operatorEmail().equalsIgnoreCase(email)) {
                continue;
            }
            points += RankingService.POINTS_IDEA_CREATED;
            if (seed.status() == IdeaStatus.APROVADA) {
                points += RankingService.POINTS_IDEA_APPROVED;
            }
            if (seed.converted()) {
                points += RankingService.POINTS_IDEA_CONVERTED;
            }
        }
        return points;
    }

    private User seedUser(String name, String email, Role role, int points) {
        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User user = new User(name, email, passwordEncoder.encode(DEMO_PASSWORD), role);
        user.setPoints(points);
        User saved = userRepository.save(user);
        log.info("Usuário de demonstração criado: {} ({}, {} pontos)", email, role, points);
        return saved;
    }

    // ── Orientacoes estrategicas ────────────────────────────────────────────

    private Map<String, Strategy> seedStrategies(User leader) {
        Map<String, Strategy> byKey = new LinkedHashMap<>();

        for (StrategySeed seed : STRATEGY_SEEDS) {
            Strategy strategy = strategyRepository.findByTitleIgnoreCase(seed.title())
                    .orElseGet(() -> createStrategy(seed, leader));
            byKey.put(seed.key(), strategy);
        }
        return byKey;
    }

    private Strategy createStrategy(StrategySeed seed, User leader) {
        Strategy strategy = new Strategy();
        strategy.setTitle(seed.title());
        strategy.setDescription(seed.description());
        strategy.setCategory(seed.category());
        strategy.setCampaign(seed.campaign());
        strategy.setDate(seed.date());
        strategy.setActive(seed.active());
        strategy.setCreatedAt(Instant.now());
        strategy.setUpdatedAt(strategy.getCreatedAt());
        strategy.setCreatedBy(leader.getId());

        Strategy saved = strategyRepository.save(strategy);
        strategyHistoryRepository.save(
                StrategyHistory.snapshot(saved, StrategyAction.CRIADA, leader.getId()));
        log.info("Orientação estratégica de demonstração criada: {}", saved.getTitle());
        return saved;
    }

    // ── Ideias ──────────────────────────────────────────────────────────────

    private void seedIdeas(Map<String, User> operators, Map<String, Strategy> strategies, User gestor) {
        int created = 0;

        for (IdeaSeed seed : IDEA_SEEDS) {
            if (!ideaRepository.findByTitleIgnoreCase(seed.title()).isEmpty()) {
                continue;
            }

            User operator = operators.get(seed.operatorEmail());
            Strategy strategy = strategies.get(seed.strategyKey());
            if (operator == null || strategy == null) {
                log.warn("Ideia de demonstração ignorada por vínculo ausente: {}", seed.title());
                continue;
            }

            Instant createdAt = Instant.now().minus(seed.daysAgo(), ChronoUnit.DAYS);

            Idea idea = new Idea();
            idea.setTitle(seed.title());
            idea.setProblem(seed.problem());
            idea.setSolution(seed.solution());
            idea.setArea(seed.area());
            idea.setBenefit(seed.benefit());
            idea.setStatus(seed.status());
            idea.setPriority(seed.priority());
            idea.setStrategyId(strategy.getId());
            idea.setOperatorId(operator.getId());
            idea.setOperatorName(operator.getName());
            idea.setCreatedAt(createdAt);
            idea.setUpdatedAt(createdAt);
            idea.setConvertedToProject(seed.converted());

            if (seed.status() == IdeaStatus.APROVADA) {
                idea.setApprovedAt(createdAt.plus(5, ChronoUnit.DAYS));
            }
            if (seed.ai() != null) {
                idea.setAiAnalysis(toAnalysis(seed.ai(), createdAt, gestor.getId()));
            }

            // Os pontos ja entraram no saldo inicial do operador (initialPointsOf).
            // Marcar aqui evita que o mesmo evento pontue de novo pelo app.
            idea.setCreationPointsAwarded(true);
            idea.setApprovalPointsAwarded(seed.status() == IdeaStatus.APROVADA);
            idea.setConversionPointsAwarded(seed.converted());

            ideaRepository.save(idea);
            created++;
        }

        if (created > 0) {
            log.info("Ideias de demonstração criadas: {}", created);
        }
    }

    private AiAnalysis toAnalysis(AiSeed seed, Instant ideaCreatedAt, String gestorId) {
        AiAnalysis analysis = new AiAnalysis();
        analysis.setScore(seed.score());
        analysis.setImpactScore(seed.impact());
        analysis.setFeasibilityScore(seed.feasibility());
        analysis.setInnovationScore(seed.innovation());
        analysis.setStrategicAlignmentScore(seed.alignment());
        analysis.setRecommendation(seed.recommendation());
        analysis.setSummary(seed.summary());
        analysis.setAnalyzedAt(ideaCreatedAt.plus(1, ChronoUnit.DAYS));
        analysis.setAnalyzedBy(gestorId);
        analysis.setModel(DEMO_AI_MODEL);
        return analysis;
    }

    // ── Projetos ────────────────────────────────────────────────────────────

    private void seedProjects(Map<String, Strategy> strategies) {
        int created = 0;

        for (ProjectSeed seed : PROJECT_SEEDS) {
            if (projectRepository.existsByNameIgnoreCase(seed.name())) {
                continue;
            }

            Strategy strategy = strategies.get(seed.strategyKey());
            if (strategy == null) {
                log.warn("Projeto de demonstração ignorado por orientação ausente: {}", seed.name());
                continue;
            }

            Instant createdAt = Instant.now().minus(seed.daysAgo(), ChronoUnit.DAYS);

            Project project = new Project();
            project.setName(seed.name());
            project.setDescription(seed.description());
            project.setResponsible(seed.responsible());
            project.setStatus(seed.status());
            project.setCurrentStage(seed.currentStage());
            project.setInvestment(seed.investment());
            project.setFinancialReturn(seed.financialReturn());
            project.setCostReduction(seed.costReduction());
            project.setProductivityGain(seed.productivityGain());
            project.setDeadline(seed.deadline());
            project.setStrategyId(strategy.getId());
            project.setCreatedAt(createdAt);
            project.setUpdatedAt(createdAt);

            // Projeto nascido de uma ideia guarda o vinculo; os demais foram
            // cadastrados direto pelo gestor e ficam sem origem.
            if (seed.sourceIdeaTitle() != null) {
                ideaRepository.findByTitleIgnoreCase(seed.sourceIdeaTitle()).stream()
                        .findFirst()
                        .ifPresent(idea -> project.setIdeaId(idea.getId()));
            }

            projectRepository.save(project);
            created++;
        }

        if (created > 0) {
            log.info("Projetos de demonstração criados: {}", created);
        }
    }

    // ── Descricao dos dados de demonstracao ─────────────────────────────────

    private record OperatorSeed(String name, String email) {
    }

    private record StrategySeed(String key, String title, String description, String category,
                                String campaign, String date, boolean active) {
    }

    /** Analise de IA pre-gravada, para as telas terem conteudo sem chamar o Gemini. */
    private record AiSeed(int score, int impact, int feasibility, int innovation, int alignment,
                          String recommendation, String summary) {
    }

    private record IdeaSeed(String title, String problem, String solution, String area, String benefit,
                            IdeaStatus status, IdeaPriority priority, String strategyKey,
                            String operatorEmail, int daysAgo, boolean converted, AiSeed ai) {
    }

    private record ProjectSeed(String name, String description, String responsible, ProjectStatus status,
                               String currentStage, double investment, double financialReturn,
                               double costReduction, double productivityGain, String deadline,
                               String strategyKey, String sourceIdeaTitle, int daysAgo) {
    }
}
