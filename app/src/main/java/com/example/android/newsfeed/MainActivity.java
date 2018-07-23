package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Publication>>{
    private static final String PUBLICATIONS_URL = "https://content.guardianapis.com/search?page-size=200&api-key=87ac2707-711e-4c4c-8c31-8c33b760e15c";
    private PublicationsArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arrayAdapter = new PublicationsArrayAdapter(getApplicationContext(), new ArrayList<Publication>());

        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(arrayAdapter);

        LoaderManager loaderManager = getLoaderManager();

        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<List<Publication>> onCreateLoader(int id, Bundle args) {
        return new EarthquakeLoader(this, PUBLICATIONS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Publication>> loader, List<Publication> data) {
        if (data != null && !data.isEmpty()) {
            arrayAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Publication>> loader) {
        arrayAdapter.clear();
    }


    static class ViewHolder {
        TextView author;
        TextView articleName;
        TextView datePosted;
        TextView timePosted;
    }
}
