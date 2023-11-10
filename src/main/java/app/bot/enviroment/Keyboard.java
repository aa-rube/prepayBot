package app.bot.enviroment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class Keyboard {

    public synchronized InlineKeyboardMarkup fullNameIsOk() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton stpwrd = new InlineKeyboardButton();
        stpwrd.setText("Все верно. Далее.");
        stpwrd.setCallbackData("next");
        firstRow.add(stpwrd);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public synchronized InlineKeyboardMarkup sumInRublesIsOk() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton stpwrd = new InlineKeyboardButton();
        stpwrd.setText("Все верно. Далее.");
        stpwrd.setCallbackData("next1");
        firstRow.add(stpwrd);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup iPaidForThis() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton stpwrd = new InlineKeyboardButton();
        stpwrd.setText("Оплачено");
        stpwrd.setCallbackData("next2");
        firstRow.add(stpwrd);

        InlineKeyboardButton cancel = new InlineKeyboardButton();
        cancel.setText("Отмена");
        cancel.setCallbackData("cancel");
        firstRow.add(cancel);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
