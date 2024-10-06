package com.example.projet_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap map;
    public static boolean exist = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.map = googleMap;

        putMarkerOnMap();
    }

    private void putMarkerOnMap()
    {
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Integer> hashMapCountryScoreboard = new HashMap<>();

                for (DataSnapshot userSnap : snapshot.getChildren())
                {
                    User user = userSnap.getValue(User.class);
                    try
                    {
                        hashMapCountryScoreboard.put(user.getCountry(), hashMapCountryScoreboard.get(user.getCountry()) + user.getScore());
                    }
                    catch (NullPointerException e)
                    {
                        hashMapCountryScoreboard.put(user.getCountry(), user.getScore());
                    }

                }

                try
                {
                    for (String country : hashMapCountryScoreboard.keySet())
                    {
                        CSVReader reader = new CSVReader(new InputStreamReader(getAssets().open("country-coord.csv")));
                        String[] nextLine;

                        while ((nextLine = reader.readNext()) != null) {
                            if (nextLine[0].equals(country))
                            {
                                LatLng latLng = new LatLng(Double.parseDouble(nextLine[4]), Double.parseDouble(nextLine[5]));

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(country + ", score : " + hashMapCountryScoreboard.get(country));

                                map.addMarker(markerOptions);
                                break;
                            }
                        }

                        reader.close();
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
    }
    public void startGame(View view)
    {
        Intent intent = new Intent(this, SaloonChoiceActivity.class);
        startActivity(intent);
    }
    public void viewProfile(View view)
    {

        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        exist = true;
    }
}