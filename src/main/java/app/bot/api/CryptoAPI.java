package app.bot.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CryptoAPI {
    private final String apiKey ="545872a65b7237d9a9f07a9a87df174240a77e56ccf68f38ffec15f9f70231ce";
    public double getPrice(String from, String to) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(getResponse(from, to)).get(to).asDouble();
        } catch (Exception e) {
            return 0.00;
        }
    }

    private String getResponse(String from, String to) throws IOException {
        String url = "https://min-api.cryptocompare.com/data/price?fsym=" + from + "&tsyms=" + to;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("authorization", "Apikey " + apiKey);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}