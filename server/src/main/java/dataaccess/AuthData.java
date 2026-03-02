package dataaccess;

public class AuthData {
    private String authToken;

    public AuthData(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() { return authToken; }
}
