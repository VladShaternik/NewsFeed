package com.example.android.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Publication>> {

    String url;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Publication> loadInBackground() {
        if(url == null){
            return null;
        }

        return Utils.fetchPublications(url);
    }
}
