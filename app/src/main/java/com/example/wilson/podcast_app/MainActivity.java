package com.example.wilson.podcast_app;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    public static int oneTimeOnly = 0;
    private double startTime = 0;
    private double finalTime = 0;
    ListView list;
    TextView text;
    ArrayList<Item> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        Button btnStart = (Button) findViewById(R.id.btnStart);
        Button btnPause = (Button) findViewById(R.id.btnPause);
        list = (ListView) findViewById(R.id.list123);
        text = (TextView) findViewById(R.id.textView);
        seekBar = (SeekBar) findViewById(R.id.seekBar3);
        seekBar.setClickable(false);
        mediaPlayer = new MediaPlayer();


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

                mediaPlayer.reset();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(items.get(position).getLink());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekBar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }

            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    seekBar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });
    }

    private class podcastGet extends AsyncTask<String, Void, ArrayList<Item>> {
        ArrayList<String> item2 = new ArrayList<>();

        @Override
        protected ArrayList<Item> doInBackground(String... params) {

            URL  url = null;
            try {
                url = new URL("http://yogpod.libsyn.com/rss");
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                int responseCode = http.getResponseCode();
                Log.d("ResponseCode: ", "" + responseCode);

                http.connect();
                InputStream stream = http.getInputStream();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(stream, null);

                items = parseMethod(parser);

                String text = "";

                for (Item item:items) {
                    text += "Title: " + item.getLink() + "\n"; //+ " Description: " + item.getDesc() + " Img: " + item.getImg() + " URL: " + item.getLink();
                }

                System.out.println("TEST: " + text);

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
            text.setText("Podcasts!");
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