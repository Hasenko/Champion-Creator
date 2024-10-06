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

public class SaloonAdapter extends ArrayAdapter<Saloon> {
    private final Activity mContext;
    private final List<Saloon> saloonsList;

    public SaloonAdapter(Activity context, List<Saloon> saloonsList)
    {
        super(context, R.layout.list_view_users, saloonsList);
        this.mContext = context;
        this.saloonsList = saloonsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View listItemView = inflater.inflate(R.layout.list_view_saloon, null, true);

        TextView saloonName = listItemView.findViewById(R.id.saloonName);
        TextView saloonSize = listItemView.findViewById(R.id.saloonSize);
        TextView saloonPrivacy = listItemView.findViewById(R.id.saloonPrivacy);

        Saloon saloon = saloonsList.get(position);

        saloonName.setText(saloon.getName());
        saloonPrivacy.setText(saloon.getStringPrivacy());
        saloonSize.setText(saloon.getStringUserInSaloonSize());

        return listItemView;
    }
}
