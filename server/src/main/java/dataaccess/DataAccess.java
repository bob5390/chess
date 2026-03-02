package dataaccess;

public interface DataAccess {
    public UserData getUser(String username);
    public UserData createUser(UserData userData);
    public AuthData getAuth(UserData userData);
    public AuthData createAuth(UserData userData);
}
