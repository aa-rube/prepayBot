package app.bot.enviroment;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SuperAccurateCalculator {
    public int calculate(double amount, double rubToUsdt, double usdtTOThb) {
        BigDecimal usdt = BigDecimal.valueOf(amount / usdtTOThb);
        BigDecimal rub = BigDecimal.valueOf(usdt.doubleValue()/rubToUsdt);

        rub = rub.multiply(BigDecimal.valueOf(1.04));
        rub = rub.setScale(0, RoundingMode.CEILING);
        return rub.intValue();
    }
}