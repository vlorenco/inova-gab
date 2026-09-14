package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findAllByOrderByCreatedAtDesc();

    List<Project> findByStrategyId(String strategyId);

    Optional<Project> findByIdeaId(String ideaId);

    boolean existsByIdeaId(String ideaId);
}
