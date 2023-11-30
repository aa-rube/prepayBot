package app.bot.enviroment;

import app.bot.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CreateAdminMessage {
    @Autowired
    KeyboardAdmin keyboard;
    private final StringBuffer buffer = new StringBuffer();

    private SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);

        return msg;
    }

    public SendMessage getStartMessage(Long chatId) {
        buffer.setLength(0);
        buffer.append("Бот предоплты приветствует вас. Ниже кнопки меню для управления");

        return getSendMessage(chatId, buffer.toString(), keyboard.mainAdminMenu());
    }

    public SendMessage addNewCard(Long chatId) {
        buffer.setLength(0);
        buffer.append("Введите новый номер карты(16 знаков без пробелов) Номера могут быть не уникальными.");

        return getSendMessage(chatId, buffer.toString(), keyboard.getBackMain());
    }

    public SendMessage inputCardHolderName(Long chatId, String text) {
        buffer.setLength(0);
        buffer.append("Введите имя держателя карты в формате Иван И.\nКарта ")
                .append("<code>").append(text).append("</code>");

        return getSendMessage(chatId, buffer.toString(), keyboard.getBackMain());
    }

    public SendMessage cardSaved(Long chatId, Card card) {
        buffer.setLength(0);
        buffer.append("Карта <code>").append(card.getCardNumber()).append("</code>, ").append(card.getName())
                .append("\nДанные сохранены. Можно добавить еще.");

        return getSendMessage(chatId, buffer.toString(), keyboard.getBackMain());
    }

    private String getCutString(String input) {
        Pattern pattern = Pattern.compile("\\d{16}");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String cardNumber = matcher.group();

            return input.substring(0, input.indexOf(cardNumber) + cardNumber.length());
        }

        return "";
    }

    public synchronized SendPhoto getPhotoMessage(Long adminChatId, Long userChatId, String photoId, String textToAdmin) {
        buffer.setLength(0);
        buffer.append("Данные клиента: \n").append(getCutString(textToAdmin)).append("</code>\n\n")
                .append("Для формирования инвойс проверьте поступление денег на указанную карту.\nПосле чего подтвердите платеж");

        SendPhoto msg = new SendPhoto();
        msg.setChatId(adminChatId);
        msg.setPhoto(new InputFile(photoId));
        msg.setCaption(buffer.toString());
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyMarkup(null);

        return msg;
    }

    public SendDocument getDocumentMessage(Long adminChatId, Long userChatId, String docId, String textToAdmin) {
        buffer.setLength(0);
        buffer.append("Данные клиента: \n").append(getCutString(textToAdmin)).append("</code>\n\n")
                .append("Для формирования чека проверьте поступление денег на указанную карту.\nПосле чего подтвердите платеж");

        SendDocument msg = new SendDocument();
        msg.setChatId(adminChatId);
        msg.setDocument(new InputFile(docId));
        msg.setCaption(buffer.toString());
        msg.setParseMode(ParseMode.HTML);
        msg.setReplyMarkup(null);

        return msg;
    }

    public SendMessage getListOfCard(Long chatId, List<Card> cards) {
        buffer.setLength(0);
        buffer.append("Список  добавленных карт: \n");
        for (Card card : cards) {
            buffer.append("<code>").append(card.getCardNumber()).append("</code>,").append(card.getName()).append("\n")
                    .append("/_").append(card.getId()).append("_deleteCard\n\n");
        }

        return getSendMessage(chatId, buffer.toString(), keyboard.getBackMain());
    }

    public SendMessage cancelPay(Long chatId, String username) {
        buffer.setLength(0);
        buffer.append("Платеж отменен. Контакт пользователя: @").append(username);

        return getSendMessage(chatId, buffer.toString(), null);
    }

    public SendDocument approvePay(long adminChatId, File pdf) {
        SendDocument docMsg = new SendDocument();
        docMsg.setChatId(adminChatId);
        docMsg.setDocument(new InputFile(pdf));
        docMsg.setCaption("Документ готов и передан плательщику.\nВы можете его сохранить и распечатать.");

        return docMsg;
    }

    public SendMessage getExceptionMessage(Long chatId) {
        buffer.setLength(0);
        buffer.append("Произошла ошибка сохранения. Нажмите /start и попробуйте еще раз.\nРазработчик: t.me/i_amallears");

        return getSendMessage(chatId, buffer.toString(), null);
    }

    public InlineKeyboardMarkup getKeyboardApproveOrNot(int integer) {
        return keyboard.approveOrNot(integer);
    }
}
