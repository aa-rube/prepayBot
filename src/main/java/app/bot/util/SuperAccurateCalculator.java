package app.bot.util;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SuperAccurateCalculator {
    public static int calculate(double amount, double rubToUsdt, double usdtTOThb, int userPercent) {
        BigDecimal usdt = BigDecimal.valueOf(amount / usdtTOThb);
        BigDecimal rub = BigDecimal.valueOf(usdt.doubleValue()/rubToUsdt);

        BigDecimal percent =  new BigDecimal(userPercent).divide(new BigDecimal(100));

        rub = rub.multiply(percent);
        rub = rub.setScale(0, RoundingMode.CEILING);
        return rub.intValue();
    }
}