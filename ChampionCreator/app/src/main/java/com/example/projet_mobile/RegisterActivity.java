package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText inputMail, inputName, inputPassword, inputPasswordConfirmation;
    private Spinner spinnerCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getRegisterElement();
        fillSpinnerWithCountry();
    }

    // METHOD TO FILL THE SPINNER FROM A TEXT FILE
    private void fillSpinnerWithCountry()
    {
        List<String> countriesList;

        CSVReader reader;
        countriesList = new ArrayList<>();

        // Browse the entire files in assets folder and put every line in a list
        try
        {
            reader = new CSVReader(new InputStreamReader(getAssets().open("country-coord.csv")));
            String[] nextLine;

            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                countriesList.add(nextLine[0]);
            }

            reader.close();
        }
        catch (IOException e)
        {
            Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        // Put the list in a spinner
        spinnerCountry = findViewById(R.id.spinnerRegisterCountry);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countriesList);

        spinnerCountry.setAdapter(adapter);
        spinnerCountry.setSelection(adapter.getPosition("Belgium"));
    }

    // METHOD TO KNOW IF REGISTER INFO IS ALREADY IN DB OR PSWD IS NOT THE SAME AS THE CONF
    private boolean checkForm(String name, String mail, String password, String passwordConfirmation)
    {
        // return true if name, mail and password is not empty and password equal the confirmation
        return !(password.isEmpty()) && password.equals(passwordConfirmation) && !(name.isEmpty()) && !(mail.isEmpty());
    }

    // METHOD TO GET ELEMENT FROM THE PAGE
    private void getRegisterElement()
    {
        inputName = findViewById(R.id.inputSaloonName);
        inputMail = findViewById(R.id.inputLoginMail);

        inputPassword = findViewById(R.id.inputLoginPassword);
        inputPasswordConfirmation = findViewById(R.id.inputRegisterPasswordConfirmation);
    }

    // METHOD TO REGISTER IN THE FIREBASE DB
    public void sendRegisterInfo(View view)
    {
        // get user input and remove space at the end when necessary
        final String name = inputName.getText().toString().replaceAll("\\s+$", "");
        final String mail = inputMail.getText().toString().replaceAll("\\s+$", "");
        final String password = inputPassword.getText().toString();
        final String passwordConfirmation = inputPasswordConfirmation.getText().toString();
        final String country = spinnerCountry.getSelectedItem().toString();

        if (checkForm(name, mail, password, passwordConfirmation))
        {
            try
            {
                FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean finded = false;
                        for (DataSnapshot userDataSnap : snapshot.getChildren())
                        {
                            if(name.equals(userDataSnap.getValue(User.class).getName()))
                                finded = true;
                        }

                        if (finded)
                            Toast.makeText(RegisterActivity.this, "Username is already taken", Toast.LENGTH_SHORT).show();
                        else
                        {
                            // Prepare to register a new account
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.createUserWithEmailAndPassword(mail, password)
                                    .addOnCompleteListener(task -> {
                                        // Authentification success
                                        if (task.isSuccessful()) {
                                            // Prepare to register the account in the db
                                            User user = new User(name, mail, country);
                                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            usersRef.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                                                // Userdata in firebase success
                                                if (task1.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Success to register", Toast.LENGTH_SHORT).show();

                                                    // Send user to login page with mail already filled
                                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                    intent.putExtra("mail", mail);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                // Userdata in firebase failed
                                                else
                                                    Toast.makeText(RegisterActivity.this, "User failed to registered : " + task1.getException(), Toast.LENGTH_SHORT).show();
                                            });
                                        }
                                        // Authentification failed
                                        else
                                            Toast.makeText(RegisterActivity.this, "An account already exist with this mail !", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            catch (Exception e)
            {
                Toast.makeText(RegisterActivity.this, "FATAL ERROR : " + e, Toast.LENGTH_SHORT).show();
            }

        }
        else
            Toast.makeText(RegisterActivity.this, "Register information is incorrect.", Toast.LENGTH_SHORT).show();
    }

}