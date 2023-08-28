package com.example.myaudioplayer;

import static com.example.myaudioplayer.AlbumDetailsAdapter.albumFIles;
import static com.example.myaudioplayer.MainActivity.musicFiles;
import static com.example.myaudioplayer.MainActivity.repeatBoolean;
import static com.example.myaudioplayer.MainActivity.shuffleBoolean;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements
        MediaPlayer.OnCompletionListener,ActionPlaying, ServiceConnection {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView backBtn,menuBtn,coverArt,shuffleBtn,previousBtn,repeatBtn,nextBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seek_Bar;
    int position=-1;
    static  ArrayList<MusicFiles> listSongs=new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    private Thread playThread, prevThread, nextThread;

    MusicService musicService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seek_Bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer!=null && b)
                {
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                    seek_Bar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        // For checking if shuffle button is on/off using its boolean
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean)
                {
                    shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle);
                }
                else {
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean)
                {
                    repeatBoolean=false;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat);
                }
                else {
                    repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_on_24);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent=new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }
    public void onPause(){
        super.onPause();
        unbindService(this);
    }
    private void nextThreadBtn() {
        nextThread=new Thread()
        {
            public void run(){
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            // Shuffle and repeat logic
            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position=((position+1)%listSongs.size());   // to check the song within the list
            }
            // else the repeat button is on position will be as it is

            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
//            playPauseBtn.setImageResource(R.drawable.baseline_pause);
//            mediaPlayer.start();
            seek_Bar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek_Bar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_pause);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position=((position+1)%listSongs.size());   // to check the song within the list
            }

            //position=((position+1)%listSongs.size());   // to check the song within the list
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //playPauseBtn.setImageResource(R.drawable.baseline_play);

            seek_Bar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek_Bar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_play);
            //mediaPlayer.start();
        }
    }

    private int getRandom(int i) {
     Random random =new Random();
        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread=new Thread()
        {
            public void run(){
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    // Working of play pause Button
    public void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.baseline_play);
            mediaPlayer.pause();
            seek_Bar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek_Bar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });

        }
        else {
            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            mediaPlayer.start();
            seek_Bar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek_Bar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private void prevThreadBtn() {
        prevThread=new Thread()
        {
            public void run(){
                super.run();
                previousBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        previousBtnClicked();
                    }
                });
            }
        };
       prevThread.start();
    }

    public void previousBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position=((position-1)<0 ?(listSongs.size()-1) : (position-1));   // to check the song within the list
            }


            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
//            playPauseBtn.setImageResource(R.drawable.baseline_pause);
//            mediaPlayer.start();
            seek_Bar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek_Bar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_pause);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position=((position-1)<0 ?(listSongs.size()-1) : (position-1));   // to check the song within the list
            }

            //position=((position-1)<0 ?(listSongs.size()-1) : (position-1));  // to check the song within the list
            uri=Uri.parse(listSongs.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
//            playPauseBtn.setImageResource(R.drawable.baseline_pause);
//            mediaPlayer.start();
            seek_Bar.setMax(mediaPlayer.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer!=null){
                        int mCurrentPosition=mediaPlayer.getCurrentPosition()/1000;
                        seek_Bar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.baseline_play);
           // mediaPlayer.start();
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout="";
        String totalNew="";
        String seconds = String.valueOf(mCurrentPosition%60);
        String minutes=String.valueOf(mCurrentPosition/60);
        totalout=minutes+":"+seconds;
        totalNew=minutes+":"+"0"+seconds;

        if(seconds.length()==1){
            return totalNew;
        }
        else{
            return totalout;
        }
    }

    private void getIntentMethod() {
        position=getIntent().getIntExtra("position",-1);
        String sender = getIntent().getStringExtra("sender");
        if(sender!=null && sender.equals("albumDetails")) {
            listSongs= albumFIles;
        }
        else {
            listSongs = musicFiles;
        }
        if(listSongs!=null){
            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            uri=Uri.parse(listSongs.get(position).getPath());
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else{
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seek_Bar.setMax(mediaPlayer.getDuration()/1000);    // divide by 1000 so that we get in seconds
        metaData(uri);
    }

    private void initViews() {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        duration_played=findViewById(R.id.duration_played);
        duration_total=findViewById(R.id.duration_total);
        coverArt=findViewById(R.id.cover_art);
        shuffleBtn=findViewById(R.id.shuffle);
        previousBtn=findViewById(R.id.previous);
        repeatBtn=findViewById(R.id.repeat);
        nextBtn=findViewById(R.id.next);
        playPauseBtn=findViewById(R.id.play_pause);
        seek_Bar=findViewById(R.id.seekBar);
        menuBtn=findViewById(R.id.menu_btn);
        backBtn=findViewById(R.id.back_btn);
    }

    private void metaData(Uri uri)  // uniform resource identifier
    {   // For fetching the total duration of the song
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art=retriever.getEmbeddedPicture();
        Bitmap bitmap;

        if(art!=null)
        {


            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,coverArt,bitmap);
            // For Background color acc to the music in the player

          // Palette.from(bitmap).generate(new Palette.PaletteAsyncListener())
        }
        else{
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.song_icon)
                    .into(coverArt);
           ImageView gredient = findViewById(R.id.imageViewGradient);
            RelativeLayout mContainer=findViewById(R.id.mContainer);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }

    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if(mediaPlayer!=null){
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
            MusicService.MyBinder myBinder= (MusicService.MyBinder) service;
            musicService = myBinder.getService();
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService=null;
    }
}