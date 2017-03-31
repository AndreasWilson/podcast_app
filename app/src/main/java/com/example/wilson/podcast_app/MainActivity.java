package com.example.wilson.podcast_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.delay;
import static android.R.attr.startDelay;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView podcastName;
    ListView list;
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<iTunesItem> itunesList;
    String text = "";
    String Podcast_url;
    String imageUri;
    String trackName;
    String podUrl;
    ImageView imageView;
    GridView gridView;
    Button btnStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        Button btnSearch = (Button) findViewById(R.id.search_btn);
        list = (ListView) findViewById(R.id.list123);
        editText = (EditText) findViewById(R.id.editText);
        //imageView = (ImageView) findViewById(R.id.imageView);
        //podcastName = (TextView) findViewById(R.id.podcastname);
        gridView = (GridView) findViewById(R.id.gridView);
        btnStore = (Button) findViewById(R.id.btnSave);


        //Removes the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            //InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            InputMethodManager imm = (InputMethodManager) getSystemService(
                    INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text = editText.getText().toString();
                podcastSearch pS = new podcastSearch();
                pS.execute();
                //podcastName.setText(trackName);
                //gridView.setAdapter(new ImageAdapter(MainActivity.this, imageUri));
                btnStore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*DBHelper dbHelper = new DBHelper(MainActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("name", trackName);
                        values.put("image", imageUri);
                        values.put("url", Podcast_url);

                        long newRowId = db.insert("PodCasts", null, values);
                        System.out.println(newRowId + "In DataBase");*/
                        getDataBase();
                    }
                });

                System.out.println(imageUri);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                podcastGet pod = new podcastGet();
                pod.execute();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position" , "" + position);

                Intent i = new Intent(MainActivity.this, PlayPodcast.class);
                i.putExtra("MediaStream", items.get(position).getLink());
                i.putExtra("MediaName", items.get(position).getTitle());
                i.putExtra("MediaPic", items.get(position).getImg());
                startActivity(i);
            }
        });
    }
    private void getDataBase() {
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        itunesList = new ArrayList<iTunesItem>();

        itunesList = dbHelper.getAllItems();
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, itunesList);
        gridView.setAdapter(adapter);
    }

    private class podcastSearch extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            String response2 = "";
            URL url = null;
            try {
                url = new URL("https://itunes.apple.com/search?term=" + text);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                int response = http.getResponseCode();
                Log.d("Response: ", "" + response);
                http.connect();


                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response2 += line;
                }

                JSONObject jsonObject = new JSONObject(response2);
                JSONArray itunesResults = jsonObject.getJSONArray("results");

                for (int i = 0; i < itunesResults.length(); i++) {
                    JSONObject c = itunesResults.getJSONObject(i);

                    Podcast_url = c.getString("feedUrl");
                    imageUri = c.getString("artworkUrl600");
                    trackName = c.getString("trackName");
                    System.out.println(Podcast_url);
                    System.out.println(imageUri);
                }
                System.out.println(response2);


                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException Je) {
                Je.printStackTrace();
            }

            return null;
        }
    }

    private class podcastGet extends AsyncTask<String, Void, ArrayList<Item>> {
        ArrayList<String> item2 = new ArrayList<>();

        @Override
        protected ArrayList<Item> doInBackground(String... params) {

            URL  url = null;
            try {
                url = new URL(podUrl);
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, item2);
            arrayAdapter.notifyDataSetChanged();
            list.setAdapter(arrayAdapter);
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