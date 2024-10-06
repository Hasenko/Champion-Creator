package com.example.projet_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NameChoiceActivity extends AppCompatActivity {
    private EditText inputUser;
    private TextView labelPlayerReady;
    private ListView listviewSpellChosen;
    private Button btnSendChampionNameChosen;
    private DatabaseReference gameDbRef;
    private ValueEventListener mListenerGame;
    private ArrayList<Spell> spellChosenList;
    private SpellInfoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_name_choice_user);

        getElement();
        // Spell spellChosen = GlobalVar.currentUserInGame.getSpellChosen();
        setupListView();
    }

    private void setupListView()
    {
        spellChosenList = new ArrayList<>();
        mListenerGame = gameDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try
                {
                    spellChosenList.clear();

                    for (DataSnapshot userInGameSnap: snapshot.getChildren())
                    {
                        Spell spellChosen = userInGameSnap.child("spellChosen").getValue(Spell.class);
                        if (spellChosen != null)
                            spellChosenList.add(spellChosen);
                    }

                    // update txtView of player who already chose their spell
                    labelPlayerReady.setText(spellChosenList.size() + "/" + GlobalVar.currentSaloon.getMaxSize());

                    adapter = new SpellInfoAdapter(NameChoiceActivity.this, R.layout.list_view_spellinfo, spellChosenList);
                    listviewSpellChosen.setAdapter(adapter);

                    // if everyone chose a spell
                    if (spellChosenList.size() == GlobalVar.currentSaloon.getMaxSize())
                    {
                        enableNameChoice();
                        if (GlobalVar.currentUserInGame.getName().equals(GlobalVar.currentGame.getCreator().getName()))
                        {
                            SendRequestToFirebase.updateDatabase(FirebaseDatabase.getInstance()
                                    .getReference("Games")
                                    .child(GlobalVar.currentSaloonUid).child("stage"), "champion name");
                        }

                    }
                }
                catch (Exception ignored)
                {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listviewSpellChosen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Spell spellCLicked = (Spell) parent.getItemAtPosition(position);

                AlertDialog.Builder dialogDesc = new AlertDialog.Builder(NameChoiceActivity.this);
                dialogDesc.setTitle(spellCLicked.getSpellName())
                        .setMessage(spellCLicked.getDescription());

                dialogDesc.show();
            }
        });
    }

    private void enableNameChoice()
    {
        gameDbRef.removeEventListener(mListenerGame);

        inputUser.setEnabled(true);
        btnSendChampionNameChosen.setEnabled(true);
        Toast.makeText(this, "You can now chose a name based on the spell list !", Toast.LENGTH_LONG).show();
    }

    private void getElement()
    {
        inputUser = findViewById(R.id.inputChampionNameChosen);
        labelPlayerReady = findViewById(R.id.labelPlayerReadyVote);
        listviewSpellChosen = findViewById(R.id.listviewSpellChosenVote);
        btnSendChampionNameChosen = findViewById(R.id.btnSendChampionNameChosen);

        inputUser.setEnabled(false);
        btnSendChampionNameChosen.setEnabled(false);

        gameDbRef = FirebaseDatabase.getInstance()
                .getReference("Games")
                .child(GlobalVar.currentSaloonUid)
                .child("userInGame");
    }

    public void sendChampionNameChosen(View view)
    {
        String championNameChosen = inputUser.getText().toString().replaceAll("\\s+$", "");

        if (championNameChosen.isEmpty())
            Toast.makeText(this, "Champion name can't be empty !", Toast.LENGTH_SHORT).show();
        else
        {
            String positionInDb = getIntent().getStringExtra("positionInDb");
            GlobalVar.currentUserInGame.setChampionNameChosen(championNameChosen);
            SendRequestToFirebase.updateDatabase(gameDbRef
                    .child(positionInDb)
                    .child("championNameChosen"), championNameChosen);

            Intent intent = new Intent(this, NameVoteActivity.class);
            intent.putExtra("positionInDb", positionInDb);
            startActivity(intent);
            finish();
        }
    }

}