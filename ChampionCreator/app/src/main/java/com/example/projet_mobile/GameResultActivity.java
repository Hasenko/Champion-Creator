package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.xml.KonfettiView;

/*
    Activity class :
        Used to calculate the winner and loser of a game.
*/
public class GameResultActivity extends AppCompatActivity {
    private TextView labelResultOfTheGame;
    private ImageView gifResult;
    private DatabaseReference gameDbRef;
    private KonfettiView confetti;
    private final int MAX_POINT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameresult);

        Log.d("DEBUG OGNG", "Coming to onCreate of GameResultActivity");

        getElement();
        setUpActivity();
    }

    private void getElement()
    {
        labelResultOfTheGame = findViewById(R.id.labelGameResult);
        gifResult = findViewById(R.id.imageViewGif);
        confetti = findViewById(R.id.konfettiView);

        gameDbRef = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child(GlobalVar.currentSaloonUid)
                .child("userInGame");
    }

    private void setUpActivity()
    {
        gameDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    Log.d("DEBUG OGNG", "Coming to onDataChange of setUpActivity");

                    ArrayList<UserInGame> userInGameList = new ArrayList<>();
                    for (DataSnapshot userInGameSnap : snapshot.getChildren()) // add every user from the game in a list
                    {
                        UserInGame userInGame = userInGameSnap.getValue(UserInGame.class);
                        userInGameList.add(userInGame);
                    }

                    // Hashmap to store player name and score
                    HashMap<String, Integer> hashMapScoreboard = getHashMapScoreboard(userInGameList);

                    // Hashmap to store every name based on there score
                    HashMap<Integer, ArrayList<String>> hashMapScoreboardInverted = getHashMapScoreboardInverted(hashMapScoreboard);

                    // get max score from the inverted hashmap
                    int maxScore = (Collections.max(hashMapScoreboardInverted.keySet()));
                    ArrayList<String> playerList = hashMapScoreboardInverted.get(maxScore);

                    updateViewAndScore(playerList);

                    // if the current user is the creator of the game, remove the game from firebase
                    if (GlobalVar.currentGame != null) {
                        SendRequestToFirebase.deleteDatabase(FirebaseDatabase.getInstance()
                                .getReference("Games")
                                .child(GlobalVar.currentSaloonUid));
                    }
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateViewAndScore(ArrayList<String> playerList)
    {
        Log.d("DEBUG OGNG", "Coming to updateViewAndScore");
        if (playerList.contains(GlobalVar.currentUser.getName())) // user is in first place
        {
            if (playerList.size() > 1) // user is not alone in first place
            {
                loadGameResultScreen("Draw !", R.drawable.frenn, MAX_POINT/playerList.size() + " point given !");
                SendRequestToFirebase.addPoint(FirebaseAuth.getInstance().getCurrentUser().getUid(), MAX_POINT/playerList.size());
                playSound(R.raw.drawsound);
            }
            else // user is alone in first place
            {
                loadGameResultScreen("You won the game !", R.drawable.peepokcvictory, MAX_POINT + " point given !");
                playSound(R.raw.victorysound);
                showConfetti();

                SendRequestToFirebase.addPoint(FirebaseAuth.getInstance().getCurrentUser().getUid(), MAX_POINT);
            }
        }
        else // user in not in first place
        {
            loadGameResultScreen("You lose the game !", R.drawable.peepokcdefeatgif, "0 point given !");
            playSound(R.raw.defeatsound);
        }
    }

    private HashMap<Integer, ArrayList<String>> getHashMapScoreboardInverted(HashMap<String, Integer> hashMapScoreboard)
    {
        HashMap<Integer, ArrayList<String>> hashMapScoreboardInverted = new HashMap<>();

        for (String name : hashMapScoreboard.keySet()) {
            Integer score = hashMapScoreboard.get(name);

            if (hashMapScoreboardInverted.containsKey(score))
                hashMapScoreboardInverted.get(score).add(name);
            else
            {
                hashMapScoreboardInverted.put(score,new ArrayList<String>()
                {{
                    add(name);
                }});
            }
        }

        return hashMapScoreboardInverted;
    }

    private HashMap<String, Integer> getHashMapScoreboard (ArrayList<UserInGame> userInGameList)
    {
        HashMap<String, Integer> hashMapScoreboard = new HashMap<>();
        for (UserInGame userInGame : userInGameList) {
            String championNameChosen = userInGame.getChampionNameChosen();
            hashMapScoreboard.put(userInGame.getName(), 0);

            for (UserInGame userInGame2 : userInGameList) {
                if (userInGame2.getChampionNameVoted().equals(championNameChosen))
                {
                    hashMapScoreboard.put(userInGame.getName(), hashMapScoreboard.get(userInGame.getName()) + 1);
                }
            }
        }

        return hashMapScoreboard;
    }
    private void loadGameResultScreen(String textViewText, int gif, String toastText)
    {
        labelResultOfTheGame.setText(textViewText);
        Glide.with(this).load(gif).into(gifResult);
        Toast.makeText(GameResultActivity.this, toastText, Toast.LENGTH_LONG).show();
    }

    private void playSound(int sound)
    {
        MediaPlayer winSound = MediaPlayer.create(this, sound);
        winSound.start();
    }

    private void showConfetti()
    {
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .build();

        confetti.start(party);
    }
    public void quitGameResult(View view)
    {
        GlobalVar.resetVar();
        finish();
    }
}