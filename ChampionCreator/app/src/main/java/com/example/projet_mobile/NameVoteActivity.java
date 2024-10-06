package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NameVoteActivity extends AppCompatActivity {
    private ListView listviewSpellChosen, listviewChampionNameChosen;
    private TextView labelPlayerReady;
    private DatabaseReference gameDbRef;
    private ValueEventListener mListenerGame;
    private ChampionNameChosenAdapter championNameAdapter;
    private ArrayList<UserInGame> userInGameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_vote);

        getElement();
        setupListViewSpellChoice();
        setupListViewChampionNameChosen();
    }

    private void getElement()
    {
        labelPlayerReady = findViewById(R.id.labelPlayerReadyVote);
        listviewSpellChosen = findViewById(R.id.listviewSpellChosenVote);
        listviewChampionNameChosen = findViewById(R.id.listviewChampionNameChosenVote);

        listviewChampionNameChosen.setEnabled(false);

        gameDbRef = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child(GlobalVar.currentSaloonUid)
                .child("userInGame");
    }

    private void setupListViewChampionNameChosen()
    {
        userInGameList = new ArrayList<>();
        mListenerGame = gameDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInGameList.clear();

                for (DataSnapshot userInGameSnap: snapshot.getChildren())
                {
                    if (userInGameSnap.hasChild("championNameChosen"))
                    {
                        UserInGame userIngame = userInGameSnap.getValue(UserInGame.class);
                        userInGameList.add(userIngame);
                    }
                }

                // update txtView of player who already chose their spell
                labelPlayerReady.setText(userInGameList.size() + "/" + GlobalVar.currentSaloon.getMaxSize());

                championNameAdapter = new ChampionNameChosenAdapter (NameVoteActivity.this, userInGameList);
                listviewChampionNameChosen.setAdapter(championNameAdapter);

                // if everyone chose a champion name
                if (userInGameList.size() == GlobalVar.currentSaloon.getMaxSize())
                {
                    enableNameVote();
                    if (GlobalVar.currentGame != null)
                    {
                        SendRequestToFirebase.updateDatabase(FirebaseDatabase.getInstance()
                                .getReference("Games")
                                .child(GlobalVar.currentSaloonUid).child("stage"), "vote");
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void enableNameVote()
    {
        gameDbRef.removeEventListener(mListenerGame);
        listviewChampionNameChosen.setEnabled(true);

        Toast.makeText(this, "You can now vote !", Toast.LENGTH_SHORT).show();

        listviewChampionNameChosen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String positionInDb = getIntent().getStringExtra("positionInDb");
                UserInGame userInGame = (UserInGame) parent.getItemAtPosition(position);

                if (userInGame.getName().equals(GlobalVar.currentUser.getName()))
                    Toast.makeText(NameVoteActivity.this, "You can't vote for your self \uD83D\uDE02", Toast.LENGTH_SHORT).show();
                else
                {
                    listviewChampionNameChosen.setEnabled(false);
                    GlobalVar.currentUserInGame.setChampionNameVoted(userInGame.getChampionNameChosen());

                    SendRequestToFirebase.updateDatabase(gameDbRef
                            .child(positionInDb)
                            .child("championNameVoted"), userInGame.getChampionNameChosen());

                    Toast.makeText(NameVoteActivity.this, "Voted for : " + userInGame.getChampionNameChosen() + " by " + userInGame.getName(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(NameVoteActivity.this, EndGameActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
    private void setupListViewSpellChoice()
    {
        // TODO: 29-04-24 clean the code (repetition, Toast, Log.d, non final variable, bad code, error management,
        //                use better name for variable, function and class, use lambda to gain more place, ...)

        gameDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Spell> spellChosenList = new ArrayList<>();

                for (DataSnapshot userInGameSnap: snapshot.getChildren())
                {
                    Spell spellChosen = userInGameSnap.child("spellChosen").getValue(Spell.class);
                    if (spellChosen != null)
                        spellChosenList.add(spellChosen);
                }

                SpellInfoAdapter adapter = new SpellInfoAdapter(NameVoteActivity.this, R.layout.list_view_spellinfo, spellChosenList);
                listviewSpellChosen.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}