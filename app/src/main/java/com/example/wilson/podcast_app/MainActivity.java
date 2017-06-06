package com.example.wilson.podcast_app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncInterface {

    EditText editText;
    TextView podcastName;
    ImageView imgSearch;
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<iTunesItem> itunesList, itunesList2;
    String text = "", trackName, Podcast_url, imageUri, podUrl, podID;
    GridView gridView;
    Button btnStore;
    private SlidingUpPanelLayout mLayout;
    private static final String TAG = "MainActivity";
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnSearch = (Button) findViewById(R.id.search_btn);
        editText = (EditText) findViewById(R.id.editText);
        podcastName = (TextView) findViewById(R.id.textViewPodcastName);
        gridView = (GridView) findViewById(R.id.gridView);
        btnStore = (Button) findViewById(R.id.btnSave);
        imgSearch = (ImageView) findViewById(R.id.imageViewSearch);
        getDataBase();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layoutMain);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        //Removes the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        //Todo: trackname and the other strings are null on the first click, but not the second
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(MainActivity.this, sliderTest.class);
                //startActivity(i);
                text = editText.getText().toString();
                podcastSearch pS = new podcastSearch();
                pS.execute();

                btnStore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBHelper dbHelper = new DBHelper(MainActivity.this);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("id", podID);
                        values.put("name", trackName);
                        values.put("image", imageUri);
                        values.put("url", Podcast_url);

                        long newRowId = db.insertWithOnConflict("PodCasts", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        System.out.println("In DataBase");
                        getDataBase();
                        db.close();
                    }
                });

                //System.out.println(imageUri);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                itunesList2 = new ArrayList<>();
                itunesList2.addAll(dbHelper.getAllItems());
                iTunesItem item;
                item = itunesList2.get(position);
                System.out.println(item.getUrl());
                String urlParam = item.getUrl();

                //dbHelper.removeSingleContact(item.getName());

                SharedPreferences sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("podcastImg", item.getImg());
                editor.apply();
                asyncPodcast(urlParam);
                dbHelper.close();
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    public void asyncPodcast(String urlParam) {
        getPodcast podcast = new getPodcast();
        podcast.asyncInterface = this;
        podcast.execute(urlParam);
    }
    //Set podcasts from async to list
    @Override
    public void processFinish(ArrayList<Item> output) {
        System.out.println("processFinish main");
        System.out.println("main" + output);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("valuesArray", output);
        PodcastFragment fragment = new PodcastFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void getDataBase() {
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        itunesList = new ArrayList<iTunesItem>();

        itunesList.addAll(dbHelper.getAllItems());
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, itunesList);
        gridView.setAdapter(adapter);
        dbHelper.close();
    }

    private class podcastSearch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

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
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response2;
        }

        @Override
        protected void onPostExecute(String searchResp) {
            super.onPostExecute(searchResp);

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(searchResp);
                JSONArray itunesResults = jsonObject.getJSONArray("results");

                for (int i = 0; i < itunesResults.length(); i++) {
                    JSONObject c = itunesResults.getJSONObject(i);

                    Podcast_url = c.getString("feedUrl");
                    imageUri = c.getString("artworkUrl600");
                    trackName = c.getString("trackName");
                    podID = c.getString("trackId");
                    System.out.println(Podcast_url);
                    System.out.println(imageUri);
                    System.out.println(trackName);
                }
                System.out.println(searchResp);
            } catch (JSONException e) {
                e.printStackTrace();
                podcastName.setText("Podcast not found");
            }

            podcastName.setText(trackName);
            imageLoader.displayImage(imageUri, imgSearch);
        }
    }
}