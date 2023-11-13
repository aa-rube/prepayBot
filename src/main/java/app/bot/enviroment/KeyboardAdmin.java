package app.bot.enviroment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardAdmin {
    public synchronized InlineKeyboardMarkup mainAdminMenu() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();

        InlineKeyboardButton add = new InlineKeyboardButton();
        add.setText("Добавить карту");
        add.setCallbackData("addCard");
        firstRow.add(add);

        InlineKeyboardButton list = new InlineKeyboardButton();
        list.setText("Список карт");
        list.setCallbackData("cardList");
        firstRow.add(list);


        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public InlineKeyboardMarkup getBackMain() {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText("◀Назад");
        back.setCallbackData("backAdminMain");
        firstRow.add(back);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }

    public ReplyKeyboard approveOrNot(Long userChatId) {
        InlineKeyboardMarkup inLineKeyBoard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardMatrix = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton ok = new InlineKeyboardButton();
        ok.setText("ПОДТВЕРДИТЬ");
        ok.setCallbackData("ok_" + userChatId);
        firstRow.add(ok);

        InlineKeyboardButton no = new InlineKeyboardButton();
        no.setText("ОТКЛОНИТЬ");
        no.setCallbackData("no_" + userChatId);
        firstRow.add(no);

        keyboardMatrix.add(firstRow);
        inLineKeyBoard.setKeyboard(keyboardMatrix);
        return inLineKeyBoard;
    }
}
