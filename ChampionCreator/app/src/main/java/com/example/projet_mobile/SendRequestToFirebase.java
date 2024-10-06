package com.example.projet_mobile;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public abstract class SendRequestToFirebase {
    public static void updateDatabase(DatabaseReference dbRef, Object newValue)
    {
        dbRef.setValue(newValue);
    }
    public static void deleteDatabase(DatabaseReference dbRef)
    {
        dbRef.removeValue();
    }
    public static void getCurrentUserInGameValueFromDatabase()
    {
        GlobalVar.gameDbRef.child(GlobalVar.currentSaloonUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    List<UserInGame> allUserInGame = snapshot.getValue(Game.class).getUserInGame();

                    for (UserInGame userInGame: allUserInGame)
                    {
                        if (userInGame.getName().equals(GlobalVar.currentUser.getName()))
                            GlobalVar.currentUserInGame = userInGame;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static Task<AuthResult> login(String mail, String password)
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        return mAuth.signInWithEmailAndPassword(mail, password);
    }

    public static void addTokenToDb()
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful())
                updateDatabase(GlobalVar.usersDbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fcmToken"), task.getResult());
        });
    }

    public static void removeToken()
    {
        deleteDatabase(GlobalVar.usersDbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("fcmToken"));
    }

    public static void sendUserSpellToDb(String gameUid, String positionInDb)
    {
        updateDatabase(GlobalVar.gameDbRef
                .child(gameUid)
                .child("userInGame")
                .child(positionInDb)
                .child("spellChosen"),
                    GlobalVar.currentUserInGame.getSpellChosen());
    }

    public static void addPoint(String userUid, int point)
    {
        GlobalVar.currentUser.setScore(GlobalVar.currentUser.getScore() + point);
        updateDatabase(GlobalVar.usersDbRef.child(userUid).child("score"), GlobalVar.currentUser.getScore());
    }
}
