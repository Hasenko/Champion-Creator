package com.example.projet_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

public class SpellChoiceActivity extends AppCompatActivity {
    // prepare to get every element of the page
    private TextView labelError;
    private SearchView searchView;
    private ListView listView;
    private SpellInfoAdapter adapter;

    // create an array to store spell information (champion name, spell name, spell image name)
    private ArrayList<Spell> spell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_choice_user);


        // Obtain the categories of spell from the user
        String spellCAT = GlobalVar.currentUserInGame.getCategory();
        Toast.makeText(SpellChoiceActivity.this, spellCAT, Toast.LENGTH_SHORT).show();

        // Set up element in the page (searchview, listview)
        getElement();
        Toast.makeText(this, "Making request", Toast.LENGTH_LONG).show();
        initListView(spellCAT);
        initSearchView();
    }
    private void getElement()
    {
        // get element from this page
        labelError = findViewById(R.id.labelError);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
    }

    private void initListView(String spellCAT)
    {
        // prepare to send a request to riot api
        SendRequestToRiot APIRiot = new SendRequestToRiot(this);

        spell = new ArrayList<>();

        // make a request to obtain every champions name stored in an array
        APIRiot.getChampionList(new SendRequestToRiot.championTabCallBack() {
            @Override
            public void returnChampionTab(String[] championTab) {
                // make a request to obtain every spell and spell image name from champions on a certain categories from the champion array
                APIRiot.getSpellInfoList(championTab, spellCAT, new SendRequestToRiot.spellTabCallBack() {
                    @Override
                    public void returnSpellTab(String[][] spellTab)
                    {
                        // put every item from spellTab in a list
                        for (int i = 0; i < championTab.length; i++)
                        {
                            spell.add(new Spell(championTab[i], spellTab[i][0], spellTab[i][1], spellCAT, spellTab[i][2]));
                        }
                        
                        // create a custom adapter for the listView
                        adapter = new SpellInfoAdapter(getApplicationContext(), R.layout.list_view_spellinfo, spell);
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void returnError(String error)
                    {
                        Toast.makeText(getApplicationContext(), "Error while obtaining spells information.", Toast.LENGTH_LONG).show();
                        displayError("API SPELL ERROR : ", error);
                    }
                });
            }

            @Override
            public void returnError(String error)
            {
                Toast.makeText(getApplicationContext(), "Error while obtaining champions.", Toast.LENGTH_LONG).show();
                displayError("API CHAMPION ERROR : ", error);
            }
        });

        // Set up the listview with the spell list
        listView.setOnItemClickListener((parent, view, position, id) -> {
            GlobalVar.currentUserInGame.setSpellChosen((Spell) parent.getItemAtPosition(position));

            SendRequestToFirebase.sendUserSpellToDb(GlobalVar.currentSaloonUid, getIntent().getStringExtra("positionInDb"));

            Intent intent = new Intent(SpellChoiceActivity.this, NameChoiceActivity.class);
            intent.putExtra("positionInDb", getIntent().getStringExtra("positionInDb"));
            startActivity(intent);
            finish();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Spell spellCLicked = (Spell) parent.getItemAtPosition(position);

            AlertDialog.Builder dialogDesc = new AlertDialog.Builder(SpellChoiceActivity.this);
            dialogDesc.setTitle(spellCLicked.getSpellName())
                    .setMessage(spellCLicked.getDescription());

            dialogDesc.show();
            return true;
        });
    }

    // Method to display the error message on the app, log
    private void displayError(String tag, String error)
    {
        labelError.setVisibility(View.VISIBLE);
        labelError.setText(error);
    }

    // Method to set up the searchview and his filter
    private void initSearchView()
    {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Spell> filteredSpell = new ArrayList<Spell>();

                // Create a new adapter based on the search from the user
                for (int i = 0; i < spell.size(); i++) {
                    Spell spellData = spell.get(i);

                    if(spellData.getChampionName().toLowerCase().startsWith(newText.toLowerCase()) || spellData.getSpellName().toLowerCase().startsWith(newText.toLowerCase()))
                        filteredSpell.add(spellData);

                    adapter = new SpellInfoAdapter(getApplicationContext(), R.layout.list_view_spellinfo, filteredSpell);
                    listView.setAdapter(adapter);
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}