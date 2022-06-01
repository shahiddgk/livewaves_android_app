package com.app.livewave.models.ParameterModels;

public class ChangePasswordRequestModel {
    String old_password,password,password_confirmation;

    public ChangePasswordRequestModel(String old_password, String password, String password_confirmation) {
        this.old_password = old_password;
        this.password = password;
        this.password_confirmation = password_confirmation;
    }
}
