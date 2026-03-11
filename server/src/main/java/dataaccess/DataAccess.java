package dataaccess;

import java.util.Collection;

import io.javalin.http.HttpResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

public interface DataAccess {
    public UserData getUser(String username) throws HttpResponseException;
    public String createUser(UserData userData) throws HttpResponseException;
    public AuthData getAuth(String authToken) throws HttpResponseException;
    public AuthData getAuth(UserData userData) throws HttpResponseException;
    public AuthData createAuth(String authToken, String username) throws HttpResponseException;
    public AuthData createAuth(String username) throws HttpResponseException;
    public boolean deleteAuth(AuthData authData) throws HttpResponseException;
    public Collection<GameData> listGames() throws HttpResponseException;
    public GameData getGame(String gameID) throws HttpResponseException;
    public GameData createGame(String gameName) throws HttpResponseException;
    public GameData joinGame(GameData gameData, UserData userData, String teamColor) throws HttpResponseException;
    public GameData updateGame(GameData gameData) throws HttpResponseException;

    public boolean clearGames() throws HttpResponseException;
    public boolean clearAuths() throws HttpResponseException;
    public boolean clearUsers() throws HttpResponseException;
}
