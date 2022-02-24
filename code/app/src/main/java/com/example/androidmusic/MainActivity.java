package com.example.androidmusic;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter arrayAdapter;

    int currentVolume = 70;
    int maxVolume = 100;
    int maxProgBar = 1000;
    int currentSongIndex;
    SeekBar volumeBar;
    SeekBar progressBar;
    //TODO make the buttons' functions
    ImageButton btnPlayPause;
    ImageButton btnStop;
    ImageButton btnNext;
    ImageButton btnPrevious;
    TextView textViewActualTime;
    ArrayList songList;
    MediaPlayer mediaPlayer = new MediaPlayer();
    Handler handler = new Handler();
    Runnable run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPlayPause = findViewById(R.id.btn_playPause);
        btnPlayPause.setEnabled(false);
        btnStop = findViewById(R.id.btn_stop);
        btnStop.setEnabled(false);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setEnabled(false);
        btnPrevious = findViewById(R.id.btn_previous);
        btnPrevious.setEnabled(false);
        updateButtons();

        final ListView listViewSongs = findViewById(R.id.ListView_Songs);
        volumeBar = findViewById(R.id.volumeBar);
        volumeBar.setMax(maxVolume);
        volumeBar.setProgress(currentVolume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentVolume = i;
                float volume = (float) i / 100f;
                mediaPlayer.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(maxProgBar);
        textViewActualTime = findViewById(R.id.textView_actualTime);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                    mediaPlayer.seekTo((mediaPlayer.getDuration() * i) / maxProgBar);
                if(i != 0) {
                    int x = mediaPlayer.getCurrentPosition()/1000;
                    textViewActualTime.setText(String.format("%02d:%02d", x/ 60, x % 60));
                } else {
                    textViewActualTime.setText("00:00");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        songList = getSongsList();
        listViewSongs.setAdapter(new CustomSongAdapter(this, songList));
        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int song = ((SongListItem) listViewSongs.getItemAtPosition(i)).getSongID();
                currentSongIndex = i;
                playSongSelected(song);
            }
        });

        run = new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress((mediaPlayer.getCurrentPosition()*maxProgBar) / mediaPlayer.getDuration());
                if(mediaPlayer.isPlaying()){
                    handler.postDelayed(this, 500);
                }
            }
        };
    }

    public ArrayList getSongsList(){
        ArrayList<SongListItem> result = new ArrayList<>();
        SongListItem item1 = new SongListItem("The Scientist", "Coldplay", "A Rush of Blood to the Head", R.raw.coldplay_the_scientist);
        SongListItem item2 = new SongListItem("It's My Life", "Bon Jovi", "Crush", R.raw.its_my_life_bon_jovi);
        SongListItem item3 = new SongListItem("Arcade", "Duncan Laurence", "Small Town Boy", R.raw.duncan_laurence_arcade);
        result.add(item1);
        result.add(item2);
        result.add(item3);
        return result;
    }

    public void playSongSelected(int songId){
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(this, songId);
        //TODO enable buttons
        btnPlayPause.setEnabled(true);
        btnPlayPause.setImageResource(R.drawable.ic_pause);
        btnStop.setEnabled(true);
        btnPrevious.setEnabled(currentSongIndex != 0);
        btnNext.setEnabled(currentSongIndex != songList.size()-1);
        mediaPlayer.start();
        handler.post(run);
        updateButtons();
    }

    public void btnPlayPauseClick(View view){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            ((ImageButton) view).setImageResource(R.drawable.ic_play);
        }
        else {
            mediaPlayer.start();
            handler.post(run);
            ((ImageButton) view).setImageResource(R.drawable.ic_pause);
        }
    }

    public void updateButtons(){
        btnPrevious.setBackgroundResource(btnPrevious.isEnabled() ? R.color.colorBtnEnnabled : R.color.colorBtnDisabled);
        btnStop.setBackgroundResource(btnStop.isEnabled() ? R.color.colorBtnEnnabled : R.color.colorBtnDisabled);
        btnPlayPause.setBackgroundResource(btnPlayPause.isEnabled() ? R.color.colorBtnEnnabled : R.color.colorBtnDisabled);
        btnNext.setBackgroundResource(btnNext.isEnabled() ? R.color.colorBtnEnnabled : R.color.colorBtnDisabled);
    }

    public void setBtnNextClick(View view){
        ((ListView) findViewById(R.id.ListView_Songs)).performItemClick(view, currentSongIndex+1, 0);
    }

    public void setBtnPreviousClick(View view){
        ((ListView) findViewById(R.id.ListView_Songs)).performItemClick(view, currentSongIndex-1, 0);
    }

    public void btnStopClick(View view){
        //TODO return seekbar state, set playPauseButton to play
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        btnPlayPause.setImageResource(R.drawable.ic_play);
    }
}