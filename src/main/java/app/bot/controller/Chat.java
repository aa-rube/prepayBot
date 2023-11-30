package app.bot.controller;

import app.bot.api.CryptoAPI;
import app.bot.enviroment.*;
import app.bot.config.BotConfig;
import app.bot.model.Card;
import app.bot.model.Project;
import app.bot.model.Rate;
import app.bot.model.ReceiptData;
import app.bot.pdf.PdfEditor;
import app.bot.service.CardService;
import app.bot.service.ReceiptDataService;
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
    private CardService cardService;
    @Autowired
    private final Rate rate = new Rate();
    @Autowired
    private ReceiptDataService receiptDataService;
    private final Map<Long, Integer> chatIdMsgId = Collections.synchronizedMap(new HashMap<>());
    private List<Card> cards = new ArrayList<>();
    private final Set<Long> startEnterCardData = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, Card> cardData = Collections.synchronizedMap(new HashMap<>());
    private final Set<Long> waitForPayScreenShot = Collections.synchronizedSet(new HashSet<>());
    private final Map<Long, LocalDateTime> chattingWithAdmin = Collections.synchronizedMap(new HashMap<>());
    private final LinkedHashMap<String, Project> buttons = CreateButtonsData.getAllButtonsData();
    private final Map<Long, ReceiptData> userData = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<Integer, ReceiptData> receiptDataMap = Collections.synchronizedMap(new HashMap<>());

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

        for (ReceiptData receiptData : receiptDataService.findAll()) {
            receiptDataMap.put(receiptData.getMsgId(), receiptData);
        }
    }

    private void updateCurrencyRate() {
        rate.setRubToUSDT(CryptoAPI.getPrice("RUB", "USDT"));
        rate.setUsdtToTHB(CryptoAPI.getPrice("USDT", "THB"));

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
        try {
            Long replyToMessageForwardFromChatId = update.getMessage().getReplyToMessage().getForwardFrom().getId();
            executeMsg(createMessage.getSupportMessage(replyToMessageForwardFromChatId, update.getMessage().getText()));
        } catch (Exception ignored) {
        }
    }

    private synchronized void screenShotHandler(Message message, Long chatId) {
        try {
            int msgId = 0;
            Long adminsChat = botConfig.getAdminsChat();

            if (message.hasPhoto() && waitForPayScreenShot.contains(chatId)) {
                waitForPayScreenShot.remove(chatId);
                String photoId = message.getPhoto().get(0).getFileId();
                msgId = execute(adminMessage.getPhotoMessage(adminsChat, chatId, photoId, userData.get(chatId).getTextToAdmin())).getMessageId();
                tempDataHandler(adminsChat, chatId, msgId);
            }

            if (message.hasDocument() && waitForPayScreenShot.contains(chatId)) {
                waitForPayScreenShot.remove(chatId);
                String docId = message.getDocument().getFileId();
                msgId = execute(adminMessage.getDocumentMessage(adminsChat, chatId, docId, userData.get(chatId).getTextToAdmin())).getMessageId();
                tempDataHandler(adminsChat, chatId, msgId);
            }

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void tempDataHandler(Long adminsChat, Long chatId, int msgId) throws TelegramApiException {
        chatIdMsgId.put(adminsChat, msgId);
        userData.get(chatId).setMsgId(msgId);

        receiptDataService.save(userData.get(chatId));
        receiptDataMap.put(msgId, userData.get(chatId));
        userData.remove(chatId);

        EditMessageReplyMarkup edit = new EditMessageReplyMarkup();
        edit.setMessageId(msgId);
        edit.setChatId(adminsChat);
        edit.setReplyMarkup(adminMessage.getKeyboardApproveOrNot(msgId));

        execute(edit);
    }

    private void commandHandle(Update update, Long chatId, String command) {
        Long adminsChat = botConfig.getAdminsChat();

        if (command.contains("/start")) {

            if (chatId.equals(adminsChat)) {
                executeMsg(adminMessage.getStartMessage(adminsChat));
                init();
                cardData.remove(chatId);
                return;
            }

            userData.remove(chatId);
            userData.put(chatId, new ReceiptData());
            userData.get(chatId).setChatId(chatId);
            userData.get(chatId).setUserName(update.getMessage().getFrom().getUserName());
            executeMsg(createMessage.getStartMessage(chatId, buttons));
            return;
        }

        if (command.contains("_deleteCard") && chatId.equals(adminsChat)) {
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
                startEnterCardData.add(chatId);
                updateCardList();
            } else {
                executeMsg(adminMessage.getExceptionMessage(chatId));
            }
            return;
        }

        if (chattingWithAdmin.containsKey(chatId)) {
            deleteKeyboard(chatId);
            chattingWithAdmin.put(chatId, LocalDateTime.now());
            forwardMessage(update.getMessage());
            return;
        }

        if (userData.get(chatId).isStartEnterName()) {
            if (text.split(" ").length > 1) {
                userData.get(chatId).setFullName(Transliterator.transliterate(text.trim().toUpperCase()));
                executeMsg(createMessage.allRightNext(chatId, userData.get(chatId).getFullName()));
            } else {
                executeMsg(createMessage.wrongInput(chatId));
            }
            return;
        }

        if (userData.get(chatId).isStartEnterSum()) {

            try {
                double bth = Double.parseDouble(text);
                userData.get(chatId).setSumInBth(bth);
                LocalDateTime plusTime = rate.getLastUpdate().plusMinutes(3);
                LocalDateTime now = LocalDateTime.now();

                if (now.isAfter(plusTime)) {
                    updateCurrencyRate();
                }

                int rub = SuperAccurateCalculator.calculate(bth, rate.getRubToUSDT(), rate.getUsdtToTHB());
                userData.get(chatId).setSumInRub(rub);
                executeMsg(createMessage.checkTheRubSum(chatId, rub));
            } catch (Exception e) {
                executeMsg(createMessage.wrongInput(chatId));
            }
            return;
        }

    }

    private void callBackDataHandler(Update update, Long chatId, String data) {

        if (data.contains("ok_") && chatId.equals(botConfig.getAdminsChat())) {
            int callBackMsgId = Integer.parseInt(data.split("_")[1]);

            File pdf = PdfEditor.addTextToPdf(receiptDataMap.get(callBackMsgId).getFullName(),
                    String.valueOf(receiptDataMap.get(callBackMsgId).getSumInBth()),
                    receiptDataMap.get(callBackMsgId).getStringReceipt());

            if (pdf != null) {
                try {
                    execute(createMessage.approvePay(receiptDataMap.get(callBackMsgId).getChatId(), pdf));//user
                    execute(adminMessage.approvePay(botConfig.getAdminsChat(), pdf));//admin
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else {
                executeMsg(createMessage.fileDidNotSendToUser(receiptDataMap.get(callBackMsgId).getChatId()));
            }

            receiptDataMap.remove(callBackMsgId);
            receiptDataService.delete(callBackMsgId);
            return;
        }

        if (data.contains("no_") && chatId.equals(botConfig.getAdminsChat())) {
            int callBackMsgId = Integer.parseInt(data.split("_")[1]);

            executeMsg(createMessage.cancelPay(receiptDataMap.get(callBackMsgId).getChatId()));
            executeMsg(adminMessage.cancelPay(botConfig.getAdminsChat(), receiptDataMap.get(callBackMsgId).getUserName()));
            receiptDataService.delete(callBackMsgId);
            return;
        }

        if (data.equals("next")) {
            deleteKeyboard(chatId);

            userData.get(chatId).setStartEnterName(false);
            userData.get(chatId).setStartEnterSum(true);
            executeMsg(createMessage.inputYourSum(chatId));
            return;
        }

        if (data.equals("next1")) {
            deleteKeyboard(chatId);

            userData.get(chatId).setStartEnterSum(false);
            SendMessage msg = createMessage.getRandomCard(chatId, userData.get(chatId), cards);
            userData.get(chatId).setTextToAdmin("");
            userData.get(chatId).setTextToAdmin(msg.getText());
            executeMsg(msg);
            return;
        }

        if (data.equals("next2")) {
            deleteKeyboard(chatId);

            waitForPayScreenShot.add(chatId);
            executeMsg(createMessage.waitForScreenShot(chatId));
            return;
        }

        if (data.equals("cancel")) {
            deleteKeyboard(chatId);

            userData.get(chatId).setStartEnterSum(false);
            executeMsg(createMessage.getStartMessage(chatId, buttons));
            return;
        }

        if (data.equals("addCard")) {
            deleteKeyboard(chatId);

            startEnterCardData.add(chatId);
            executeMsg(adminMessage.addNewCard(chatId));
            return;
        }

        if (data.equals("cardList")) {
            deleteKeyboard(chatId);

            executeMsg(adminMessage.getListOfCard(chatId, cardService.findAllCards()));
            return;
        }

        if (data.equals("backAdminMain")) {
            deleteKeyboard(chatId);

            cardData.remove(chatId);
            startEnterCardData.remove(chatId);
            executeMsg(adminMessage.getStartMessage(chatId));
            return;
        }


        if (data.equals("closeChat")) {
            chattingWithAdmin.remove(chatId);
            executeMsg(createMessage.stopSupportChat(chatId));
            return;
        }

        if (data.equals("payAgain")) {
            userData.put(chatId, new ReceiptData());
            userData.get(chatId).setUserName(update.getCallbackQuery().getMessage().getFrom().getUserName());
            executeMsg(createMessage.getStartMessage(chatId, buttons));
            return;
        }

        if (buttons.containsKey(data)) {
            userData.remove(chatId);

            userData.put(chatId, new ReceiptData());
            userData.get(chatId).setUserName(update.getCallbackQuery().getMessage().getFrom().getUserName());
            userData.get(chatId).setChatId(chatId);
            executeMsg(createMessage.getStartMessage(chatId, buttons));
            userData.get(chatId).setStringReceipt(buttons.get(data).getStringReceipt());
            userData.get(chatId).setStartEnterName(true);
            executeMsg(createMessage.startEnterFullName(chatId));
            return;
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