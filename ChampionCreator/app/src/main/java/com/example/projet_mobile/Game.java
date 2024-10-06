package com.example.projet_mobile;

import java.util.List;

/*
    Object class :
        Used to store game information.
*/
public class Game {
    private List<UserInGame> userInGame;
    private User creator;
    private String stage; // spell - champion name - vote - end

    public Game() {}

    public Game(List<UserInGame> userInGame, User creator)
    {
        this.userInGame = userInGame;
        this.creator = creator;
        this.stage = "spell";
    }

    public User getCreator() {
        return creator;
    }
    public String getStage() { return stage; }
    public List<UserInGame> getUserInGame() { return userInGame; }
}
