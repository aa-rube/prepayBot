package app.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Getter
@Setter
public class Rate {
    private LocalDateTime lastUpdate;
    private Double rubToUSDT;
    private Double usdtToTHB;
}
