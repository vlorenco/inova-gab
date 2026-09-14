package br.com.fiap.inovagab.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Snapshot imutavel de uma orientacao estrategica a cada mudanca relevante.
 * E o que permite a lideranca consultar o historico exigido pela atividade.
 */
@Document(collection = "strategy_history")
public class StrategyHistory {

    @Id
    private String id;

    @Indexed
    private String strategyId;

    private String title;
    private String description;
    private String date;
    private String category;
    private String campaign;
    private boolean active;

    private StrategyAction action;
    private Instant changedAt;
    private String changedBy;

    public static StrategyHistory snapshot(Strategy strategy, StrategyAction action, String changedBy) {
        StrategyHistory history = new StrategyHistory();
        history.strategyId = strategy.getId();
        history.title = strategy.getTitle();
        history.description = strategy.getDescription();
        history.date = strategy.getDate();
        history.category = strategy.getCategory();
        history.campaign = strategy.getCampaign();
        history.active = strategy.isActive();
        history.action = action;
        history.changedAt = Instant.now();
        history.changedBy = changedBy;
        return history;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public StrategyAction getAction() {
        return action;
    }

    public void setAction(StrategyAction action) {
        this.action = action;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}
