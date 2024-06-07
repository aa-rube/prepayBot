package app.bot.util;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
public class CommissionConfig {
    private static final String CONFIG_FILE = "/home/root/prepayBot/commission.percent";

    private static Properties properties;

    static {
        properties = new Properties();
        loadConfig();
    }

    public static void setCommissionPercent(int percent) {
        properties.setProperty(CONFIG_FILE, String.valueOf(percent));
        saveConfig();
    }

    public static int getCommissionPercent() {
        return Integer.parseInt(properties.getProperty(CONFIG_FILE, "0"));
    }

    private static void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
