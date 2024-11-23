package com.example.tdm_social_media;

import android.content.Context;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class AccessTokenGenerator {
    private static final String[] SCOPES = {
            "https://www.googleapis.com/auth/firebase.messaging" // Thêm phạm vi quyền ở đây
    };

    public static String getAccessToken(Context context) throws IOException {
        try (InputStream serviceAccountStream = context.getAssets().open("tdmsocialmedia-c0fcd-firebase-adminsdk-q2rm7-f3aa6665ff.json")) {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(serviceAccountStream)
                    .createScoped(Arrays.asList(SCOPES));
            googleCredentials.refresh();
            return googleCredentials.getAccessToken().getTokenValue();
        }
    }
}
