package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.Strategy;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface StrategyRepository extends MongoRepository<Strategy, String> {

    List<Strategy> findAllByOrderByCreatedAtDesc();

    List<Strategy> findByActiveOrderByCreatedAtDesc(boolean active);

    Optional<Strategy> findByTitleIgnoreCase(String title);
}
