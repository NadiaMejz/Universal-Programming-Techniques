/**
 *
 *  @author Mejza Nadia S31677
 *
 */
package zad1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Service {
    String kraj;
    String kodWaluty;

    public Service(String kraj) {
        this.kraj = kraj;
        this.kodWaluty = KrajWalut.getCurrencyCode(kraj);
    }

    public String getWeather(String miasto) {
        try {
            String apiKey = "3818cbc95e629aaf6a918af7bc18b20f";
            String urlStr = "http://api.openweathermap.org/data/2.5/weather?q=" +
                    miasto + "," + kraj + "&appid=" + apiKey + "&units=metric";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // return response.toString();
            JsonObject jsonObject= JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray jsonArray=jsonObject.getAsJsonArray("weather");
            JsonObject weatherObject=jsonArray.get(0).getAsJsonObject();
            return weatherObject.get("main").getAsString()+" ,temperatura: "+ jsonObject.getAsJsonObject("main").get("temp").getAsDouble()+" C";

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Double getRateFor(String targetCurrencyCode) {
        try {
            String urlStr = "https://open.er-api.com/v6/latest/" + this.kodWaluty;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String json = response.toString();

            String searchStr = "\"" + targetCurrencyCode.toUpperCase() + "\":";
            int index = json.indexOf(searchStr);
            if (index == -1) {
                throw new RuntimeException("Nie znaleziono kursu dla waluty: " + targetCurrencyCode);
            }
            int startIndex = index + searchStr.length();
            int endIndex = json.indexOf(",", startIndex);
            if (endIndex == -1) { // ostatnia wartość może nie mieć przecinka
                endIndex = json.indexOf("}", startIndex);
            }
            String rateStr = json.substring(startIndex, endIndex).trim();
            return Double.valueOf(rateStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Pobiera kurs NBP – kurs złotego (PLN) względem waluty kraju (this.kodWaluty)
    public Double getNBPRate() {
        try {
            return getRateFromNBP(this.kodWaluty);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Double getRateFromNBP(String currencyCode) throws Exception {
        if ("PLN".equalsIgnoreCase(currencyCode)) {
            return 1.0;
        }
        String urlStr = "http://api.nbp.pl/api/exchangerates/rates/A/"
                + currencyCode + "/?format=json";
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String json = response.toString();
        int midIndex = json.indexOf("\"mid\":");
        if (midIndex == -1) {
            throw new RuntimeException("Nie znaleziono pola \"mid\" w odpowiedzi NBP!");
        }
        int colonIndex = json.indexOf(":", midIndex);
        int endIndex = json.indexOf("}", colonIndex);
        int commaIndex = json.indexOf(",", colonIndex);
        if (commaIndex != -1 && commaIndex < endIndex) {
            endIndex = commaIndex;
        }

        String valueStr = json.substring(colonIndex + 1, endIndex).trim();
        return Double.valueOf(valueStr);
    }
}
