package app.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    @Value("${bot.username}")
    private String botName;
    @Value("${bot.token}")
    private String token;
    @Value("${admins.chat}")
    private  Long adminsChat;
    @Value("${support.chat}")
    private  Long supportChat;

    public Long getSupportChat() {
        return supportChat;
    }
    public Long getAdminsChat() {
        return adminsChat;
    }
    public String getBotName() {
        return botName;
    }
    public String getToken() {
        return token;
    }
}