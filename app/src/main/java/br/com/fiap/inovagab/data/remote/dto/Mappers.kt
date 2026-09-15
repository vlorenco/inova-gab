package br.com.fiap.inovagab.data.remote.dto

import br.com.fiap.inovagab.data.model.AiAnalysis
import br.com.fiap.inovagab.data.model.DashboardSummary
import br.com.fiap.inovagab.data.model.Idea
import br.com.fiap.inovagab.data.model.MyRanking
import br.com.fiap.inovagab.data.model.Project
import br.com.fiap.inovagab.data.model.RankingEntry
import br.com.fiap.inovagab.data.model.Strategy
import br.com.fiap.inovagab.data.model.StrategyHistoryEntry
import br.com.fiap.inovagab.data.model.User

// DTO (formato do JSON) -> modelo de dominio usado pelas telas.

fun UserDto.toDomain() = User(
    id = id,
    name = name,
    email = email,
    role = role.trim().uppercase(),
    points = points
)

fun StrategyDto.toDomain() = Strategy(
    id = id,
    title = title,
    description = description,
    date = date.orEmpty(),
    category = category.orEmpty(),
    campaign = campaign.orEmpty(),
    active = active
)

fun StrategyHistoryDto.toDomain() = StrategyHistoryEntry(
    id = id,
    strategyId = strategyId,
    title = title,
    description = description,
    action = action,
    changedAt = changedAt.orEmpty(),
    active = active
)

fun AiAnalysisDto.toDomain() = AiAnalysis(
    score = score,
    impactScore = impactScore,
    feasibilityScore = feasibilityScore,
    innovationScore = innovationScore,
    strategicAlignmentScore = strategicAlignmentScore,
    recommendation = recommendation,
    summary = summary
)

fun IdeaDto.toDomain() = Idea(
    id = id,
    title = title,
    problem = problem,
    solution = solution,
    area = area.orEmpty(),
    benefit = benefit.orEmpty(),
    status = status,
    priority = priority,
    operatorId = operatorId,
    operatorName = operatorName,
    strategyId = strategyId.orEmpty(),
    strategyTitle = strategyTitle.orEmpty(),
    convertedToProject = convertedToProject,
    aiAnalysis = aiAnalysis?.toDomain()
)

fun ProjectDto.toDomain() = Project(
    id = id,
    ideaId = ideaId.orEmpty(),
    strategyId = strategyId.orEmpty(),
    strategyTitle = strategyTitle.orEmpty(),
    name = name,
    description = description.orEmpty(),
    responsible = responsible.orEmpty(),
    status = status,
    currentStage = currentStage.orEmpty(),
    investment = investment,
    financialReturn = financialReturn,
    costReduction = costReduction,
    productivityGain = productivityGain,
    roi = roi,
    deadline = deadline.orEmpty()
)

fun DashboardSummaryDto.toDomain() = DashboardSummary(
    totalProjects = totalProjects,
    activeProjects = activeProjects,
    completedProjects = completedProjects,
    plannedProjects = plannedProjects,
    cancelledProjects = cancelledProjects,
    totalInvestment = totalInvestment,
    totalFinancialReturn = totalFinancialReturn,
    profit = profit,
    roi = roi,
    totalCostReduction = totalCostReduction,
    averageProductivityGain = averageProductivityGain,
    totalIdeas = totalIdeas,
    approvedIdeas = approvedIdeas,
    ideasUnderAnalysis = ideasUnderAnalysis,
    totalStrategies = totalStrategies,
    activeStrategies = activeStrategies
)

fun RankingEntryDto.toDomain() = RankingEntry(
    position = position,
    userId = userId,
    name = name,
    points = points
)

fun MyRankingDto.toDomain() = MyRanking(
    position = position,
    totalOperators = totalOperators,
    name = name,
    points = points
)

// Modelo de dominio -> DTO enviado ao backend.

fun Strategy.toRequest() = StrategyRequestDto(
    title = title,
    description = description,
    date = date.ifBlank { null },
    category = category.ifBlank { null },
    campaign = campaign.ifBlank { null },
    active = active
)

fun Project.toRequest() = ProjectRequestDto(
    name = name,
    description = description.ifBlank { null },
    responsible = responsible.ifBlank { null },
    status = status,
    currentStage = currentStage.ifBlank { null },
    investment = investment,
    financialReturn = financialReturn,
    costReduction = costReduction,
    productivityGain = productivityGain,
    deadline = deadline.ifBlank { null },
    ideaId = ideaId.ifBlank { null },
    strategyId = strategyId.ifBlank { null }
)
