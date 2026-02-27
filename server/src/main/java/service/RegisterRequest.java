package service;

import java.util.Map;

import com.google.gson.Gson;

public class RegisterRequest extends ServiceRequest {
    private Map<String, String> registerData;

    public RegisterRequest(String jsonData) {
        super(jsonData);
        this.registerData = new Gson().fromJson(jsonData, Map.class);
    }

    public String getUsername() {
        return registerData.get("username");
    }

    public String getPassword() {
        return registerData.get("password");
    }

    public String getEmail() {
        return registerData.get("email");
    }
}
