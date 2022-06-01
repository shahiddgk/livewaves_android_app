package com.app.livewave.models.ParameterModels;

public class AuthModel {
    String email, password, name, username, password_confirmation;

    public AuthModel(String email, String password, String name, String username, String password_confirmation) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.username = username;
        this.password_confirmation = password_confirmation;
    }

    public AuthModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthModel(String email) {
        this.email = email;
    }
}
