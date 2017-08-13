package com.example.wilson.podcast_app;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.wilson.podcast_app.Objects.Item;

import java.io.IOException;
import java.util.ArrayList;

public class MediaService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer;
    private int position;
    private final IBinder musicBind = new MusicBinder();
    private ArrayList<Item> podLinkList;
    private boolean isPrepared = false;

    //onCreate
    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (mediaPlayer != null) {
                isPrepared = false;
                setSong(intent.getExtras().getInt("Position"));
                podLinkList = intent.getParcelableArrayListExtra("LinkArrays");
                System.out.println("Position: " + position);
                startPod();
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
    }
    public class MusicBinder extends Binder {
        MediaService getService() {
            return MediaService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.reset();
        //playNext();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MediaPlayer", "Error");
        mediaPlayer.reset();
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playPod();
        isPrepared = true;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
    public void playPod() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    public void stopPod() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }
    public void pausePod() {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            } else if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }
    public int getPos() {
        return mediaPlayer.getCurrentPosition();
    }
    public int getDur() {
        return mediaPlayer.getDuration();
    }
    public void seek(int pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos);
        }
    }
    public boolean isPlay(){
        return mediaPlayer.isPlaying();
    }
    public boolean isPrepared() {
        return isPrepared;
    }
    public void playNext() {
        position++;
        if (position >= podLinkList.size()) {
            position = 0;
        }
        System.out.println("Position: " + position);
        startPod();
    }
    public void playPrev() {
        position--;
        if (position < 0) {
            position = podLinkList.size() - 1;
        }
        System.out.println("Position: " + position);
        startPod();
    }
    public void setSong(int songIndex){
        position = songIndex;
    }
    public String getTitle() {
        return podLinkList.get(position).getTitle();
    }
    public void startPod() {
        mediaPlayer.reset();
        isPrepared = false;
        if (!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(podLinkList.get(position).getLink());
                mediaPlayer.prepareAsync();
                System.out.println("Service start");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
