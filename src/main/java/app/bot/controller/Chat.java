package app.bot.controller;

import app.bot.api.CryptoAPI;
import app.bot.enviroment.*;
import app.bot.config.BotConfig;
import app.bot.model.Card;
import app.bot.model.Rate;
import app.bot.pdf.PdfEditor;
import app.bot.service.CardService;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class Chat extends TelegramLongPollingBot {
    @Autowired
    private BotConfig botConfig;
    @Autowired
    private CreateMessage createMessage;
    @Autowired
    private CreateAdminMessage adminMessage;
    @Autowired
    private CryptoAPI compareAPI;
    @Autowired
    private SuperAccurateCalculator calculator;
    @Autowired
    private final Rate rate = new Rate();
    @Autowired
    private Transliterator transliterator;
    @Autowired
    private CardService cardService;
    private final Map<Long, Integer> chatIdMsgId = Collections.synchronizedMap(new HashMap<>());
    private final Set<Long> startEnterName = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, String> fullName = Collections.synchronizedMap(new HashMap<>());
    private final Set<Long> startEnterSum = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, Integer> sumInRub = Collections.synchronizedMap(new HashMap<>());
    private List<Card> cards = new ArrayList<>();
    private final Set<Long> startEnterCardData = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, Card> cardData = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, String> textToAdmin = Collections.synchronizedMap(new HashMap<>());
    private final Set<Long> waitForPayScreenShot = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, LocalDateTime> chattingWithAdmin = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, String> userName = Collections.synchronizedMap(new HashMap<>());
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @PostConstruct
    private void init() {
        updateCurrencyRate();
        updateCardList();
    }
    private void updateCurrencyRate() {
        rate.setRubToUSDT(compareAPI.getPrice("RUB", "USDT"));
        rate.setUsdtToTHB(compareAPI.getPrice("USDT", "THB"));

        rate.setLastUpdate(LocalDateTime.now());
    }
    private void updateCardList() {
        cards = cardService.findAllCards();
    }

    @Scheduled(fixedRate = 10000)
    public void closeSupportChat() {
        List<Long> idList = new ArrayList<>();
        for (Map.Entry<Long, LocalDateTime> data : chattingWithAdmin.entrySet()) {
            Long chatId = data.getKey();
            LocalDateTime plusTime = data.getValue().plusMinutes(10);
            LocalDateTime now = LocalDateTime.now();

            if (now.isAfter(plusTime)) {
                idList.add(chatId);
            }
        }

        for (Long chatId : idList) {
            executeMsg(createMessage.stopSupportChat(chatId));
            chattingWithAdmin.remove(chatId);
        }

        idList.clear();
    }

    @Override
    public void onUpdateReceived(Update update) {
        Thread thread = new Thread(() -> {

            if (update.hasMessage() && update.getMessage().isReply()) {
                replayHandle(update);
                return;
            }

            if (update.getMessage() != null) {
                Message message = update.getMessage();
                Long chatId = message.getChatId();

                if (message.isCommand()) {
                    commandHandle(update, chatId, message.getText());
                    return;
                }

                if (message.hasText()) {
                    textHandler(update, chatId, message.getText());
                }

                if (message.hasDocument() || message.hasPhoto()) {
                    screenShotHandler(message, chatId);
                }
            }

            if (update.hasCallbackQuery()) {
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                callBackDataHandler(update, chatId, update.getCallbackQuery().getData());
            }
        });
        thread.start();
    }

    private synchronized void replayHandle(Update update) {
        Long replyToMessageForwardFromChatId = update.getMessage().getReplyToMessage().getForwardFrom().getId();
        executeMsg(createMessage.getSupportMessage(replyToMessageForwardFromChatId, update.getMessage().getText()));
    }

    private void screenShotHandler(Message message, Long chatId) {
        Long adminsChat = botConfig.getAdminsChat();
        try {
            if (message.hasPhoto() && waitForPayScreenShot.contains(chatId)) {
                waitForPayScreenShot.remove(chatId);
                String photoId = message.getPhoto().get(0).getFileId();
                chatIdMsgId.put(adminsChat,
                        execute(adminMessage.getPhotoMessage(adminsChat,
                                chatId, photoId, textToAdmin.get(chatId))).getMessageId());
                return;
            }

            if (message.hasDocument() && waitForPayScreenShot.contains(chatId)) {
                waitForPayScreenShot.remove(chatId);
                String docId = message.getDocument().getFileId();
                chatIdMsgId.put(adminsChat,
                        execute(adminMessage.getDocumentMessage(adminsChat,
                                chatId, docId, textToAdmin.get(chatId))).getMessageId());
                return;

            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void commandHandle(Update update, Long chatId, String command) {
        Long adminsChat = botConfig.getAdminsChat();
        if (command.equals("/start")) {
            if (chatId.equals(adminsChat)) {
                executeMsg(adminMessage.getStartMessage(adminsChat));
                init();
                cardData.remove(chatId);
                return;
            }
            startEnterName.add(chatId);
            userName.put(chatId, update.getMessage().getFrom().getUserName());
            executeMsg(createMessage.getStartMessage(chatId));
            return;
        }

        if (command.contains("_deleteCard@bereza_property_prepay_bot") && chatId.equals(adminsChat)) {
            int id = Integer.parseInt(command.split("_")[1]);
            if (cardService.deleteById(id)) {
                init();
                executeMsg(adminMessage.getListOfCard(adminsChat, cardService.findAllCards()));
            } else {
                executeMsg(adminMessage.getListOfCard(adminsChat, cardService.findAllCards()));
                try {
                    execute(createMessage.wrongInput(chatId));
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }

        if (command.equals("/customercentre")) {
            chattingWithAdmin.put(chatId, LocalDateTime.now());
            executeMsg(createMessage.startSupport(chatId));
        }
    }

    private synchronized void textHandler(Update update, Long chatId, String text) {

        if (chattingWithAdmin.containsKey(chatId)) {
            deleteKeyboard(chatId);
            chattingWithAdmin.put(chatId, LocalDateTime.now());
            forwardMessage(update.getMessage());
            return;
        }

        if (startEnterName.contains(chatId)) {
            if (text.split(" ").length > 1) {
                String transitFullName = transliterator.transliterate(text.trim().toUpperCase());
                fullName.put(chatId, transitFullName);

                executeMsg(createMessage.allRightNext(chatId, transitFullName));
            } else {
                executeMsg(createMessage.wrongInput(chatId));
            }
            return;
        }

        if (startEnterSum.contains(chatId)) {
            try {
                double thb = Double.parseDouble(text);

                LocalDateTime plusTime = rate.getLastUpdate().plusMinutes(3);
                LocalDateTime now = LocalDateTime.now();

                if (now.isAfter(plusTime)) {
                    updateCurrencyRate();
                }

                int rub = calculator.calculate(thb, rate.getRubToUSDT(), rate.getUsdtToTHB());
                sumInRub.put(chatId, rub);
                executeMsg(createMessage.checkTheRubSum(chatId, rub));
            } catch (Exception e) {
                executeMsg(createMessage.wrongInput(chatId));
            }
            return;
        }

        if (startEnterCardData.contains(chatId)) {
            if (text.trim().length() == 16) {
                startEnterCardData.remove(chatId);
                Card card = new Card();
                card.setCardNumber(text.trim());
                cardData.put(chatId, card);
                executeMsg(adminMessage.inputCardHolderName(chatId, text));
            } else {
                executeMsg(createMessage.wrongInput(chatId));
            }
            return;
        }

        if (cardData.containsKey(chatId)) {

            cardData.get(chatId).setName(text.trim());
            if (cardService.save(cardData.get(chatId))) {
                executeMsg(adminMessage.cardSaved(chatId, cardData.get(chatId)));

                cardData.remove(chatId);
                updateCardList();
            } else {
                executeMsg(adminMessage.getExceptionMessage(chatId));
            }

        }
    }

    private void callBackDataHandler(Update update, Long chatId, String data) {
        try {
            deleteKeyboard(chatId);
        } catch (Exception e) {
        }

        if (data.equals("next")) {
            startEnterName.remove(chatId);
            startEnterSum.add(chatId);
            executeMsg(createMessage.inputYourSum(chatId));
            return;
        }

        if (data.equals("next1")) {
            startEnterSum.remove(chatId);
            SendMessage msg = createMessage.getRandomCard(chatId, sumInRub, fullName, cards);
            textToAdmin.put(chatId, msg.getText());
            executeMsg(msg);
            return;
        }

        if (data.equals("next2")) {
            waitForPayScreenShot.add(chatId);
            executeMsg(createMessage.waitForScreenShot(chatId));
            return;
        }

        if (data.equals("cancel")) {
            startEnterSum.remove(chatId);
            executeMsg(createMessage.getStartMessage(chatId));
            startEnterName.add(chatId);
            return;
        }

        if (data.equals("addCard")) {
            startEnterCardData.add(chatId);
            executeMsg(adminMessage.addNewCard(chatId));
            return;
        }

        if (data.equals("cardList")) {
            executeMsg(adminMessage.getListOfCard(chatId, cardService.findAllCards()));
            return;
        }

        if (data.equals("backAdminMain")) {
            cardData.clear();
            executeMsg(adminMessage.getStartMessage(chatId));
            return;
        }

        if (data.contains("ok_") && chatId.equals(botConfig.getAdminsChat())) {
            Long chatUserId = Long.valueOf(data.split("_")[1]);
            userName.remove(chatUserId);
            File pdf = PdfEditor.addTextToPdf(fullName.get(chatUserId), sumInRub.get(chatUserId).toString());
            if (pdf != null) {

                try {
                    execute(createMessage.approvePay(chatUserId, pdf));//user
                    execute(adminMessage.approvePay(botConfig.getAdminsChat(), pdf));//admin
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            } else {
                executeMsg(createMessage.fileDidNotSendToUser(chatUserId));
            }
            return;
        }

        if (data.contains("no_") && chatId.equals(botConfig.getAdminsChat())) {
            Long chatUserId = Long.valueOf(data.split("_")[1]);

            executeMsg(createMessage.cancelPay(chatUserId));
            executeMsg(adminMessage.cancelPay(botConfig.getAdminsChat(), userName.get(chatUserId)));
        }

        if (data.equals("closeChat")) {
            chattingWithAdmin.remove(chatId);
            executeMsg(createMessage.stopSupportChat(chatId));
        }

        if (data.equals("payAgain")) {
            startEnterSum.remove(chatId);
            sumInRub.remove(chatId);
            startEnterName.add(chatId);
            userName.put(chatId, update.getCallbackQuery().getMessage().getFrom().getUserName());
            executeMsg(createMessage.getStartMessage(chatId));
        }
    }

    private synchronized void forwardMessage(Message messageContent) {
        ForwardMessage forwardMessage = new ForwardMessage();

        forwardMessage.setChatId(botConfig.getSupportChat());
        forwardMessage.setFromChatId(messageContent.getChatId().toString());
        forwardMessage.setMessageId(messageContent.getMessageId());
        try {
            chatIdMsgId.put(Long.valueOf(messageContent.getChatId().toString()), execute(forwardMessage).getMessageId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void executeMsg(SendMessage msg) {
        try {
            if (msg.getReplyMarkup() != null) {
                chatIdMsgId.put(Long.valueOf(msg.getChatId()), execute(msg).getMessageId());
            } else {
                execute(msg);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteKeyboard(Long chatId) {
        EditMessageReplyMarkup e = new EditMessageReplyMarkup();
        e.setChatId(chatId);
        e.setMessageId(chatIdMsgId.get(chatId));
        e.setReplyMarkup(null);
        try {
            execute(e);
        } catch (TelegramApiException ex) {
            throw new RuntimeException(ex);
        }
    }
}