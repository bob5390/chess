package service;

import java.util.Map;

import com.google.gson.Gson;

public class ClearResult {
    private boolean success;

    public ClearResult(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() { return success; }
    public String toJson() {
        return new Gson().toJson(Map.of("success", success));
    }
}
