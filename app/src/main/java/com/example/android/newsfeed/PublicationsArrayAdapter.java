package com.example.android.newsfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PublicationsArrayAdapter extends ArrayAdapter<Publication> {
    public PublicationsArrayAdapter(@NonNull Context context, @NonNull List<Publication> publications) {
        super(context, 0, publications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View publicationsList = convertView;
        MainActivity.ViewHolder viewHolder;

        if(publicationsList == null){
            publicationsList = LayoutInflater.from(getContext()).inflate(
                    R.layout.single_article, parent, false);

            viewHolder = new MainActivity.ViewHolder();

            viewHolder.author = publicationsList.findViewById(R.id.author);
            viewHolder.articleName = publicationsList.findViewById(R.id.title);
            viewHolder.datePosted = publicationsList.findViewById(R.id.date);
            viewHolder.timePosted = publicationsList.findViewById(R.id.time);

            publicationsList.setTag(viewHolder);
        }
        else {
            viewHolder = (MainActivity.ViewHolder) publicationsList.getTag();
        }

        Publication currentPublication = getItem(position);

        viewHolder.author.setText(currentPublication.getAuthor());
        viewHolder.articleName.setText(currentPublication.getArticleName());
        viewHolder.datePosted.setText(currentPublication.getDatePosted());
        viewHolder.timePosted.setText(currentPublication.getTimePosted());

        return publicationsList;
    }
}
