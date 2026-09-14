package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.Idea;
import br.com.fiap.inovagab.backend.model.IdeaStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeaRepository extends MongoRepository<Idea, String> {

    List<Idea> findAllByOrderByCreatedAtDesc();

    List<Idea> findByOperatorIdOrderByCreatedAtDesc(String operatorId);

    List<Idea> findByStatusOrderByCreatedAtDesc(IdeaStatus status);

    List<Idea> findByStrategyId(String strategyId);

    long countByStrategyId(String strategyId);

    long countByStatus(IdeaStatus status);
}
