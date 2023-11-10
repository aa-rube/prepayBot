package app.bot.enviroment;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SuperAccurateCalculator {
    public int calculate(double amount, double rate) {
        BigDecimal result = BigDecimal.valueOf(amount / rate);
        result = result.multiply(BigDecimal.valueOf(1.03));
        result = result.setScale(0, RoundingMode.CEILING);
        return result.intValue();
    }
}