package server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

import requests.*;
import results.*;

public class ServerFacade {
    private HttpClient httpClient;
    private String serverURL = null;
    private Gson gson;

    public ServerFacade(String serverURL) {
        this.serverURL = serverURL;
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }

    public RegisterResult register(RegisterRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("POST", "/user", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("POST", "/session", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, LoginResult.class);
    }

    public LogoutResult logout(LogoutRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("DELETE", "/session", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, LogoutResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("POST", "/game", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("PUT", "/game", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, JoinGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("GET", "/game", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, ListGamesResult.class);
    }

    public ClearResult clearDatabase(ClearRequest request) throws Exception {
        HttpRequest httpRequest = buildRequest("DELETE", "/db", request);
        HttpResponse<String> response = sendRequest(httpRequest);
        return handleResponse(response, ClearResult.class);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        Builder request = HttpRequest.newBuilder()
                             .uri(URI.create(serverURL + path))
                             .method(method, makeRequestBody(body));
        if(body != null) {
            request.setHeader("Content-Type", "application/json");
            if(body.getClass().equals(LogoutRequest.class)) {
                request.setHeader("Authorization", ((LogoutRequest)body).getAuthToken());
            } else if(body.getClass().equals(CreateGameRequest.class)) {
                request.setHeader("Authorization", ((CreateGameRequest)body).getAuthToken());
            } else if(body.getClass().equals(JoinGameRequest.class)) {
                request.setHeader("Authorization", ((JoinGameRequest)body).getAuthToken());
            } else if(body.getClass().equals(ListGamesRequest.class)) {
                request.setHeader("Authorization", ((ListGamesRequest)body).getAuthToken());
            }
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object body) {
        if(body != null) {
            return BodyPublishers.ofString(gson.toJson(body));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return httpClient.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception("error making request to server");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        int status = response.statusCode();
        if (status < 200 || status > 299) {
            String body = response.body();
            if (body != null) {
                throw new Exception("bad response but received body: " + body + "; reported status: " + status);
            }

            throw new Exception("other failure: " + status);
        }

        if (responseClass != null) {
            return gson.fromJson(response.body(), responseClass);
        }
        return null;
    }
}
