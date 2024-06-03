package app.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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
}