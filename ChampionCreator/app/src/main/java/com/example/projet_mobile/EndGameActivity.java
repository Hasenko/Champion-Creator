package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/*
    Activity class :
        Used for the users in game as a transition to the result activity.
*/
public class EndGameActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private DatabaseReference gameDbRef;
    private ValueEventListener mListenerGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        getElement();
        initListener();
    }

    private void updateProgressBar(int status)
    {
        progressBar.setProgress(status, true);
        progressBar.setMax(GlobalVar.currentSaloon.getMaxSize());
    }

    private void initListener()
    {
        ArrayList<UserInGame> userInGameList = new ArrayList<>();
        mListenerGame = gameDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInGameList.clear();
                for (DataSnapshot userInGameSnap: snapshot.getChildren())
                {
                    if (userInGameSnap.hasChild("championNameVoted"))
                    {
                        UserInGame userIngame = userInGameSnap.getValue(UserInGame.class);
                        userInGameList.add(userIngame);
                    }
                }

                updateProgressBar(userInGameList.size());

                // if everyone chose a champion name
                if (userInGameList.size() == GlobalVar.currentSaloon.getMaxSize())
                    goToGameResult();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void goToGameResult()
    {
        gameDbRef.removeEventListener(mListenerGame);
        Toast.makeText(this, "Everyone has voted", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, GameResultActivity.class);
        startActivity(intent);
        finish();
    }
    private void getElement()
    {
        progressBar = findViewById(R.id.progressBarPlayerVoted);
        gameDbRef = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child(GlobalVar.currentSaloonUid)
                .child("userInGame");
    }
}