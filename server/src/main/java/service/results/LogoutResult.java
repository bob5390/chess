package service.results;

import java.util.Map;

import com.google.gson.Gson;

public class LogoutResult {
    private boolean success;

    public LogoutResult(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() { return success; }
    public String toJson() {
        return new Gson().toJson(Map.of("success", success));
    }
    
    @Override
    public boolean equals(Object obj) {
        LogoutResult toTest = (LogoutResult) obj;
        return success == toTest.getSuccess();
    }
}
