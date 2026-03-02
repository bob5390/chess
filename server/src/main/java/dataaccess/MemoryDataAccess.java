package dataaccess;

public class MemoryDataAccess implements DataAccess {

    @Override
    public UserData getUser(String username) {
        throw new UnsupportedOperationException("Unimplemented method 'getUser'");
    }

    @Override
    public UserData createUser(UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'createUser'");
    }

    @Override
    public AuthData getAuth(UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
    }

    @Override
    public AuthData createAuth(UserData userData) {
        throw new UnsupportedOperationException("Unimplemented method 'createAuth'");
    }
}
