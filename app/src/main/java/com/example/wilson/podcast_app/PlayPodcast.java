package com.example.wilson.podcast_app;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayPodcast extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    TextView text;
    TextView timeText;
    ImageView img;
    ImageLoader imageLoader;
    SeekBar seekBar;
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;
    private Handler myHandler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_podcast);
        Button btnStart = (Button) findViewById(R.id.btnSrt);
        Button btnSkipF = (Button) findViewById(R.id.btnSkipF);
        Button btnSkipB = (Button) findViewById(R.id.btnSkipB);
        Button btnPause = (Button) findViewById(R.id.button2);
        img = (ImageView) findViewById(R.id.imageView2);
        text = (TextView) findViewById(R.id.textView);
        timeText = (TextView) findViewById(R.id.time_textview);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        Bundle extras = getIntent().getExtras();
        String mediaStream;
        String mediaName;
        String mediaPic;

        mediaStream = extras.getString("MediaStream");
        mediaName = extras.getString("MediaName");
        mediaPic = extras.getString("MediaPic");

        if (mediaName != null){
            text.setText(mediaName);
        }
        imageLoader.displayImage(mediaPic, img);
        mediaPlayer(mediaStream);

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
                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

    }

    private void mediaPlayer(String podcast){
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(podcast);
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
        seekBar.setProgress((int) startTime);
        myHandler.postDelayed(UpdateSongTime, 100);
    }
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            timeText.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}
