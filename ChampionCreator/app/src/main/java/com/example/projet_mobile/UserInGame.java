package com.example.projet_mobile;


import com.google.firebase.database.Exclude;

public class UserInGame extends User {
    private String category;
    private Spell spellChosen;
    private String championNameChosen;
    private String championNameVoted;
    @Exclude
    private int voteNumber;

    public UserInGame() {}

    public UserInGame(User user, String category)
    {
        super(user.getName(), user.getMail(), user.getCountry());
        this.setScore(user.getScore());
        this.category = category;
    }
    public String getCategory() { return category; }
    public Spell getSpellChosen() { return spellChosen; }
    public void setSpellChosen(Spell spellChosen) {this.spellChosen = spellChosen; }
    public String getChampionNameChosen() { return championNameChosen; }
    public void setChampionNameChosen(String championNameChosen) {this.championNameChosen = championNameChosen; }
    public String getChampionNameVoted() { return championNameVoted; }
    public void setChampionNameVoted(String championNameVoted) {this.championNameVoted = championNameVoted; }

    public int getVoteNumber() {
        return voteNumber;
    }

}
