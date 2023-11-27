package app.bot.enviroment;
import app.bot.model.Project;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
public class CreateButtonsData {
    private static final LinkedHashMap<String, Project> buttons = new LinkedHashMap<>();
    public synchronized static LinkedHashMap<String, Project> getAllButtonsData() {

        Project project = new Project();
        project.setButton("Бронирование аппартаментов");
        project.setStringReceipt("Booking Apartment");
        buttons.put("first", project);

        Project project1 = new Project();
        project1.setButton("Оплата экскурсий");
        project1.setStringReceipt("Payment for excursion");
        buttons.put("third", project1);

        Project project2 = new Project();
        project2.setButton("Оплата трансфера");
        project2.setStringReceipt("Payment for transfer");
        buttons.put("second", project2);

        return buttons;
    }
}
