package service;

import java.util.Map;

import com.google.gson.Gson;

import dataaccess.*;

public class RegisterResult {
    private UserData userData;
    private AuthData authData;

    public RegisterResult(AuthData authData, UserData userData) {
        this.userData = userData;
        this.authData = authData;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("username", userData.getUsername(), "authToken", authData.getAuthToken()));
    }
}
