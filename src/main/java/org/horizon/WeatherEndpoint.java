package org.horizon;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/weather")
public class WeatherEndpoint {

    // Map each client session to the city they are interested in
    private static final Map<Session, String> clientCityMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("✅ Client connected: " + session.getId());
        // Default city is London until client sends another
        clientCityMap.put(session, "London");
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        JSONObject json = new JSONObject(message);
        String city = json.optString("city", "London");

        clientCityMap.put(session, city);

        // Send instant update for this client
        JSONObject weather = WeatherFetcher.fetchWeather(city);
        session.getAsyncRemote().sendText(weather.toString());
    }

    @OnClose
    public void onClose(Session session) {
        clientCityMap.remove(session);
        System.out.println("❌ Client disconnected: " + session.getId());
    }

    // Broadcast updates only to clients interested in this city
    public static void broadcastUpdate(JSONObject weatherUpdate) {
        String city = weatherUpdate.optString("name");
        clientCityMap.forEach((session, clientCity) -> {
            if (clientCity.equalsIgnoreCase(city)) {
                session.getAsyncRemote().sendText(weatherUpdate.toString());
            }
        });
    }

    public static Map<Session, String> getClientCityMap() {
        return clientCityMap;
    }
}
