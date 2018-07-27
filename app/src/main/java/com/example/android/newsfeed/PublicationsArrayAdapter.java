package com.example.android.newsfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * class PublicationsArrayAdapter
 *
 * Extends ArrayAdapter in order to create a custom array adapter
 */
public class PublicationsArrayAdapter extends ArrayAdapter<Publication> {
    /**
     * Constructor PublicationsArrayAdapter
     * @param context      - context of the application
     * @param publications - list of publications
     */
    PublicationsArrayAdapter(@NonNull Context context, @NonNull List<Publication> publications) {
        super(context, 0, publications);
    }

    /**
     * getView
     *
     * Creates/reuses views to create relevant views (which are currently need to be displayed)
     *
     * @param position    - position of the view
     * @param convertView - view to convert
     * @param parent      - parent view group
     * @return            - inflated view loaded with correct information
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get view to convert and create a view holder from the main activity
        View publicationsList = convertView;
        MainActivity.ViewHolder viewHolder;

        // If publication list is null then we need to inflate it with new
        if(publicationsList == null){
            // Inflate publication list
            publicationsList = LayoutInflater.from(getContext()).inflate(
                    R.layout.single_article, parent, false);

            // Connect all of the necessary views to the view holder views
            viewHolder = new MainActivity.ViewHolder();
            viewHolder.author = publicationsList.findViewById(R.id.author);
            viewHolder.articleName = publicationsList.findViewById(R.id.title);
            viewHolder.datePosted = publicationsList.findViewById(R.id.date);
            viewHolder.timePosted = publicationsList.findViewById(R.id.time);
            viewHolder.section = publicationsList.findViewById(R.id.section);

            // Apply view holder
            publicationsList.setTag(viewHolder);
        }
        // Use previous view holder
        else {

            viewHolder = (MainActivity.ViewHolder) publicationsList.getTag();
        }

        // Get current publication
        Publication currentPublication = getItem(position);

        // if current publication is not null then put all of the data into the view holder
        if (currentPublication != null) {
            viewHolder.author.setText(currentPublication.getAuthor());
            viewHolder.articleName.setText(currentPublication.getArticleName());
            viewHolder.datePosted.setText(currentPublication.getDatePosted());
            viewHolder.timePosted.setText(currentPublication.getTimePosted());
            viewHolder.section.setText(currentPublication.getSection());
        }

        return publicationsList;
    }
}
