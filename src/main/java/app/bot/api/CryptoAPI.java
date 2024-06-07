package app.bot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class CryptoAPI {
    private static final String apiKey ="a1355d33fa642a7392e8ba4cee83909f72b68fdf572d4ca40ea617bf3be3c2d9";

    public static double getPrice(String from, String to) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(getResponse(from, to)).get(to).asDouble();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.00;
        }
    }

    private static String getResponse(String from, String to) throws IOException {
        String url = "https://min-api.cryptocompare.com/data/price?fsym=" + from + "&tsyms=" + to;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("authorization", "Apikey " + apiKey);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}