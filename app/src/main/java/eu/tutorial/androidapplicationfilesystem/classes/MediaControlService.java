package eu.tutorial.androidapplicationfilesystem.classes;

import static eu.tutorial.androidapplicationfilesystem.classes.ServiceNotification.ACTION_NEXT;
import static eu.tutorial.androidapplicationfilesystem.classes.ServiceNotification.ACTION_PLAY;
import static eu.tutorial.androidapplicationfilesystem.classes.ServiceNotification.ACTION_PREVIOUS;
import static eu.tutorial.androidapplicationfilesystem.classes.ServiceNotification.CHANNEL_ID_1;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import java.io.File;

import eu.tutorial.androidapplicationfilesystem.R;
import eu.tutorial.androidapplicationfilesystem.activities.MainActivity;

public class MediaControlService extends Service implements AudioManager.OnAudioFocusChangeListener{
    MediaPlayer mp = new MediaPlayer();
    String musicPath = "none";
    String mediaSessionTag = "mediaSession";
    Boolean mpAvailable = false;
    Playlist sourcePlaylist = null;
    Integer sourceIndex = null;

    MediaSession mediaSession;
    NotificationManager notificationManager;

    AudioManager audioManager;

    AudioFocusRequest focusRequest;

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                //resume playback
                if (mp != null && !mp.isPlaying()) {
                    mp.start();
                    setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                //stop playback
                if (mp != null && mp.isPlaying()) {
                    mp.pause();
                    setPlaybackState(PlaybackStateCompat.STATE_STOPPED);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                //pause playback
                if (mp != null && mp.isPlaying()) {
                    mp.pause();
                    setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //lower volume
                if (mp != null && mp.isPlaying()) {
                    mp.setVolume(0.1f, 0.1f);
                }
                break;
           // case AudioManager.AUDIOFOCUS_
           // case AudioManager.STREAM_MUSI
        }
    }


    class MyServiceBinder extends Binder{
        public MediaControlService getService(){
            return MediaControlService.this;
        }
    }

    private final IBinder mBinder = new MyServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getAction()!=null){

            if(intent.getAction().equals(ACTION_PREVIOUS)){
                playPrev();
            }

            if(intent.getAction().equals(ACTION_NEXT)){
                playNext();
            }

            if(intent.getAction().equals(ACTION_PLAY)){
                if(mp.isPlaying()){
                    pauseMedia();
                    mediaSession.setActive(true);
                    //broadcast("isPlaying",true, "SOURCECHANGED");
                }else{
                    playMedia();
                    mediaSession.setActive(true);
                    //broadcast("isPlaying",false, "SOURCECHANGED");
                }
            }
        }

