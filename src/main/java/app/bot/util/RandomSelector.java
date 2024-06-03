package app.bot.util;

import app.bot.model.Card;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class RandomSelector {
    public static Card getRandomCard(List<Card> list) {
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