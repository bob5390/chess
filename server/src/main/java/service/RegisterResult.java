package service;

public class RegisterResult {
    private String username;
    private String authToken;

    public RegisterResult(String authToken, String username) {
        this.username = username;
        this.authToken = authToken;
    }
}
