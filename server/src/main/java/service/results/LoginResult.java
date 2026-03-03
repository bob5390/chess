package service.results;

public class LoginResult {
    private String username;
    private String authToken;

    public LoginResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() { return username; }
    public String getAuthToken() { return authToken; }

    @Override
    public boolean equals(Object obj) {
        LoginResult toTest = (LoginResult) obj;
        return username.equals(toTest.getUsername()) && authToken.equals(toTest.getAuthToken());
    }
}
