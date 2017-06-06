package com.example.wilson.podcast_app;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MediaService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer;
    private String position;
    private final IBinder musicBind = new MusicBinder();
    private ArrayList<String> podLinkList;

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
                    position = intent.getExtras().getString("MediaLink");

                    mediaPlayer.reset();
                    if (!mediaPlayer.isPlaying()) {
                        try {
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(position);
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
        return START_STICKY;
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
        stopSelf();
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
        System.out.println(mediaPlayer.getDuration());
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
    public void setLinkList(ArrayList<String> podList) {
        podLinkList = podList;
    }
}
