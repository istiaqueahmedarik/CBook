package com.codebook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String username, password, email, name;
    public String id_token;

    User(String username, String password, String email, String name, String id_token) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.id_token = id_token;
    }

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
