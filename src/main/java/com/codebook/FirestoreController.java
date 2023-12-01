package com.codebook;

import java.io.FileInputStream;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;

public class FirestoreController {
    public static FirebaseOptions initFirestore() {
        GoogleCredentials credentials = null;
        try {
            InputStream serviceAccount = new FileInputStream("src\\main\\resources\\com\\codebook\\service_acc.json");
            credentials = GoogleCredentials.fromStream(serviceAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FirebaseOptions options = null;
        if (credentials != null) {
            options = FirebaseOptions
                    .builder()
                    .setCredentials(credentials)
                    .setStorageBucket("codebook-e3539.appspot.com")
                    .setDatabaseUrl("https://codebook-e3539-default-rtdb.firebaseio.com")
                    .build();
        }
        assert options != null;
        return options;
    }

}
