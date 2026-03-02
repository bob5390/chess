package dataaccess;

import java.util.Collection;

public interface DataAccess {
    public UserData getUser(String username);
    public UserData getUser(AuthData authData);
    public UserData createUser(UserData userData);
    public AuthData getAuth(String authToken);
    public AuthData getAuth(UserData userData);
    public AuthData createAuth(UserData userData);
    public boolean deleteAuth(AuthData authData);
    public Collection<GameData> listGames();
    public GameData createGame(String gameName, UserData userData);
}
