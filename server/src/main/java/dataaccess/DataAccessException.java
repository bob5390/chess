package dataaccess;

import io.javalin.http.HttpStatus;
import kotlin.NotImplementedError;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public String toJson() {
        throw new NotImplementedError();
    }

    public HttpStatus toHttpStatusCode() {
        throw new NotImplementedError();
    }
}
