package com.example.android.newsfeed;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

/**
 * class PublicationsLoader
 *
 * extends AsyncTaskLoader in order to do networking on background
 */
public class PublicationsLoader extends AsyncTaskLoader<List<Publication>> {

    // url : url of the desired json
    private String url;

    /**
     * Constructor EarthquakeLoader
     *
     * @param context - context of the app
     * @param url     - url of the desired json
     */
    PublicationsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    /**
     * onStartLoading (extended from AsyncTaskLoader)
     *
     * Forces the load to begin
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * loadInBackground
     *
     * Job done in the background thread, fetching data from the provided url
     *
     * @return - List of the publications fetched from the provided url or null if the url is null
     */
    @Override
    public List<Publication> loadInBackground() {
        if(url == null){
            return null;
        }

        return Utils.fetchPublications(url);
    }
}
