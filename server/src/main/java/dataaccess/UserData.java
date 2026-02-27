package dataaccess;

public class UserData extends ServerData {
    private String authToken;

    public UserData() {
        authToken = null;
    }

    public UserData(String authToken) {
        setAuthToken(authToken);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}
