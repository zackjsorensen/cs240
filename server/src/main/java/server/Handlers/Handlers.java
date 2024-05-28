package server.Handlers;

import Service.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;
import model.UserData;
import server.ReqResObjects.ErrorResponse;
import server.ReqResObjects.JoinRequest;
import server.ReqResObjects.ListGamesResult;
import server.ReqResObjects.LoginResult;
import spark.Request;
import spark.Response;
import java.util.List;

public class Handlers {
    public UserService userService;
    public AuthService authService;
    public GameService gameService;
    Gson gson;
    UserData empty;

    public Handlers() {
        userService = new UserService(new MemoryUserDAO()); // we have our userService that starts with an empty userDAO
        authService = new AuthService(new MemoryAuthDAO());
        gameService = new GameService(new MemoryGameDAO());
        gson = new Gson();
        empty = (new UserData(null, null, null));
    }

    public String registerUser(Request req, Response res) {
        model.UserData user = gson.fromJson(req.body(), UserData.class);
        if(!userService.checkRequest(user)){
            return RespondToBadReq(res);
        }
        if (userService.getUser(user.username()) != null){
            res.status(403);
            return gson.toJson(new LoginResult(null, "Error: already taken"));
        }
        userService.addUser(user);
        res.status(200);
        return gson.toJson(authService.createAuth(user.username()));
    }

    private String RespondToBadReq(Response res) {
        res.status(400);
        return gson.toJson(new ErrorResponse("Error: bad request"));
    }

    public String login(Request req, Response res) {
        model.UserData user = gson.fromJson(req.body(), UserData.class);
        if (!userService.verify(user)) {
            return RespondToUnauthorized(res);
        }
        res.status(200);
        return gson.toJson(authService.createAuth(user.username()));
    }

    private String RespondToUnauthorized(Response res) {
        res.status(401);
        return gson.toJson(new ErrorResponse( "Error: unauthorized"));
    }

    public String logout(Request req, Response res) {
        String authToken = req.headers("authorization");
        if (authService.getAuth(authToken)== null){
           return RespondToUnauthorized(res);
        }
        authService.deleteAuth(authToken);
        res.status(200);
        return gson.toJson(empty);
    }

    public String clearDB(Request req, Response res) {
        try {
            userService.clearUsers();
            authService.clear();
            gameService.clear();
            res.status(200);
            String body = gson.toJson(empty);// I guess this is returning an empty response
            res.body(body);
            return body;
        } catch (Exception e) {
            res.status(500);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
    }

    public String createGame(Request req, Response res){
        String gameName = gson.fromJson(req.body(), GameData.class).gameName();
        if (gameName == null){
            return RespondToBadReq(res);
        }
        if (!checkAuthToken(req)){
            return RespondToUnauthorized(res);
        }
        int gameID = gameService.createGame(gameName);
        res.status(200);
        return gson.toJson(new GameData(gameID, null, null, null, null));
    }

    private boolean checkAuthToken(Request req){
        String authToken = req.headers("authorization");
        if (authToken == null){
            return false;
        }
        return authService.getAuth(authToken) != null;
    }

    public String joinGame(Request req, Response res) throws DataAccessException {
        JoinRequest joinRequest = gson.fromJson(req.body(), JoinRequest.class);
        if (!checkAuthToken(req)){
            return RespondToUnauthorized(res);
        }
        if (joinRequest.gameID() == 0 || joinRequest.playerColor()== null || (!joinRequest.playerColor().equals("BLACK") && !joinRequest.playerColor().equals("WHITE"))){
            return RespondToBadReq(res);
        }
        String username = authService.getAuth(req.headers("authorization")).username();
        if (username == null){
            return RespondToUnauthorized(res);
        }

        try {
            if (gameService.isColorTaken(joinRequest.gameID(), joinRequest.playerColor())){
                res.status(403);
                return gson.toJson(new LoginResult(null, "Error: already taken"));
            }

        } catch (Exception e){
            res.status(403);
            return gson.toJson(new ErrorResponse(e.getMessage()));
        }
        res.status(200);
        gameService.joinGame(joinRequest.gameID(), username, joinRequest.playerColor());
        return gson.toJson(empty);
    }

    public String listGames(Request req, Response res){
        if (!checkAuthToken(req)){
            return RespondToUnauthorized(res);
        }
        res.status(200);
        return gson.toJson(new ListGamesResult(List.of(gameService.listGames())));
    }


}