package com.example;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpstashQueueService implements QueueService {

    private static final String Redis_Rest_URL="https://composed-seal-96090.upstash.io";
    private static final String Redis_Rest_TOKEN= "Your_Token";

    @Override
    public void push(String queueUrl, String msgBody) {
        try {
            // LPUSH - add message to queue
            call("/lpush/" + queueUrl + "/" + msgBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message pull(String queueUrl) {
        try {
            String response = call("/rpop/" + queueUrl);

            if (response == null || response.contains("null")) {
                return null;
            }

            // extract value after colon
            int index = response.indexOf(":");
            if (index != -1) {
                response = response.substring(index + 1);
            }

            // remove brackets and quotes
            response = response.replace("}", "")
                    .replace("\"", "")
                    .replace("[", "")
                    .replace("]", "")
                    .trim();

            return new Message(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void delete(String queueUrl, String receiptId) {
        // Redis pop already removes element
    }

    private String call(String path) throws Exception {

        URL url = new URL(Redis_Rest_URL+path);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Authorization",
                "Bearer " + Redis_Rest_TOKEN
        );

        BufferedReader reader =new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        return reader.readLine();
    }
}