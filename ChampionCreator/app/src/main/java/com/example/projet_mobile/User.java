package com.example.projet_mobile;

/* CLASS FOR USER INFORMATION */
public class User {
    private String name;
    private String mail;
    private String country;
    private String fcmToken;
    private int score;

    public User() {}

    public User(String name, String mail, String country)
    {
        this.name = name;
        this.mail = mail;
        this.country = country;
    }

    public void setName(String name) { this.name = name; }
    public void setScore(int score) { this.score = score; }

    public String getName() { return name; }
    public String getMail() { return mail; }
    public String getCountry() { return country; }
    public int getScore() { return score; }
    public String getFcmToken() {return fcmToken; }

}
