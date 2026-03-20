package results;

public class RegisterResult {
    private String username;
    private String authToken;

    public RegisterResult(String authToken, String username) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() { return username; }
    public String getAuthToken() { return authToken; }

    @Override
    public boolean equals(Object obj) {
        RegisterResult toTest = (RegisterResult) obj;
        return username.equals(toTest.getUsername()) && authToken.equals(toTest.getAuthToken());
    }
}
