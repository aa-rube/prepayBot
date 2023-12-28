package app.bot.repository;

import app.bot.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardDataRepository extends JpaRepository<Card, Integer> {
}

