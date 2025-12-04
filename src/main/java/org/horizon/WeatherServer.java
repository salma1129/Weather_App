package org.horizon;

import org.glassfish.tyrus.server.Server;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherServer {

    public static void main(String[] args) {
        Server server = new Server(
                "localhost",
                8081,
                "/",
                null,
                Collections.singleton(WeatherEndpoint.class)
        );

        try {
            server.start();
            System.out.println("WebSocket server started at ws://localhost:8081/weather");

            // Background timer to fetch weather every 10 seconds for all requested cities
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    WeatherEndpoint.getClientCityMap().values().stream().distinct().forEach(city -> {
                        JSONObject weather = WeatherFetcher.fetchWeather(city);
                        WeatherEndpoint.broadcastUpdate(weather);
                    });
                }
            }, 0, 10000); // every 10 seconds

            System.out.println("Press Enter to stop the server...");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            timer.cancel();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
