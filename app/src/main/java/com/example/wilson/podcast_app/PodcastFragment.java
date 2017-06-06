package com.example.wilson.podcast_app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;


/**
 * Created by andre on 23.04.2017.
 */

public class PodcastFragment extends Fragment implements MediaController.MediaPlayerControl {

    ListView list;
    MediaService mediaService;
    private SlidingUpPanelLayout mLayout;
    private static final String TAG = "ListFragment";
    SharedPreferences sharedPreferences;
    ImageButton playButton, skipPrev, skipNext, playMain;
    TextView nameText, timeText, timeFullText;
    ImageLoader imageLoader;
    ImageView img, imgSmall;
    Intent serviceIntent;
    SeekBar seekBar;
    private Handler myHandler = new Handler();
    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;
    private boolean musicBound = false;
    ServiceConnection musicConnection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.podcast_fragment, container, false);

        //Init
        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar2);
        timeText = (TextView) rootView.findViewById(R.id.textViewTime);
        timeFullText = (TextView) rootView.findViewById(R.id.textViewTimeFull);
        skipPrev = (ImageButton) rootView.findViewById(R.id.imageButtonSkipPrev);
        skipNext = (ImageButton) rootView.findViewById(R.id.imageButtonSkipNext);
        playMain = (ImageButton) rootView.findViewById(R.id.imageButtonPlayPause);
        img = (ImageView) rootView.findViewById(R.id.imageViewFrag);
        imgSmall = (ImageView) rootView.findViewById(R.id.imageViewSmall);
        nameText = (TextView) rootView.findViewById(R.id.name);
        playButton = (ImageButton) rootView.findViewById(R.id.playPod);

        sharedPreferences = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        mediaService = new MediaService();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layoutFragment);
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

        //Set image on imagebutton
        if (isPlaying()) {
            playButton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
        } else playButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
                if (isPlaying()) {
                    playButton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    playMain.setImageResource(R.drawable.ic_pause_black_24dp);
                } else {
                    playButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    playMain.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });
        if (isPlaying()) {
            playMain.setImageResource(R.drawable.ic_pause_black_24dp);
        } else playMain.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        playMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
                if (isPlaying()) {
                    playMain.setImageResource(R.drawable.ic_pause_black_24dp);
                    playButton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                } else {
                    playMain.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    playButton.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });


        //MediaConnection and bind
        musicConnection = new ServiceConnection(){

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MediaService.MusicBinder binder = (MediaService.MusicBinder)service;
                //get service
                mediaService = binder.getService();
                //pass list
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        };

        final ArrayList<Item> output = getArguments().getParcelableArrayList("valuesArray");

        list = (ListView) rootView.findViewById(R.id.ListView);
        //ListAdapter adapter = new ListAdapter(getActivity(), output);
        //list.setAdapter(adapter);
        ArrayList<String> titleList = new ArrayList<>();
        final ArrayList<String> linkList = new ArrayList<>();
        for (int i = 0; i < output.size(); i++) {
            titleList.add(output.get(i).getTitle());
            linkList.add(output.get(i).getLink());
        }
        if (titleList != null) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, titleList);
            arrayAdapter.notifyDataSetChanged();
            list.setAdapter(arrayAdapter);
        }
        if (linkList != null) {
            System.out.println(linkList);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position" , "" + position);
                serviceIntent = new Intent(getActivity(), MediaService.class);
                serviceIntent.putExtra("MediaLink", output.get(position).getLink());
                getActivity().startService(serviceIntent);
                getActivity().bindService(serviceIntent, musicConnection, Context.BIND_AUTO_CREATE);
                System.out.println("Starting Service from fragment");
                playButton.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                playMain.setImageResource(R.drawable.ic_pause_black_24dp);

                String podImg = sharedPreferences.getString("podcastImg", "");
                nameText.setText(output.get(position).getTitle());
                imageLoader.displayImage(podImg, img);
                imageLoader.displayImage(podImg, imgSmall);

                finalTime = getDuration();
                startTime = getCurrentPosition();
                if (oneTimeOnly == 0) {
                    oneTimeOnly = 1;
                }
                seekBar.setMax((int) finalTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                System.out.println("Podcast duration" + finalTime);
                System.out.println("Podcast start" + startTime);

                Log.d("Position" , "" + output.get(position).getTitle());
                Log.d("Position" , "" + output.get(position).getImg());
                Log.d("Position" , "" + output.get(position).getLink());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mediaService.seek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        Log.d("CustomAdapter", "PodcastFragment onCreateView successful");

        return rootView;
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = getCurrentPosition();
            timeText.setText(String.format(Locale.getDefault(), "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            timeFullText.setText(String.format(Locale.getDefault(), "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) finalTime)))
            );
            seekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 1000);
        }
    };



    @Override
    public void start() {
        mediaService.playPod();
    }

    @Override
    public void pause() {
        mediaService.pausePod();
    }

    @Override
    public int getDuration() {
        if (mediaService != null && musicBound) {
            return mediaService.getDur();
        } else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mediaService != null && musicBound) {
            return mediaService.getPos();
        } else return 0;
    }

    @Override
    public void seekTo(int pos) {
        mediaService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mediaService!=null && musicBound)
            return mediaService.isPlay();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaService != null && musicBound) {
            getActivity().unbindService(musicConnection);
            musicBound = false;
            System.out.println("unbind");
        }
    }
}
