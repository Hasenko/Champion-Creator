package com.example.projet_mobile;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {
    private final Activity mContext;
    private final List<User> usersList;

    public UsersAdapter(Activity context, List<User> usersList)
    {
        super(context, R.layout.list_view_users, usersList);
        this.mContext = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.list_view_users, null, true);

        TextView userName = listItemView.findViewById(R.id.userName);
        TextView score = listItemView.findViewById(R.id.userScore);

        User user = usersList.get(position);

        try
        {
            userName.setText(user.getName());
            score.setText(String.valueOf(user.getScore()));
        }
        catch (Exception ignored)
        {

        }


        return listItemView;
    }
}
