package app.bot.enviroment;
import app.bot.model.Project;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class ButtonsData {
    private static final Map<String, Project> buttons = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, Project> getAllButtonsData() {

        Project project = new Project();
        project.setButton("Аренда номера");
        project.setStringReceipt("Booking Apartmen");
        buttons.put("first", project);

        Project project1 = new Project();
        project1.setButton("Аренда самокатов");
        project1.setStringReceipt("Scooter Rental");
        buttons.put("second", project1);

//        Project project2 = new Project();
//        project2.setButton();
//        buttons.put("theThird", "Третий проект");
        return buttons;
    }
}
