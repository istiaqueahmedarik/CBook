package com.codebook;

import java.io.IOException;

public class DashboardController {
    public void logout() throws IOException {
        LoginValidator.fileDelete();
        Main.setRoot("login");
    }
}
