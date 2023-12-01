package com.codebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.openhtmltopdf.util.ThreadCtx;

public class LoginValidator {
    public static void encryptor(String email, String password) throws IOException {
        String encryptedString = "";
        for (int i = 0; i < email.length(); i++) {
            encryptedString += (char) (email.charAt(i) + 7);
        }
        encryptedString += " ";
        for (int i = 0; i < password.length(); i++) {
            encryptedString += (char) (password.charAt(i) + 7);
        }
        FileWriter fileWriter = new FileWriter("validator.txt");
        fileWriter.write(encryptedString);
        fileWriter.close();

    }

    private static String EmailDecryptor(String encrypteString) {
        String email = "";
        for (int i = 0; i < encrypteString.length(); i++) {
            if (encrypteString.charAt(i) == ' ') {
                break;
            }
            email += (char) (encrypteString.charAt(i) - 7);
        }
        return email;
    }

    private static String PasswordDecryptor(String encrypteString) {
        String password = "";
        boolean flag = false;
        for (int i = 0; i < encrypteString.length(); i++) {
            if (encrypteString.charAt(i) == ' ') {
                flag = true;
                continue;
            }
            if (flag) {
                password += (char) (encrypteString.charAt(i) - 7);
            }
        }
        return password;
    }

    public static void fileDelete() {
        File file = new File("validator.txt");
        file.delete();
    }

    public static boolean validatePassword() throws IOException, InterruptedException, ExecutionException {
        try {
            File file = new File("validator.txt");
            Scanner scanner = new Scanner(file);
            int size = 0;
            String line = "";
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                size += line.length();
            }
            if (size == 0) {
                scanner.close();
                return false;
            }
            // RequestHandler requestHandler = new RequestHandler();
            // String req = "http://localhost:8080/login?username=" +
            // EmailDecryptor(line) +
            // "&&password=" +
            // PasswordDecryptor(line);
            // String response = requestHandler.GetRequest(req);
            User user = new User(EmailDecryptor(line), PasswordDecryptor(line));
            BasicAuthResponse response = Methods.login(user);
            GetjsonData getjsonData = new GetjsonData();
            String email = response.getEmail();
            System.out.println(email);
            if (email == null) {
                scanner.close();
                return false;
            }
            System.out.println(response);
            String idToken = response.getIdToken();
            MainUser.idToken = idToken;
            // req = "http://localhost:8080/getUserDetails?id=" +
            // idToken +
            // "&&token=" +
            // getjsonData.getData(response, "refreshToken");
            // response = requestHandler.GetRequest(req);
            System.out.println(idToken);
            UserDetails userDetails = Methods.getUserDetails(idToken);
            System.out.println(userDetails);
            if (userDetails.getEmailVerified().equals("false")) {
                scanner.close();
                return false;
            }
            String displayName = userDetails.getDisplayName();
            String photoUrl = userDetails.getPhotoUrl();
            MainUser.displayName = displayName;
            MainUser.photoUrl = photoUrl;
            MainUser.emailVerified = userDetails.getEmailVerified();
            MainUser.idToken = idToken;
            RequestHandler requestHandler1 = new RequestHandler();
            // String request = "http://localhost:8080/getRealName" +
            // "?username=" +
            // displayName;
            // String res = requestHandler1.GetRequest(request);
            MainUser.username = Methods.getProfile(email).username;
            MainUser.userEmail = email;

            scanner.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }
}