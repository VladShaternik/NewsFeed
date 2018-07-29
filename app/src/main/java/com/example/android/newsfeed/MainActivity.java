package com.example.android.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Publication>> {
    private PublicationsArrayAdapter arrayAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the TextView to display empty state messages
        mEmptyStateTextView = findViewById(R.id.empty_view);

        // Load custom array adapter with application context and empty array list of publications
        arrayAdapter = new PublicationsArrayAdapter(getApplicationContext(), new ArrayList<Publication>());

        // Find the ListView to display publications in it and set adapter to it
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(arrayAdapter);

        // Provide an OnItemClickListener in order to open links of the publications when they are
        // clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get clicked publication's object
                Publication clickedPublication = arrayAdapter.getItem(position);

                // If Publication object is not null then load created Uri with the link of the
                // publication
                Uri uri = null;
                if (clickedPublication != null) {
                    uri = Uri.parse(clickedPublication.getStoryLink());
                }

                // Create an intent to open the link of the publication
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader(0, null, this);
        } else {
            // Display error, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // If the adapter is empty sets the mEmptyStateTextView view to be displayed
            listView.setEmptyView(mEmptyStateTextView);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    /**
     * onCreateOptionsMenu
     *
     * This method initialize the contents of the Activity's options menu.
     *
     * @param menu - menu to inflate
     * @return     - whether the menu was inflated
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    /**
     * onOptionsItemSelected
     *
     * Open settings activity when item in the Options Menu is selected.
     *
     * @param item - item clicked
     * @return     - whether action was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onCreateLoader
     *
     * When Loader is created it returns custom loader with url of the desired json
     *
     * @param id   - id of the loader
     * @param args - a mapping from String keys to various Parcelable values
     * @return     - custom Loader
     */
    @Override
    public Loader<List<Publication>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String publicationsAmt = sharedPrefs.getString(
                getString(R.string.settings_publications_amt_key),
                getString(R.string.settings_publications_amt_default));

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse("https://content.guardianapis.com/search");

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key", "87ac2707-711e-4c4c-8c31-8c33b760e15c");
        uriBuilder.appendQueryParameter("page-size", publicationsAmt);
        uriBuilder.appendQueryParameter("order-by", orderBy);

        return new PublicationsLoader(this, uriBuilder.toString());
    }

    /**
     * onLoadFinished
     *
     * When load is finished adds loaded publications to the array adapter in order to display it
     *
     * @param loader - custom loader
     * @param data   - list of publications
     */
    @Override
    public void onLoadFinished(Loader<List<Publication>> loader, List<Publication> data) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // If the adapter is empty sets the mEmptyStateTextView view to be displayed
        mEmptyStateTextView.setText(R.string.no_publications_found);

        // If we got data then add it to the array adapter and set empty state text to empty
        if (data != null && !data.isEmpty()) {
            arrayAdapter.addAll(data);
            mEmptyStateTextView.setText("");
        }
    }

    /**
     * onLoaderReset
     *
     * When loader resets - clear the array adapter
     *
     * @param loader - custom loader
     */
    @Override
    public void onLoaderReset(Loader<List<Publication>> loader) {
        arrayAdapter.clear();
    }


    /**
     * class ViewHolder
     *
     * Using a ViewHolder pattern in order to avoid multiple calls of the findById functions, and
     * decrease memory usage
     */
    static class ViewHolder {
        TextView author;      // Text view for the name of the author of the publication
        TextView articleName; // Text view for the name of the article of the publication
        TextView datePosted;  // Text view for the date when the publication was posted
        TextView timePosted;  // Text view for the time when the publication was posted
        TextView section;     // Text view for the news section of the publication
    }
}
