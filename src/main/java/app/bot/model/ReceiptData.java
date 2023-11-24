package app.bot.model;

import org.telegram.telegrambots.meta.api.objects.Message;

public class ReceiptData {
    private String userName;
    private String payDirection;
    private int sumInRub;
    private int sumInBth;
    private String fullName;
    private String textToAdmin;
    private boolean startEnterSum;
    private boolean startEnterName;

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

    public String getPayDirection() {
        return payDirection;
    }

    public void setPayDirection(String payDirection) {
        this.payDirection = payDirection;
    }

    public String getTextToAdmin() {
        return textToAdmin;
    }

    public void setTextToAdmin(String textToAdmin) {
        this.textToAdmin = textToAdmin;
    }

    public String getProject() {
        return payDirection;
    }

    public void setProject(String project) {
        this.payDirection = project;
    }

    public int getSumInRub() {
        return sumInRub;
    }

    public void setSumInRub(int sumInRub) {
        this.sumInRub = sumInRub;
    }

    public int getSumInBth() {
        return sumInBth;
    }

    public void setSumInBth(int sumInBth) {
        this.sumInBth = sumInBth;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}