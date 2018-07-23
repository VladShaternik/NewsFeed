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

public final class Utils {

    Utils() {
    }

    public static List<Publication> fetchPublications(String urlStr){
        List<Publication> publications = new ArrayList<>();

        URL url = createUrl(urlStr);

        String jsonStr = null;

        try {
            jsonStr = makeHttpRequest(url);
        } catch (IOException e) {
            //TODO think about this log
            Log.e("Utils", "Problem retrieving the publications JSON results.", e);
        }

        publications = parseJson(jsonStr);

        return publications;
    }

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

            if(httpURLConnection.getResponseCode() == 200){
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                //TODO display error code to user
                Log.e("Utils", "Error response code: " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            //TODO display this error to user (empty screen)
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

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Utils", "Problem building the URL ", e);
        }
        return url;
    }

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

                publications.add(new Publication(author, title, date, time));
            }
        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the publications JSON results", e);
        }

        return publications;
    }

    private static String getAuthor(String webTitle) {
        String author = "Unknown author";

        int index = webTitle.indexOf("|");

        if (index != -1) {
            author = webTitle.substring(index + 2);
        }

        return author;
    }

    private static String getTitle(String webTitle) {
        String title = webTitle;

        int index = webTitle.indexOf("|");

        if (index != -1) {
            title = webTitle.substring(0, index);
        }

        return title;
    }

    private static String getDate(String dateUnformatted) {
        Date date = new Date(0);

        SimpleDateFormat formatParseIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            date = formatParseIso.parse(dateUnformatted);
        } catch (ParseException e) {
            Log.e("Utils", "Problem parsing the Date", e);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        return dateFormat.format(date);
    }

    private static String getTime(String dateUnformatted) {
        Date date = new Date(0);

        SimpleDateFormat formatParseIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            date = formatParseIso.parse(dateUnformatted);
        } catch (ParseException e) {
            Log.e("Utils", "Problem parsing the Time", e);
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");

        return timeFormat.format(date);
    }
}
