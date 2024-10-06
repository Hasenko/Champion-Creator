package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Paper.init(this);

        String userMailKey = Paper.book().read(GlobalVar.userMailKey);
        String userPasswordKey = Paper.book().read(GlobalVar.userPasswordKey);

        if (getIntent().getExtras() != null) // open from notification
        {
            GlobalVar.currentSaloonUid = getIntent().getExtras().getString("saloonUid");
            DatabaseReference saloonDbRef = FirebaseDatabase.getInstance().getReference("Saloons").child(GlobalVar.currentSaloonUid);

            saloonDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GlobalVar.currentSaloon = snapshot.getValue(Saloon.class);

                    forceLogin(userMailKey, userPasswordKey, WaitingRoomActivity.class, "USER_ROLE","invited");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else if (userMailKey != null && userPasswordKey != null)
        {
            if (!userMailKey.isEmpty() && !userPasswordKey.isEmpty())
                forceLogin(userMailKey, userPasswordKey, MenuActivity.class, "", "");
        }

        setContentView(R.layout.activity_main);
    }

    private void forceLogin(String userMailKey, String userPasswordKey, Class activityClass, String tagExtra, String extraValue)
    {
        SendRequestToFirebase.login(userMailKey, userPasswordKey)
                .addOnCompleteListener(task -> {
                    // Login success
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // Put the current user information in a global var
                                        GlobalVar.currentUser = snapshot.getValue(User.class);

                                        Toast.makeText(MainActivity.this, "Logging in ...", Toast.LENGTH_SHORT).show();

                                        // Send user to menu activity
                                        Intent intent = new Intent(MainActivity.this, activityClass);
                                        intent.putExtra(tagExtra, extraValue);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(MainActivity.this, "ERROR : " + error, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    // Login failed
                    else
                        Toast.makeText(MainActivity.this, "Email or password was modified !", Toast.LENGTH_SHORT).show();
                });
    }

    public void sendToLogin(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
    public void sendToRegister(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}