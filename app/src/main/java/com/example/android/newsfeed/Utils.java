package com.example.android.newsfeed;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * class Utils
 *
 * Set of utility functions
 */
public final class Utils {

    /**
     * Constructor Utils
     */
    Utils() {
    }

    /**
     * fetchPublications
     *
     * Fetch list of publications from the json which is located by the given url string
     *
     * @param urlStr - url string where desired json is located
     * @return       - list of publications
     */
    public static List<Publication> fetchPublications(String urlStr){
        List<Publication> publications;
        URL url = createUrl(urlStr);

        String jsonStr = null;
        try {
            jsonStr = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Utils", "Problem retrieving the publications JSON results.", e);
        }

        publications = parseJson(jsonStr);

        return publications;
    }

    /**
     * makeHttpRequest
     *
     * Get json string from the given url
     *
     * @param url - url where json located
     * @return    - json string
     * @throws IOException - for http connection
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.connect();

            // If the response code is good then we can get and json string
            if(httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e("Utils", "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("Utils", "Problem retrieving the publications JSON results.", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * createUrl
     *
     * Converts string to URL object
     *
     * @param stringUrl - string Url
     * @return          - object Url
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Utils", "Problem building the URL ", e);
        }

        return url;
    }

    /**
     * readFromStream
     *
     * Read json string from stream using InputStreamReader and BufferedReader in order to minimise
     * the time the reading takes
     *
     * @param inputStream - input stream (from the http request)
     * @return - json sting
     * @throws IOException - for BufferedReader's readLine() method
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder jsonStr = new StringBuilder();

        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null){
                jsonStr.append(line);
                line = bufferedReader.readLine();
            }
        }

        return jsonStr.toString();
    }

    /**
     * parseJson
     *
     * Get needed info from the json
     *
     * @param jsonToParse - string json
     * @return - list of filled-with-information Publications
     */
    private static List<Publication> parseJson(String jsonToParse) {
        if (TextUtils.isEmpty(jsonToParse)) {
            return null;
        }

        ArrayList<Publication> publications = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(jsonToParse);
            JSONArray publicationsList = root.optJSONObject("response").getJSONArray("results");

            for (int i = 0; i < publicationsList.length(); i++) {
                String webTitle = publicationsList.getJSONObject(i).optString("webTitle");
                String title = getTitle(webTitle);
                String author = getAuthor(webTitle);
                String date = getDate(publicationsList.getJSONObject(i).optString("webPublicationDate"));
                String time = getTime(publicationsList.getJSONObject(i).optString("webPublicationDate"));
                String section = publicationsList.getJSONObject(i).optString("sectionName");
                String storyLink = publicationsList.getJSONObject(i).optString("webUrl");

                publications.add(new Publication(author, title, date, time, section, storyLink));
            }
        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the publications JSON results", e);
        }

        return publications;
    }

    /**
     * getAuthor
     *
     * Get author which name could be located in the title of the publication
     *
     * @param webTitle - title of the publication
     * @return         - author name or "Unknown author"
     */
    private static String getAuthor(String webTitle) {
        String author = "Unknown author";

        // Get index of the symbol after which author name is displayed
        int index = webTitle.indexOf("|");

        if (index != -1) {
            author = webTitle.substring(index + 2);
        }

        return author;
    }

    /**
     * getTitle
     *
     * Get title after which author name could be located (remove author name)
     *
     * @param webTitle - title of the publication
     * @return         - title of the publication (without possible author name)
     */
    private static String getTitle(String webTitle) {
        String title = webTitle;

        // Get index of the symbol until which the title is displayed
        int index = webTitle.indexOf("|");

        if (index != -1) {
            title = webTitle.substring(0, index);
        }

        return title;
    }

    /**
     * getDate
     *
     * Formats the string date to the "MM/dd/yyyy zzz" format and to the time zone where the device
     * is located
     *
     * @param dateUnformatted - unformatted date string from json
     * @return                - formatted date string
     */
    private static String getDate(String dateUnformatted) {
        Date date;

        SimpleDateFormat formatParseIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        formatParseIso.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            date = formatParseIso.parse(dateUnformatted);
        } catch (ParseException e) {
            Log.e("Utils", "Problem parsing the Date", e);
            return "Unknown date";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy zzz", Locale.US);
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat.format(date);
    }

    /**
     * getTime
     *
     * Formats the string time to the "h:mm a" format and to the time zone where the device
     * is located
     *
     * @param dateUnformatted - unformatted date string from json
     * @return                - formatted time string
     */
    private static String getTime(String dateUnformatted) {
        Date date;

        SimpleDateFormat formatParseIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        formatParseIso.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            date = formatParseIso.parse(dateUnformatted);
        } catch (ParseException e) {
            Log.e("Utils", "Problem parsing the Time", e);
            return "Unknown time";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        timeFormat.setTimeZone(TimeZone.getDefault());

        return timeFormat.format(date);
    }
}
