package dataaccess;

import kotlin.NotImplementedError;

public class DataAccess {
    public UserData getUser(String username) {
        throw new NotImplementedError();
    }

    public UserData createUser(UserData userData) {
        return userData;
    }

    public AuthData createAuth(AuthData authData) {
        return authData;
    }
}
