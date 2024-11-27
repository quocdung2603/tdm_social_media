package com.example.tdm_social_media;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FCMNotificationSender {
    private String userid;
    private String title;
    private String message;
    private Context context;

    public FCMNotificationSender(String userid, String title, String message, Context context) {
        this.userid = userid;
        this.title = title;
        this.message = message;
        this.context = context;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/tdmsocialmedia-c0fcd/messages:send";

    public void sendNotification(String accessToken, String messagePayload) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL(FCM_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            connection.setRequestProperty("Content-Type", "application/json");

            // Gửi payload (JSON thông báo)
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(messagePayload.getBytes());
            outputStream.flush();
            outputStream.close();

            // Kiểm tra phản hồi từ server
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Notification sent successfully.");
            } else {
                System.err.println("Failed to send notification. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNotificationToDevice() {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users");
            database.child(this.getUserid()).child("token").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    try {
                        String accessToken = AccessTokenGenerator.getAccessToken(this.context);
                        Log.d("AccessToken", "Access Token: " + accessToken);
                        Log.d("AccessToken", "Author token: " + task.getResult().getValue(String.class));

                        String messagePayload = "{\n" +
                                "  \"message\": {\n" +
                                "    \"token\": \"" + task.getResult().getValue(String.class) + "\",\n" +
                                "    \"notification\": {\n" +
                                "      \"title\": \"" + this.getTitle() + "\",\n" +
                                "      \"body\": \"" + this.getMessage() + "\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}";
                        Log.d("AccessToken", "Message : " + messagePayload);
                        sendNotification(accessToken, messagePayload);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Failed to fetch recipient token: " + task.getException());
                }
            });
    }
}
