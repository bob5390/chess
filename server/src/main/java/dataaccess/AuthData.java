package dataaccess;

public class AuthData extends ServerData{
    private String authToken;

    public AuthData() {
        authToken = null;
    }

    public AuthData(String authToken) {
        this.authToken = authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
