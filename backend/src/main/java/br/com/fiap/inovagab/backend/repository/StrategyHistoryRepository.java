package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.StrategyHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StrategyHistoryRepository extends MongoRepository<StrategyHistory, String> {

    List<StrategyHistory> findByStrategyIdOrderByChangedAtDesc(String strategyId);
}
