package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SaloonCreatorActivity extends AppCompatActivity {
    private EditText inputName;
    private CheckBox checkboxPrivate;
    private Spinner spinnerSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_creator);

        getElement();
        initEditText();
        initSpinner();
    }

    private void initEditText() { inputName.setText(GlobalVar.currentUser.getName() + " party"); }
    private void initSpinner()
    {
        List<Integer> numberList = new ArrayList<>();

        for (int i = 2; i <= 5; i++)
            numberList.add(i);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, numberList);
        spinnerSize.setAdapter(adapter);
    }
    private void getElement()
    {
        inputName = findViewById(R.id.inputSaloonName);
        checkboxPrivate = findViewById(R.id.checkBoxPassword);
        spinnerSize = findViewById(R.id.spinnerSaloonSize);
    }
    public void createSaloon(View view)
    {
        try
        {
            // get user input and remove space at the end when necessary
            final String name = inputName.getText().toString().replaceAll("\\s+$", "");
            final boolean isPrivate = checkboxPrivate.isChecked();
            final int maxSize = Integer.parseInt(spinnerSize.getSelectedItem().toString());

            if (!name.isEmpty())
            {
                // Prepare to register the saloon in the db
                Saloon saloon = new Saloon(GlobalVar.currentUser, name, maxSize, isPrivate);
                DatabaseReference saloonDbRef = FirebaseDatabase.getInstance().getReference("Saloons");
                saloonDbRef.child(saloon.getUid()).setValue(saloon).addOnCompleteListener(task -> {
                    // Saloon in firebase success
                    if (task.isSuccessful())
                    {
                        Toast.makeText(SaloonCreatorActivity.this, "Saloon successfully created", Toast.LENGTH_SHORT).show();
                        GlobalVar.currentSaloon = saloon;
                        GlobalVar.currentSaloonUid = saloon.getUid();

                        // redirect the user in the waiting room of his new saloon
                        Intent intent = new Intent(SaloonCreatorActivity.this, WaitingRoomActivity.class);
                        intent.putExtra("USER_ROLE", "creator");
                        startActivity(intent);
                    }
                    // Saloon in firebase failed
                    else
                    {
                        Toast.makeText(SaloonCreatorActivity.this, "Failed to create saloon, try again !", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                });
            }
            else
                Toast.makeText(getApplicationContext(), "Name can not be empty", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ignored)
        {

        }

    }

    public void closeSaloonCreatorPage(View view)
    {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
            closeSaloonCreatorPage(null);

        return super.onKeyDown(keyCode, event);
    }
}