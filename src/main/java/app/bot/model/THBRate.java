package app.bot.model;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class THBRate {
    private LocalDateTime lastUpdate;
    private Double thbRate;

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Double getThbRate() {
        return thbRate;
    }

    public void setThbRate(Double thbRate) {
        this.thbRate = thbRate;
    }
}
