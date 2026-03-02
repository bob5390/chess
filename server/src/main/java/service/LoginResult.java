package service;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.AuthData;
import dataaccess.UserData;

public class LoginResult {
    AuthData authData;
    UserData userData;

    public LoginResult(AuthData authData, UserData userData) {
        this.authData = authData;
        this.userData = userData;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("username", userData.getUsername(), "authToken", authData.getAuthToken()));
    }
}
