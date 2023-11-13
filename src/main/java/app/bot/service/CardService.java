package app.bot.service;

import app.bot.model.Card;
import app.bot.repository.CardDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {
    @Autowired
   private CardDataRepository repository;
    public boolean save(Card card) {
        try {
            repository.save(card);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteById(int id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Card> findAllCards() {
        return  repository.findAll();
    }
}
