package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    private EditText inputMail, inputPassword;
    private CheckBox checkBoxRemember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getLoginElement();

        // When an user is registered, he is redirect here and the mail is taken from register form and set in mail input
        Intent intent = getIntent();
        inputMail.setText(intent.getStringExtra("mail"));
    }

    private void getLoginElement()
    {
        inputMail = findViewById(R.id.inputLoginMail);
        inputPassword = findViewById(R.id.inputLoginPassword);
        checkBoxRemember = findViewById(R.id.checkBoxRememberLogin);

        Paper.init(this);
    }

    private boolean checkForm(String mail, String password)
    {
        // return true if mail and password are not empty
        return !mail.isEmpty() && !password.isEmpty();
    }
    public void sendLoginInfo(View view)
    {
        final String mail = inputMail.getText().toString().replaceAll("\\s+$", "");
        final String password = inputPassword.getText().toString();
        final boolean remember = checkBoxRemember.isChecked();

        if (checkForm(mail, password))
        {
            try
            {
                // Verify if the mail and password given is in Firebase
                SendRequestToFirebase.login(mail, password)
                        .addOnCompleteListener(task -> {
                            // Login success
                            if (task.isSuccessful()) {
                                SendRequestToFirebase.addTokenToDb();
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                // Put the current user information in a global var
                                                GlobalVar.currentUser = snapshot.getValue(User.class);

                                                if (remember)
                                                {
                                                    Paper.book().write(GlobalVar.userMailKey, mail);
                                                    Paper.book().write(GlobalVar.userPasswordKey, password);
                                                }
                                                Toast.makeText(LoginActivity.this, "Successfully login !", Toast.LENGTH_SHORT).show();

                                                // Send user to menu activity
                                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(LoginActivity.this, "ERROR : " + error, Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                            // Login failed
                            else
                                Toast.makeText(LoginActivity.this, "Bad email or password !", Toast.LENGTH_SHORT).show();
                        });
            }
            catch (Exception e)
            {
                Toast.makeText(LoginActivity.this, "FATAL ERROR : " + e, Toast.LENGTH_SHORT).show();
            }

        }
        else
            Toast.makeText(LoginActivity.this, "Email or password is empty.", Toast.LENGTH_SHORT).show();
    }
}