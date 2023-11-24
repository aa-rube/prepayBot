package app.bot.enviroment;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
@Service
public class ButtonsData {
    private static final Map<String, String> buttons = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, String> getAllButtonsData() {
        buttons.put("theFirst", "Первый проект");
        buttons.put("theSecond", "Второй проект");
        buttons.put("theThird", "Третий проект");
        return buttons;
    }
}
