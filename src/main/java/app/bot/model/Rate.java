package app.bot.model;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class Rate {
    private LocalDateTime lastUpdate;
    private Double rubToUSDT;
    private Double usdtToTHB;
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public Double getRubToUSDT() {
        return rubToUSDT;
    }
    public void setRubToUSDT(Double rubToUSDT) {
        this.rubToUSDT = rubToUSDT;
    }
    public Double getUsdtToTHB() {
        return usdtToTHB;
    }
    public void setUsdtToTHB(Double usdtToTHB) {
        this.usdtToTHB = usdtToTHB;
    }
}
