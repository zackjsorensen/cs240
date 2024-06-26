package serveraccess;

import com.google.gson.Gson;
import model.exception.ResponseException;
import model.CreateGameReq;
import model.ListGamesResult;
import model.UserData;
import serveraccess.websocket.GameOverException;
import serveraccess.websocket.WebSocketClient;
import ui.GamePlayUI;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;

import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class ServerFacade {
    ClientCommunicator communicator;
    Gson gson = new Gson();
    public WebSocketClient wsClient;
    int port;
    public boolean gameOver;

    public ServerFacade(int port){
        communicator = new ClientCommunicator(port);
        wsClient = null;
        this.port = port;
        gameOver = false;
    }

    public ServerFacade() {
        this(8080);
    }

    public ResponseObj register(UserData user) throws MalformedURLException, ResponseException {
        return communicator.register(user);
    }

    public ResponseObj login(UserData user) throws MalformedURLException, ResponseException {
        return communicator.login(user);
    }

    public ResponseObj logout(String authToken) throws MalformedURLException, ResponseException {
        return communicator.logout(authToken);
    }

    public void clear() throws MalformedURLException, ResponseException {
        communicator.clear();
    }

    public int createGame(String gameName, String authToken) throws MalformedURLException, ResponseException {

        ResponseObj res = communicator.createGame(gameName, authToken);
        CreateGameReq createGameRes = gson.fromJson(res.body(), CreateGameReq.class);
        return createGameRes.gameID();
    }

    public void joinGame(int gameID, String color, String authToken) throws IOException, ResponseException, DeploymentException, URISyntaxException {
        int code = communicator.joinGame(gameID, color, authToken);
        if (code > 199 && code < 301){

                wsClient = new WebSocketClient(color, port);
                // send CONNECT msg to WS server
                wsClient.send(gson.toJson(new ConnectCommand(authToken, gameID)));
        }
    }

    public void observeGame(int gameID, String authToken) throws DeploymentException, URISyntaxException, IOException {
        wsClient = new WebSocketClient("Observer", port);
        wsClient.send(gson.toJson(new ConnectCommand(authToken, gameID)));
    }

    public ListGamesResult listGames(String authToken) throws MalformedURLException, ResponseException {
        ResponseObj res = communicator.listGames(authToken);
        return gson.fromJson(res.body(), ListGamesResult.class);
    }

}
