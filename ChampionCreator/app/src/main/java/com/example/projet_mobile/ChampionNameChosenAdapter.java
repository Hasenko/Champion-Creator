package com.example.projet_mobile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/*
    Adapter class :
        Used for the listView of champion name chosen by the users in game.
*/
public class ChampionNameChosenAdapter extends ArrayAdapter<UserInGame> {
    private final Activity CONTEXT;
    private final List<UserInGame> USERINGAMELIST;

    public ChampionNameChosenAdapter(Activity context, List<UserInGame> UserInGameList)
    {
        super(context, R.layout.list_view_championnamechosen, UserInGameList);
        this.CONTEXT = context;
        this.USERINGAMELIST = UserInGameList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = CONTEXT.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.list_view_championnamechosen, null, true);

        TextView championName = listItemView.findViewById(R.id.championNameChosen);
        TextView username = listItemView.findViewById(R.id.userNameChosen);

        UserInGame userIngame = USERINGAMELIST.get(position);

        championName.setText(userIngame.getChampionNameChosen());
        username.setText(userIngame.getName());

        return listItemView;
    }
}