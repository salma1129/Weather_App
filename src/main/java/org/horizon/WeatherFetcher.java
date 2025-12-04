package org.horizon;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {

    private static final String API_KEY = "d1718bdcd5afed3fe066771030f9ba05\n";

    // Fetch weather for a given city
    public static JSONObject fetchWeather(String city) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q="
                    + city + "&units=metric&appid=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
            }
            in.close();
            conn.disconnect();

            return new JSONObject(content.toString());
        } catch (Exception e) {
            JSONObject error = new JSONObject();
            error.put("city", city);
            error.put("error", "Failed to fetch weather: " + e.getMessage());
            return error;
        }
    }
}
