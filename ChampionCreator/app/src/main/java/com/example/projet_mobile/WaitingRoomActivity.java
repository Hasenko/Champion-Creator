package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WaitingRoomActivity extends AppCompatActivity {
    private Dialog dialog;
    private EditText inputDialogName;
    private TextView creatorName;
    private TextView saloonName;
    private TextView saloonStatus;
    private TextView saloonSize;
    private TextView saloonPassword;
    private ListView listviewUser;
    private String positionUserInDb;
    private DatabaseReference saloonDbRef;
    private DatabaseReference userDbRef;
    private DatabaseReference gameDbRef;
    private ValueEventListener mListenerSaloon;
    private ValueEventListener mListenerUser;
    private ValueEventListener mListenerGames;
    private String roleCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        getElement();       // get element from page (only on create)
        fillLobbyInfo();    // fill lobby information (that will never change) (only on create)

        Intent intent = getIntent();
        roleCurrentUser = intent.getStringExtra("USER_ROLE");

        if (roleCurrentUser.equals("invited")) // if current is not the creator
        {
            positionUserInDb = String.valueOf(GlobalVar.currentSaloon.getCountUserInSaloon());
            SendRequestToFirebase.updateDatabase(saloonDbRef.child("userInSaloon").child(positionUserInDb), GlobalVar.currentUser);
            GlobalVar.currentSaloon.addUser(GlobalVar.currentUser);
        }
        else
            positionUserInDb = "0";

        SendRequestToFirebase.updateDatabase(saloonDbRef.child("countUserInSaloon"), GlobalVar.currentSaloon.getCountUserInSaloon());
        updateWaitingRoomOnEvent();
    }

    private void getElement() {
        creatorName = findViewById(R.id.waitingRoomCreator);
        saloonName = findViewById(R.id.waitingRoomName);
        saloonStatus = findViewById(R.id.waitingRoomStatus);
        saloonSize = findViewById(R.id.waitingRoomSize);
        saloonPassword = findViewById(R.id.waitingRoomPassword);
        listviewUser = findViewById(R.id.listviewWaitingRoomUser);

        saloonDbRef = FirebaseDatabase.getInstance().getReference("Saloons").child(GlobalVar.currentSaloonUid);
        gameDbRef = FirebaseDatabase.getInstance().getReference("Games");
    }

    private void fillLobbyInfo() {
        creatorName.setText(GlobalVar.currentSaloon.getCreator().getName());
        saloonName.setText(GlobalVar.currentSaloon.getName());
        saloonStatus.setText(GlobalVar.currentSaloon.getStringPrivacy());
        saloonPassword.setText(GlobalVar.currentSaloon.getPassword());
    }

    public void invitePlayer(View view) {
        dialog = new Dialog(WaitingRoomActivity.this);
        dialog.setContentView(R.layout.custom_dialog_single_value);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        TextView labelDialogTitle = dialog.findViewById(R.id.titleDialog);
        Button btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        Button btnDialogInvite = dialog.findViewById(R.id.btnDialogConfirm);

        labelDialogTitle.setText("Enter username");

        btnDialogCancel.setOnClickListener(v -> {
            try
            {
                userDbRef.removeEventListener(mListenerUser);
            }
            catch (Exception ignored)
            {

            }
            dialog.dismiss();
        });

        btnDialogInvite.setOnClickListener(v -> {
            inputDialogName = dialog.findViewById(R.id.inputDialog);
            String nameUserToInvite = inputDialogName.getText().toString();

            if (!nameUserToInvite.isEmpty()) {
                boolean userIsInSaloon = false;
                for (User userInSaloon : GlobalVar.currentSaloon.getUserInSaloon()) {
                    if (userInSaloon.getName().equals(nameUserToInvite)) {
                        userIsInSaloon = true;
                        break;
                    }
                }

                if (userIsInSaloon)
                    Toast.makeText(WaitingRoomActivity.this, "This user in already in the saloon !", Toast.LENGTH_SHORT).show();
                else
                {
                    sendNotification(nameUserToInvite);
                }
            } else
                Toast.makeText(WaitingRoomActivity.this, "Please enter a name.", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void callApi(JSONObject jsonObject)
    {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization", "Bearer INSERT API KEY HERE")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
    private void sendNotification(String nameUserToInvite)
    {
        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        mListenerUser = userDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        boolean finded = false;
                        for (DataSnapshot snap : snapshot.getChildren())
                        {
                            User user = snap.getValue(User.class);
                            if (user.getName().equals(nameUserToInvite))
                            {
                                finded = true;

                                try
                                {
                                    JSONObject jsonObject = new JSONObject();

                                    JSONObject notificationObj = new JSONObject();
                                    notificationObj.put("title", "Champion Creator");
                                    notificationObj.put("body", GlobalVar.currentUser.getName() + " invited you to play with him !");

                                    JSONObject dataObj = new JSONObject();
                                    dataObj.put("saloonUid", GlobalVar.currentSaloonUid);

                                    jsonObject.put("notification", notificationObj);
                                    jsonObject.put("data", dataObj);
                                    jsonObject.put("to", user.getFcmToken());

                                    callApi(jsonObject);
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(WaitingRoomActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }

                                Toast.makeText(WaitingRoomActivity.this, "Invitation send", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                        if (!finded)
                            Toast.makeText(WaitingRoomActivity.this, "User not found", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void startGame() {
        saloonDbRef.removeEventListener(mListenerSaloon);
        if (roleCurrentUser.equals("creator")) {
            List<UserInGame> usersInGame = new ArrayList<>();
            String[] catArray = {"Q", "W", "E", "R", "P"};
            List<String> categories = Arrays.asList(catArray);
            Collections.shuffle(categories);

            int i = 0;
            for (User users : GlobalVar.currentSaloon.getUserInSaloon()) {
                usersInGame.add(new UserInGame(users, categories.get(i)));
                i++;
            }

            Game game = new Game(usersInGame, GlobalVar.currentUser);
            GlobalVar.currentGame = game;
            SendRequestToFirebase.updateDatabase(FirebaseDatabase.getInstance().getReference("Games").child(GlobalVar.currentSaloonUid), game);
            SendRequestToFirebase.deleteDatabase(saloonDbRef);
        }

        mListenerGames = gameDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(GlobalVar.currentSaloonUid))
                {
                    SendRequestToFirebase.getCurrentUserInGameValueFromDatabase();

                    Intent intent = new Intent(WaitingRoomActivity.this, SpellChoiceActivity.class);
                    intent.putExtra("positionInDb", positionUserInDb);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateWaitingRoomOnEvent() {
        mListenerSaloon = saloonDbRef.addValueEventListener(new ValueEventListener() {    // look if data change from db
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) // if data as changed from last look then modify local information
                {
                    GlobalVar.currentSaloon = snapshot.getValue(Saloon.class);
                    saloonSize.setText(GlobalVar.currentSaloon.getStringUserInSaloonSize());

                    UsersAdapter adapter = new UsersAdapter(WaitingRoomActivity.this, GlobalVar.currentSaloon.getUserInSaloon());
                    listviewUser.setAdapter(adapter);

                    if (GlobalVar.currentSaloon.getCountUserInSaloon() == GlobalVar.currentSaloon.getMaxSize())
                        startGame();

                }
                else // if saloons reference is destroy (if the creator decide to leave his waiting room)
                {
                    Toast.makeText(WaitingRoomActivity.this, "Saloon destroyed, creator leaved !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WaitingRoomActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void quitWaitingRoom(View view) {
        try
        {
            if (roleCurrentUser.equals("creator"))  // if current user is the creator
                SendRequestToFirebase.deleteDatabase(saloonDbRef);  // delete saloon
            else {
                SendRequestToFirebase.deleteDatabase(saloonDbRef.child("userInSaloon").child(positionUserInDb));
                GlobalVar.currentSaloon.removeUserFromSaloon(Integer.parseInt(positionUserInDb));
                SendRequestToFirebase.updateDatabase(saloonDbRef.child("countUserInSaloon"), GlobalVar.currentSaloon.getCountUserInSaloon());
            }
        }
        catch (Exception ignored)
        {

        }

        GlobalVar.currentSaloon = null;
        GlobalVar.currentSaloonUid = null;

        if (!MenuActivity.exist)
        {
            Intent intent = new Intent(WaitingRoomActivity.this, MenuActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        saloonDbRef.removeEventListener(mListenerSaloon);

        try {
            gameDbRef.removeEventListener(mListenerGames);
        }
        catch (Exception ignored)
        {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
            quitWaitingRoom(null);

        return super.onKeyDown(keyCode, event);
    }
}
