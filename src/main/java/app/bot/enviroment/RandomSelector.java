package app.bot.enviroment;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class RandomSelector {
    public static String getRandomString(List<String> list) {
        int size = list.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return list.get(0);
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(size);
        return list.get(randomIndex);
    }
}