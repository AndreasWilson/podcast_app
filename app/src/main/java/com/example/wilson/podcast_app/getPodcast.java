package com.example.wilson.podcast_app;

import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;
import android.util.Xml;

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
    protected ArrayList<Item> doInBackground(String... urlParam) {

        String urlString = urlParam[0];

        URL url = null;
        try {
            url = new URL(urlString);
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

        /*for (int i = 0; i < items.size(); i++) {
            item2.add(items.get(i).getTitle());
            item2.add(items.get(i).getLink());
        }*/

        Log.d("ArrayList: ", "" + item2);
        if (asyncInterface != null) {
            asyncInterface.processFinish(item);
            System.out.println("asyncinterface not null");
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
                        item = new Item(Parcel.obtain());
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
