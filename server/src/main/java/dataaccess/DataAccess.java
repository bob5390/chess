package dataaccess;

import java.util.Collection;

import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {
    public UserData getUser(String username);
    public String createUser(UserData userData);
    public AuthData getAuth(String authToken);
    public AuthData getAuth(UserData userData);
    public AuthData createAuth(String authToken, String username);
    public AuthData createAuth(String username);
    public boolean deleteAuth(AuthData authData);
    public Collection<GameData> listGames();
    public GameData getGame(String gameID);
    public GameData createGame(String gameName);
    public GameData updateGame(GameData gameData, UserData userData, String teamColor);

    public boolean clearGames();
    public boolean clearAuths();
    public boolean clearUsers();
}
