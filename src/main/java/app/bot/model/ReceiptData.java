package app.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ReceiptData {
    @Id
    @Column(name = "id")
    private int msgId;
    private Long chatId;
    private String userName;
    private int sumInRub;
    private double sumInBth;
    private String fullName;
    @Column(length = 4096)
    private String textToAdmin;
    private boolean startEnterSum;
    private boolean startEnterName;
    private String stringReceipt;

    public double getSumInBth() {
        return sumInBth;
    }

    public void setSumInBth(double sumInBth) {
        this.sumInBth = sumInBth;
    }

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getStringReceipt() {
        return stringReceipt;
    }

    public void setStringReceipt(String stringReceipt) {
        this.stringReceipt = stringReceipt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isStartEnterName() {
        return startEnterName;
    }

    public void setStartEnterName(boolean startEnterName) {
        this.startEnterName = startEnterName;
    }

    public boolean isStartEnterSum() {
        return startEnterSum;
    }

    public void setStartEnterSum(boolean startEnterSum) {
        this.startEnterSum = startEnterSum;
    }

    public String getTextToAdmin() {
        return textToAdmin;
    }

    public void setTextToAdmin(String textToAdmin) {
        this.textToAdmin = textToAdmin;
    }

    public int getSumInRub() {
        return sumInRub;
    }

    public void setSumInRub(int sumInRub) {
        this.sumInRub = sumInRub;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}