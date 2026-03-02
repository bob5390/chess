package dataaccess;

import java.util.Collection;

public class MemoryDataAccess implements DataAccess {

    @Override
    public UserData getUser(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public UserData getUser(AuthData authData) {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public UserData createUser(UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public AuthData getAuth(String authToken) {
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public AuthData getAuth(UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public AuthData createAuth(UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'createAuth'");
    }

    @Override
    public boolean deleteAuth(AuthData authData) {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new UnsupportedOperationException("Unimplemented method 'listGames'");
    }

    @Override
    public GameData createGame(String gameName, UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }
}
