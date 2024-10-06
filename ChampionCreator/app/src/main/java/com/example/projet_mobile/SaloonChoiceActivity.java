package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SaloonChoiceActivity extends AppCompatActivity {
    private Dialog dialog;
    private TextView labelDialogTitle;
    private Button btnDialogCancel, btnDialogConfirm;
    private EditText inputDialogPassword;
    private ListView saloonListview;
    private List<Saloon> saloonList;
    private ValueEventListener mListener;
    private DatabaseReference saloonDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saloon_choice);

        getElement();
        initSaloonListview();
    }

    private void getElement()
    {
        saloonListview = findViewById(R.id.saloonListview);
        saloonDbRef = FirebaseDatabase.getInstance().getReference("Saloons");
    }

    private void joinWaitingRoom(Saloon saloon)
    { // DUPLICATE
        GlobalVar.currentSaloon = saloon;
        GlobalVar.currentSaloonUid = saloon.getUid();

        Intent intent = new Intent(SaloonChoiceActivity.this, WaitingRoomActivity.class);
        intent.putExtra("USER_ROLE", "invited");
        startActivity(intent);
        Toast.makeText(SaloonChoiceActivity.this, "Joining waiting room ...", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void initSaloonListview()
    {
        updateSaloonListview();
        saloonListview.setOnItemClickListener((parent, view, position, id) -> {
            Saloon saloon = (Saloon) parent.getItemAtPosition(position);

            if (saloon.getCountUserInSaloon() == saloon.getMaxSize()) // saloon is already full
                Toast.makeText(SaloonChoiceActivity.this, "This saloon is full.", Toast.LENGTH_SHORT).show();
            else if (saloon.getIsPrivate()) // saloon clicked is private (need a password)
                showDialogPassword(saloon); // ask password to the current user, then check if it is correct
            else // saloon clicked is public (free to join)
                joinWaitingRoom(saloon);
        });
    }
    private void showDialogPassword(Saloon saloon)
    {
        dialog = new Dialog(SaloonChoiceActivity.this);
        dialog.setContentView(R.layout.custom_dialog_single_value);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        labelDialogTitle = dialog.findViewById(R.id.titleDialog);
        btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        btnDialogConfirm = dialog.findViewById(R.id.btnDialogConfirm);

        labelDialogTitle.setText("Enter saloon password");

        btnDialogCancel.setOnClickListener(v -> dialog.dismiss());

        btnDialogConfirm.setOnClickListener(v -> {
            inputDialogPassword = dialog.findViewById(R.id.inputDialog);
            String passwordUser = inputDialogPassword.getText().toString();

            if (passwordUser.equals(saloon.getPassword()))
            {
                dialog.dismiss();
                saloonDbRef.child(saloon.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Saloon newSaloon = snapshot.getValue(Saloon.class);
                        joinWaitingRoom(newSaloon);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else
                Toast.makeText(SaloonChoiceActivity.this, "Password incorrect", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
    public void sendToSaloonCreator(View view)
    {
        Intent intent = new Intent(this, SaloonCreatorActivity.class);
        startActivity(intent);
    }

    private void updateSaloonListview()
    {
        saloonList = new ArrayList<>();

        mListener = saloonDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                saloonList.clear();

                for(DataSnapshot saloonDataSnap : snapshot.getChildren())
                {
                    Saloon saloon = saloonDataSnap.getValue(Saloon.class);
                    saloonList.add(saloon);
                }

                SaloonAdapter adapter = new SaloonAdapter(SaloonChoiceActivity.this, saloonList);
                saloonListview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void closeSaloonPage(View view)
    {
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saloonDbRef.removeEventListener(mListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            closeSaloonPage(null);

        return super.onKeyDown(keyCode, event);
    }

}