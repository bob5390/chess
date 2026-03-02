package service;

public class LogoutResult {
    private boolean success;
    
    public LogoutResult(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() { return success; }
}