        return START_NOT_STICKY;  //START_... will change what action is taken when the service is killed  NOT_STICKY means the service will not be restarted

    }

    @Override
    public void onCreate() {//Could put the same things as in onStartCommand, but it will only create the notification once
        actionNullNotification();
        mediaSession = new MediaSession(this, mediaSessionTag);
        PlaybackState playbackState;
        //mediaSession.setPlaybackState(PlaybackState.STATE_PAUSED);
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                System.out.println("Play");
                playMedia();
                setPlaybackState(PlaybackState.STATE_PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                System.out.println("Pause");
                pauseMedia();
                setPlaybackState(PlaybackState.STATE_PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                System.out.println("Next");
                //setPlaybackState(PlaybackState.STATE_PLAYING);
                playNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                System.out.println("Previous");
                //setPlaybackState(PlaybackState.STATE_PLAYING);
                playPrev();
            }

            @Override
            public void onStop() {
                super.onStop();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
                String action = mediaButtonEvent.getAction();
                if (action != null) {
                    if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
                        KeyEvent keyEvent = (KeyEvent) mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                        if (keyEvent != null) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                switch (keyEvent.getKeyCode()) {
                                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                                        if(mp.isPlaying()){
                                            pauseMedia();
                                            mediaSession.setActive(true);
                                            //broadcast("isPlaying",true, "SOURCECHANGED");
                                        }else{
                                            playMedia();
                                            mediaSession.setActive(true);
                                            //broadcast("isPlaying",false, "SOURCECHANGED");
                                        }
                                        break;
                                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                                        playNext();
                                        break;
                                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                                        playPrev();
                                        //handle previous button press
                                        break;
                                }
                            }
                        }
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent);
            }
        });
        mediaSession.setActive(true);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        super.onCreate();
    }

    private void setMediaSessionMetadata(String title, String artist, Bitmap bitmap){
        MediaMetadata.Builder metadataBuilder = new MediaMetadata.Builder();
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, title);
        metadataBuilder.putString(MediaMetadata.METADATA_KEY_ARTIST, artist);
        metadataBuilder.putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmap);
        mediaSession.setMetadata(metadataBuilder.build());
    }

    private void setPlaybackState(int state) {
        PlaybackState.Builder playbackstateBuilder = new PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY |
                        PlaybackState.ACTION_PAUSE |
                        PlaybackState.ACTION_STOP);
        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mediaSession.setPlaybackState(playbackstateBuilder.build());
    }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        mp = null;
        super.onDestroy();
    }

    public void setSource(Playlist playlistSource){
        sourcePlaylist = playlistSource;
    }

    public void setSources(Playlist playlistSource, Integer index){
        sourcePlaylist = playlistSource;
        sourceIndex = index;
    }

    public void playMedia(String path, Playlist source, Integer index){
        this.sourcePlaylist = source;
        this.sourceIndex = index;
        actionPlay();
    }

    public void playMedia(String path){
        sourceIndex = null;
        sourcePlaylist = null;
        actionPlay(path);
    }

    public void playPrev(){
        if(sourcePlaylist!=null && sourcePlaylist.getLength()!=0 && sourceIndex!=null && sourceIndex!=-1 && sourceIndex!=0){
            String tempPath = sourcePlaylist.getSong(sourceIndex-1).getPath();
            sourceIndex = sourceIndex-1;
            actionPlay();
            setPlaybackState(PlaybackState.STATE_PLAYING);
            //Settings.setLastSongIndex(sourceIndex);
            broadcast("isPlaying",true, "SOURCECHANGED");
        }
    }

    public void playNext(){

        if(sourcePlaylist!=null && sourcePlaylist.getLength()!=0 && sourceIndex!=null && sourceIndex!=-1 && sourceIndex!=sourcePlaylist.getLength()-1){
            String tempPath = sourcePlaylist.getSong(sourceIndex+1).getPath();
            sourceIndex = sourceIndex+1;
            actionPlay();
            setPlaybackState(PlaybackState.STATE_PLAYING);
            //Settings.setLastSongIndex(sourceIndex);
            broadcast("isPlaying",true, "SOURCECHANGED");
        }
    }

    public void playMedia(){
        int requestAudioFocusResult = requestFocus();
        if(requestAudioFocusResult== AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mp.start();
            buildNotification(musicPath);
            setPlaybackState(PlaybackState.STATE_PLAYING);
            broadcast("isPlaying",true, "SOURCECHANGED");
        }
    }

    public void pauseMedia(){
        mp.pause();
        buildNotification(musicPath);
        setPlaybackState(PlaybackState.STATE_PAUSED);
        broadcast("isPlaying",false, "SOURCECHANGED");
        if(focusRequest!=null) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        }
    }

    public void mediaSeekTo(int position){
        //finished = false;
        mp.seekTo(position);
    }

    public Boolean isMediaPlaying(){
        if(mp!=null){
            return mp.isPlaying();
        }else{
            return false;
        }
    }

    public Boolean isMediaReady(){
        return mpAvailable;
    }

    public int mediaDuration(){
        if(mpAvailable) {
            System.out.println(mp.getDuration());
            return mp.getDuration();
        }
        return 0;
    }

    public int mediaRemaining(){
        if(mpAvailable) {
            //System.out.println(mp.getCurrentPosition());
        return mp.getCurrentPosition();
        }
        return 0;
    }

    public void mediaCompletionListener(){
        mp.setOnCompletionListener(mp -> {
            mp.seekTo(0);
            broadcast("isFinished",true, "STATE");
        });
    }

    private void actionPlay(String path) {
        musicPath = path;
        if (new File(path).exists()) {
            mp.reset();
            mp = MediaPlayer.create(this, Uri.parse(path));
            mp.start();

            mp.setLooping(false);
            mpAvailable = true;
            musicPath = path;
            SharedPreferences sharedPreferences = getSharedPreferences("lastMusic",MODE_PRIVATE);
            sharedPreferences.edit().putString("lastSongSource",null).apply();
            sharedPreferences.edit().putString("lastSongPath",path).apply();
            buildNotification(path);
        }
    }

    public int requestFocus(){
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

        focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this)
                .build();

        return audioManager.requestAudioFocus(focusRequest);

    }

    private void actionPlay() {
        int requestAudioFocusResult = requestFocus();
        if(requestAudioFocusResult== AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            if(sourcePlaylist.getLength()!=0) {
                String source = sourcePlaylist.getSongPath(sourceIndex);
                musicPath = source;
                if (new File(source).exists()) {
                    mp.reset();
                    mp = MediaPlayer.create(this, Uri.parse(source));
                    mp.start();
                    //mp.start();
                    mp.setLooping(false);
                    mpAvailable = true;
                    musicPath = source;
                    SharedPreferences sharedPreferences = getSharedPreferences("lastMusic", MODE_PRIVATE);
                    sharedPreferences.edit().putString("lastSongPath", source).apply();
                    mediaCompletionListener();
                    buildNotification(source);
                }
            }else{
                actionPlay(Settings.getLastSongPath());
            }
        }
    }


    private void buildNotification(String path){
        Intent notificationIntent = new Intent(this, MainActivity.class);  //This intent is used to open Activity on click of notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent prevIntent = new Intent(this, MediaControlService.class);
        prevIntent.setAction(ACTION_PREVIOUS);

        Intent playIntent = new Intent(this, MediaControlService.class);
        playIntent.setAction(ACTION_PLAY);

        Intent nextIntent = new Intent(this, MediaControlService.class);
        nextIntent.setAction(ACTION_NEXT);

        Notification.Action actionPrev = new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.ic_action_previous),"Previous",PendingIntent.getService(this,1,prevIntent,PendingIntent.FLAG_IMMUTABLE)).build();

        Notification.Action actionPlay;
        if(mp.isPlaying()){
            actionPlay = new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.ic_action_pause),"Pause",PendingIntent.getService(this,1,playIntent,PendingIntent.FLAG_IMMUTABLE)).build();
        }else{
            actionPlay = new Notification.Action.Builder(
                    Icon.createWithResource(this, R.drawable.ic_action_play),"Pause",PendingIntent.getService(this,1,playIntent,PendingIntent.FLAG_IMMUTABLE)).build();
        }

        Notification.Action actionNext = new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.ic_action_next),"Play",PendingIntent.getService(this,1,nextIntent,PendingIntent.FLAG_IMMUTABLE)).build();

        MusicDataMetadata musicDataMetadata = new MusicDataMetadata();
        musicDataMetadata.setAllData(path);
        if(musicDataMetadata.bitmap!=null){
            setMediaSessionMetadata(musicDataMetadata.title,musicDataMetadata.artist,musicDataMetadata.bitmap);
        }else{
            setMediaSessionMetadata(musicDataMetadata.title,musicDataMetadata.artist, BitmapFactory.decodeResource(getResources(), R.drawable.ic_group_23_image_6));
        }


        Notification notification = new Notification.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_group_23)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setShowWhen(false)
                .setContentIntent(pendingIntent) //Enables clicking notification
                .setOngoing(true)
                .addAction(actionPrev)
                .addAction(actionPlay)
                .addAction(actionNext)
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSession.getSessionToken()))
                .build(); //this will start the service
        startForeground(1, notification);//but this is important to keep in running in the foreground
    }

    private void actionNullNotification(){
        Notification notification = new Notification.Builder(this, CHANNEL_ID_1)
                .setContentTitle("Song playing")
                .setSmallIcon(R.drawable.ic_group_23)
                .build(); //this will start the service
        startForeground(9,notification);
    }

    private void broadcast(String key, Boolean payload, String action){
        Intent intent = new Intent();
        intent.putExtra(key, payload);
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    private void broadcast(String key, String payload, String action){
        Intent intent = new Intent();
        intent.putExtra(key, payload);
        intent.setAction(action);
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

}
