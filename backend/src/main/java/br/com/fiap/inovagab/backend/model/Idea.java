package br.com.fiap.inovagab.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "ideas")
public class Idea {

    @Id
    private String id;

    private String title;
    private String problem;
    private String solution;
    private String area;
    private String benefit;

    private IdeaStatus status = IdeaStatus.EM_ANALISE;
    private IdeaPriority priority = IdeaPriority.NORMAL;

    /** Sempre derivado do JWT: o app nunca escolhe o dono da ideia. */
    @Indexed
    private String operatorId;
    private String operatorName;

    /** Orientacao estrategica vigente a qual a ideia esta vinculada (opcional). */
    private String strategyId;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant approvedAt;

    private boolean convertedToProject;

    private AiAnalysis aiAnalysis;

    /**
     * Marcadores de idempotencia da pontuacao: garantem que repetir a mesma
     * operacao (aprovar duas vezes, por exemplo) nao credite pontos de novo.
     */
    private boolean creationPointsAwarded;
    private boolean approvalPointsAwarded;
    private boolean conversionPointsAwarded;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public IdeaStatus getStatus() {
        return status;
    }

    public void setStatus(IdeaStatus status) {
        this.status = status;
    }

    public IdeaPriority getPriority() {
        return priority;
    }

    public void setPriority(IdeaPriority priority) {
        this.priority = priority;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public boolean isConvertedToProject() {
        return convertedToProject;
    }

    public void setConvertedToProject(boolean convertedToProject) {
        this.convertedToProject = convertedToProject;
    }

    public AiAnalysis getAiAnalysis() {
        return aiAnalysis;
    }

    public void setAiAnalysis(AiAnalysis aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }

    public boolean isCreationPointsAwarded() {
        return creationPointsAwarded;
    }

    public void setCreationPointsAwarded(boolean creationPointsAwarded) {
        this.creationPointsAwarded = creationPointsAwarded;
    }

    public boolean isApprovalPointsAwarded() {
        return approvalPointsAwarded;
    }

    public void setApprovalPointsAwarded(boolean approvalPointsAwarded) {
        this.approvalPointsAwarded = approvalPointsAwarded;
    }

    public boolean isConversionPointsAwarded() {
        return conversionPointsAwarded;
    }

    public void setConversionPointsAwarded(boolean conversionPointsAwarded) {
        this.conversionPointsAwarded = conversionPointsAwarded;
    }
}
