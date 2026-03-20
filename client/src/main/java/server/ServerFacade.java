package server;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.google.gson.Gson;

import io.javalin.http.HttpResponseException;
import requests.*;
import results.*;

public class ServerFacade {

    private String serverURL = null;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
    }

    public RegisterResult register(RegisterRequest request) {
        return makeRequest("POST", "/user", request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) {
        return makeRequest("POST", "/session", request, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request) {
        return makeRequest("DELETE", "/session", request, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) {
        return makeRequest("POST", "/game", request, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request) {
        return makeRequest("PUT", "/game", request, JoinGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest request) {
        return makeRequest("GET", "/game", request, ListGamesResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if(request != null) {
                http.addRequestProperty("Content-Type", "application/json");
                String requestData = new Gson().toJson(request);
                OutputStream requestBody = http.getOutputStream();
                requestBody.write(requestData.getBytes());
            }
            http.connect();
            if(http.getResponseCode() < 200 || http.getResponseCode() > 299) {
                throw new HttpResponseException(http.getResponseCode());
            }

            if(http.getContentLength() < 0 && responseClass != null) {
                InputStream responseBody = http.getInputStream();
                InputStreamReader reader = new InputStreamReader(responseBody);
                return new Gson().fromJson(reader, responseClass);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpResponseException(500, "error making request to server");
        }
    }
}
