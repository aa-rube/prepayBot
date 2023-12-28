package app.bot.repository;

import app.bot.model.Card;
import app.bot.model.ReceiptData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptDataRepository  extends JpaRepository<ReceiptData, Integer> {
}
