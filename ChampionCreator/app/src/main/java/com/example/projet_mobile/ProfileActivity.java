package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity {
    private TextView name, mail, country, score;
    private ListView scoreboard;
    private List<User> usersList;
    private ValueEventListener mListener;
    private final DatabaseReference usersDbRef = FirebaseDatabase.getInstance().getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getElement();

        putUserInformation();
        initScoreboard();
    }

    private void getElement()
    {
        name = findViewById(R.id.userName);
        mail = findViewById(R.id.userMail);
        country = findViewById(R.id.userCountry);
        score = findViewById(R.id.userScore);
        scoreboard = findViewById(R.id.listViewScoreboard);
    }
    public void logout(View view)
    {
        SendRequestToFirebase.removeToken();

        Paper.book().destroy();
        GlobalVar.resetVar();

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finishAffinity();
    }
    private void initScoreboard()
    {
        usersList = new ArrayList<>();

        mListener = usersDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot usersDataSnap : snapshot.getChildren())
                {
                    User user = usersDataSnap.getValue(User.class);
                    usersList.add(user);
                }

                // sort the list of users based on the score
                usersList.sort((o1, o2) -> Integer.compare(o2.getScore(), o1.getScore()));

                UsersAdapter adapter = new UsersAdapter(ProfileActivity.this, usersList);
                scoreboard.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void putUserInformation()
    {
        name.setText(GlobalVar.currentUser.getName());
        mail.setText(GlobalVar.currentUser.getMail());
        country.setText(GlobalVar.currentUser.getCountry());
        score.setText(String.valueOf(GlobalVar.currentUser.getScore()));
    }

    public void closeProfile(View view)
    {
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        usersDbRef.removeEventListener(mListener);
    }
}