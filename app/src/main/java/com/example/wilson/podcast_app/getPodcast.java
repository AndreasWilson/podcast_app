package com.example.wilson.podcast_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;

import com.example.wilson.podcast_app.Interface.AsyncInterface;
import com.example.wilson.podcast_app.Item;
import com.example.wilson.podcast_app.MainActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by andre on 18.04.2017.
 */

public class getPodcast extends AsyncTask<String, Void, ArrayList<Item>> {
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<String> item2 = new ArrayList<>();
    public AsyncInterface asyncInterface = null;

    //SharedPreferences sharedPreferences = getSharedPreferences("URLpref", Context.MODE_PRIVATE);
    //String podUrl = sharedPreferences.getString("podURL", null);

    @Override
    protected ArrayList<Item> doInBackground(String... params) {

        URL url = null;
        try {
            url = new URL("http://funhaus.roosterteeth.com/show/dude-soup/feed/mp3");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            int responseCode = http.getResponseCode();
            Log.d("ResponseCode: ", "" + responseCode);

            http.connect();
            InputStream stream = http.getInputStream();

            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(stream, null);
            items = parseMethod(parser);

            stream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    @Override
    protected void onPostExecute(ArrayList<Item> item) {

        for (int i = 0; i < items.size(); i++) {
            item2.add(items.get(i).getTitle());
        }

        Log.d("ArrayList: ", "" + item2);
        if (asyncInterface != null) {
            asyncInterface.processFinish(item2);
        }
    }

    public ArrayList<Item> parseMethod(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Item> items = null;
        int eventType = parser.getEventType();
        Item item = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    items = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("item")){
                        item = new Item();
                    } else if (item != null) {
                        if (name.equals("title")) {
                            item.setTitle(parser.nextText());
                        } else if (name.equals("description")) {
                            item.setDesc(parser.nextText());
                        } else if (name.equals("itunes:image")) {
                            item.setImg(parser.getAttributeValue(null, "href"));
                        } else if (name.equals("enclosure")) {
                            item.setLink(parser.getAttributeValue(null, "url"));
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("item") && item != null) {
                        items.add(item);
                    }
            }
            eventType = parser.next();
        }
        return items;
    }
}
