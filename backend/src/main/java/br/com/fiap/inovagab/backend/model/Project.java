package br.com.fiap.inovagab.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    /** Ideia aprovada que originou o projeto (opcional). */
    @Indexed
    private String ideaId;

    /** Orientacao estrategica vigente a qual o projeto esta vinculado (opcional). */
    @Indexed
    private String strategyId;

    private String name;
    private String description;
    private String responsible;

    private ProjectStatus status = ProjectStatus.PLANEJADO;
    private String currentStage;

    private double investment;
    private double financialReturn;
    private double costReduction;
    private double productivityGain;

    private String deadline;

    private Instant createdAt;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public double getInvestment() {
        return investment;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    public double getFinancialReturn() {
        return financialReturn;
    }

    public void setFinancialReturn(double financialReturn) {
        this.financialReturn = financialReturn;
    }

    public double getCostReduction() {
        return costReduction;
    }

    public void setCostReduction(double costReduction) {
        this.costReduction = costReduction;
    }

    public double getProductivityGain() {
        return productivityGain;
    }

    public void setProductivityGain(double productivityGain) {
        this.productivityGain = productivityGain;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
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
}
