package app.bot.enviroment;

import app.bot.model.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CreateMessage {
    @Autowired
    Keyboard keyboard;
    private final StringBuffer buffer = new StringBuffer();

    private synchronized SendMessage getSendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(text);
        msg.setReplyMarkup(markup);
        msg.enableHtml(true);
        msg.setParseMode(ParseMode.HTML);
        return msg;
    }

    public SendMessage getStartMessage(Long chatId, Map<String, String> buttons) {
        buffer.setLength(0);
        buffer.append("Приветствую! \nВыберите проект, по которому желаете внести предоплату");

        return getSendMessage(chatId, buffer.toString(), keyboard.chooseThePayOption(buttons));
    }
    public SendMessage startEnterFullName(Long chatId) {
        buffer.setLength(0);
        buffer.append("Введите пожалуйста ваши ФИО:");

        return getSendMessage(chatId, buffer.toString(), null);
    }


    public SendMessage allRightNext(Long chatId, String fullName) {
        buffer.setLength(0);
        buffer.append("Если заметили ошибку то введите ФИО еще раз.\nПроверьте данные:\n")
                .append(fullName);

        return getSendMessage(chatId, buffer.toString(), keyboard.fullNameIsOk());
    }

    public SendMessage wrongInput(Long chatId) {
        buffer.setLength(0);
        buffer.append("Вы ввели что-то другое. Попробуйте снова");

        return getSendMessage(chatId, buffer.toString(), null);
    }

    public SendMessage inputYourSum(Long chatId) {
        buffer.setLength(0);
        buffer.append("Введите сумму в тайских батах (THB):");

        return getSendMessage(chatId, buffer.toString(), null);
    }

    public SendMessage checkTheRubSum(Long chatId, int rub) {
        buffer.setLength(0);
        buffer.append("Если заметили ошибку, то введите сумму в батах (THB) еще раз.\nПроверьте сумму в рублях:\n")
                .append(rub).append(".00RUB");
        return getSendMessage(chatId, buffer.toString(), keyboard.sumInRublesIsOk());
    }

    public SendMessage getRandomCard(Long chatId, int sumInRub, String fullName, List<Card> cards) {
        buffer.setLength(0);
        Card card = RandomSelector.getRandomCard(cards);

        if (card != null) {
            buffer.append("ФИО: ").append(fullName).append("\n")
                    .append("Сумма: ").append(sumInRub).append(".00RUB\n\n")
                    .append("Данные для перевода: \n")
                    .append("Получатель: ").append(card.getName()).append("\n")
                    .append("Карта <code>").append(card.getCardNumber()).append("</code>\n\n")
                    .append("После перевода нажмите кнопку и отправьте скриншот об оплате в этот чат.");
            return getSendMessage(chatId, buffer.toString(), keyboard.iPaidForThis());
        } else {
            buffer.append("Вот это да.. Администратор еще ни одной карты не добавил. Напишите в нашу службу заботы: \n/customercentre");
            return getSendMessage(chatId, buffer.toString(), keyboard.getAnotherTryToPay());
        }
    }

    public SendMessage waitForScreenShot(Long chatId) {
        buffer.setLength(0);
        buffer.append("Отправьте скриншот платежа, что бы мы смогли проверить ваши данные и подтвердить оплату");
        return getSendMessage(chatId, buffer.toString(), null);
    }

    public SendMessage cancelPay(Long chatId) {
        buffer.setLength(0);
        buffer.append("Платеж отменен.\nДля связи со службой заботы нажмите /customercentre");
        return getSendMessage(chatId, buffer.toString(), keyboard.getAnotherTryToPay());
    }

    public SendMessage startSupport(Long chatId) {
        buffer.setLength(0);
        buffer.append("Можно задать Ваш вопрос прямо в чате с ботом.\n\nНаш менеджер ответит Вам в самое ближайшее время");
        return getSendMessage(chatId, buffer.toString(), keyboard.stopSupportChat());
    }

    public SendMessage getSupportMessage(Long replyToMessageForwardFromChatId, String text) {
        return getSendMessage(replyToMessageForwardFromChatId, text, keyboard.stopSupportChat());
    }

    public SendMessage stopSupportChat(Long chatId) {
        return getSendMessage(chatId, "Чат с поддержкой завершен.", keyboard.getAnotherTryToPay());
    }

    public SendDocument approvePay(Long chatUserId, File pdf) {
        SendDocument docMsg = new SendDocument();
        docMsg.setChatId(chatUserId);
        docMsg.setDocument(new InputFile(pdf));
        docMsg.setCaption("Документ готов.\nВы можете его сохранить и распечатать." +
                "\n\nПо всем вопросам можете обращаться в нашу службу заботы /customercentre");
        docMsg.setReplyMarkup(keyboard.getAnotherTryToPay());
        return docMsg;
    }

    public SendMessage fileDidNotSendToUser(Long chatUserId) {
        buffer.setLength(0);
        buffer.append("Ваш платеж принят, но видмо произошла ошибка формирования чека.")
                .append("\nНапишите в нашу службу заботы за доподнительной информацией /customercentre");
        return getSendMessage(chatUserId, buffer.toString(), keyboard.getAnotherTryToPay());
    }
}
