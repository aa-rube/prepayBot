package app.bot.enviroment;
import app.bot.model.Project;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class CreateButtonsData {
    private static final Map<String, Project> buttons = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, Project> getAllButtonsData() {

        Project project = new Project();
        project.setButton("Оплата бронирования");
        project.setStringReceipt("Booking Apartment");
        buttons.put("first", project);

        Project project1 = new Project();
        project1.setButton("Оплата трансфера");
        project1.setStringReceipt("Payment for transfer");
        buttons.put("second", project1);

        Project project2 = new Project();
        project2.setButton("Оплата экскурсии");
        project2.setStringReceipt("Payment for excursion");
        buttons.put("third", project2);

//        Project project2 = new Project();
//        project2.setButton();
//        buttons.put("theThird", "Третий проект");
        return buttons;
    }
}
