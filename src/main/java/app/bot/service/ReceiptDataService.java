package app.bot.service;

import app.bot.model.ReceiptData;
import app.bot.repository.ReceiptDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiptDataService {
    @Autowired
    private ReceiptDataRepository repository;

    public void save(ReceiptData data) {
        repository.save(data);
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    public List<ReceiptData> findAll() {
        return repository.findAll();
    }
}
