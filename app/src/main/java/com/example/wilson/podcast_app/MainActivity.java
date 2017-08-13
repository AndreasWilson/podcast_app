package com.example.wilson.podcast_app;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.wilson.podcast_app.Adapter.ImageAdapter;
import com.example.wilson.podcast_app.Adapter.SearchListAdapter;
import com.example.wilson.podcast_app.AsyncInterface.AsyncInterface;
import com.example.wilson.podcast_app.AsyncTasks.getPodcast;
import com.example.wilson.podcast_app.DataBase.DBHelper;
import com.example.wilson.podcast_app.Objects.Item;
import com.example.wilson.podcast_app.Objects.iTunesItem;
import com.example.wilson.podcast_app.Objects.iTunesSearch;
import com.nostra13.universalimageloader.core.ImageLoader;
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

    TextView podcastName;
    ImageView imgSearch;
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<iTunesItem> itunesList, itunesList2;
    ArrayList<iTunesSearch> iTunesList;
    GridView gridView;
    ListView searchList;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        getDataBase();

        //Removes the keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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
    public void putInBD(int position) {
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", iTunesList.get(position).getPodcastID());
        values.put("name", iTunesList.get(position).getName());
        values.put("image", iTunesList.get(position).getImageUrl());
        values.put("url", iTunesList.get(position).getPodcastUrl());

        db.insertWithOnConflict("PodCasts", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        System.out.println("In DataBase");
        getDataBase();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                podcastSearch pS = new podcastSearch();
                pS.execute(query);

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 100, 100, 100)));
                dialog.setContentView(R.layout.search_list_view);
                searchList = (ListView) dialog.findViewById(R.id.searchList);
                System.out.println("Dialog");
                dialog.setCancelable(true);
                dialog.show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
        itunesList = new ArrayList<>();

        itunesList.addAll(dbHelper.getAllItems());
        ImageAdapter adapter = new ImageAdapter(MainActivity.this, itunesList);
        gridView.setAdapter(adapter);
        dbHelper.close();
    }

    private class podcastSearch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... query) {

            String termText = query[0];
            String response2 = "";
            URL url = null;
            try {
                url = new URL("https://itunes.apple.com/search?term=" + termText +"&media=podcast&limit=10");
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

            iTunesList = new ArrayList<>();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(searchResp);
                JSONArray itunesResults = jsonObject.getJSONArray("results");

                for (int i = 0; i < itunesResults.length(); i++) {
                    iTunesSearch iTunes = new iTunesSearch();
                    JSONObject c = itunesResults.getJSONObject(i);

                    iTunes.setName(c.optString("trackName"));
                    iTunes.setPodcastUrl(c.optString("feedUrl"));
                    iTunes.setImageUrl(c.optString("artworkUrl600"));
                    iTunes.setPodcastID(c.optString("trackId"));

                    System.out.println(iTunes.getName());

                    iTunesList.add(iTunes);
                }
                System.out.println(searchResp);
            } catch (JSONException e) {
                e.printStackTrace();
                podcastName.setText("Podcast not found");
            }
            if (iTunesList != null) {
                SearchListAdapter adapter = new SearchListAdapter(MainActivity.this, iTunesList);
                searchList.setAdapter(adapter);
                System.out.println("itunesList not null");
            }
        }
    }
}