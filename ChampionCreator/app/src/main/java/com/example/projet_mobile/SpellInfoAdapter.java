package com.example.projet_mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/* CLASS FOR A CUSTOM ADAPTER FOR THE LISTVIEW AND SEARCHVIEW */
public class SpellInfoAdapter extends ArrayAdapter<Spell> {
    private final Context context;
    private final int ressource; // layout used
    public SpellInfoAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Spell> objects) {
        super(context, resource, objects);
        this.context = context;
        this.ressource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            // precise on what page the listView should be
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(ressource, parent, false);

            // Element for each row of the listView
            ImageView spellImage = convertView.findViewById(R.id.spellImage);
            TextView championName = convertView.findViewById(R.id.spell);
            TextView spellName = convertView.findViewById(R.id.spellName);

            if (getItem(position).getSpellImageName() == null)
                spellImage.setImageResource(R.drawable.item); // default image
            else // set image from url to the imageView
            {
                if (getItem(position).getCategory().equals("P"))
                    Glide.with(context).load("https://ddragon.leagueoflegends.com/cdn/14.4.1/img/passive/" + getItem(position).getSpellImageName()).into(spellImage);
                else
                    Glide.with(context).load("https://ddragon.leagueoflegends.com/cdn/14.4.1/img/spell/" + getItem(position).getSpellImageName()).into(spellImage);
            }

            championName.setText(getItem(position).getChampionName());
            spellName.setText(getItem(position).getSpellName());

            return convertView; // return listView to the page
        } catch (Exception e) {
            return convertView;
        }
    }

}