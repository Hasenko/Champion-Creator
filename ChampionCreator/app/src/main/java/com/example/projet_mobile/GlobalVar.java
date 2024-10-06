package com.example.projet_mobile;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// CLASS to store variable used in all project files
public class GlobalVar {
    public static final String userMailKey = "userMail";
    public static final String userPasswordKey = "userPassword";

    public static final DatabaseReference gameDbRef = FirebaseDatabase.getInstance().getReference("Games");
    public static final DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference("Users");

    public static User currentUser;
    public static Saloon currentSaloon;
    public static String currentSaloonUid;
    public static UserInGame currentUserInGame;
    public static Game currentGame;

    public static void resetVar()
    {
        currentSaloon = null;
        currentSaloonUid = null;
        currentUserInGame = null;
        currentGame = null;
    }
}