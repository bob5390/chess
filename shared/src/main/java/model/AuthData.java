package model;

public class AuthData {
    private String authToken;
    private String username;

    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() { return authToken; }
    public String getUsername() { return username; }

    @Override
    public boolean equals(Object obj) {
        AuthData toTest = (AuthData) obj;
        return toTest != null && this.authToken.equals(toTest.getAuthToken()) && this.username.equals(toTest.getUsername());
    }
}
