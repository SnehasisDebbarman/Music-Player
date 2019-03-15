package com.example.snehasis.player2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;




public class PlayerActivity extends AppCompatActivity  {

    Button btn_pause,btn_next,btn_previous,btn_shuffle,btn_repeat;
    TextView songTextLabel;
    SeekBar songSeekBar;
    String sname;
    Thread updateseekBar;
    static MediaPlayer myMediaPlayer;
    int position;
    ArrayList<File> mySongs=new ArrayList();

    Context context;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_player);
        btn_next = (Button) findViewById(R.id.next);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_previous = (Button) findViewById(R.id.previous);
        btn_shuffle = (Button) findViewById(R.id.shuffle);
        btn_repeat = (Button) findViewById(R.id.repeat);
        songTextLabel = (TextView) findViewById(R.id.songLabel);
        songSeekBar = (SeekBar) findViewById(R.id.seekBar);
        final TextView seekBarHint = findViewById(R.id.textView);


        getSupportActionBar().setTitle("NOW PLAYING");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


          updateseekBar = new Thread() {


            @Override
            public void run() {
                final int totalDuration = myMediaPlayer.getDuration();
                int currentPosition;
                currentPosition = 0;
                while(myMediaPlayer.isPlaying()) {
                    while(currentPosition < totalDuration) {
                        try {
                            sleep(500);
                            currentPosition = myMediaPlayer.getCurrentPosition();
                            songSeekBar.setProgress(currentPosition);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


            }


        };


        if(myMediaPlayer!=null){
            myMediaPlayer.stop();
            myMediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        try {
            mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
            sname = mySongs.get(position).getName();
            final String songName = i.getStringExtra("songname");
            songTextLabel.setText(songName);
            songTextLabel.setSelected(true);
            position = bundle.getInt("pos", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Uri u = Uri.parse(mySongs.get(position).toString());
        myMediaPlayer = MediaPlayer.create(PlayerActivity.this, u);

        songSeekBar.setMax(myMediaPlayer.getDuration());

        myMediaPlayer.start();

        updateseekBar.start();



        songSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        songSeekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);
                int z, z1;
                if (x < 10)
                    seekBarHint.setText("0:0" + x);
                else if (x > 9 && x < 60)
                    seekBarHint.setText("0:" + x);
                else {
                    z = x / 60;
                    z1 = x % 60;
                    if (z1 < 10) {
                        seekBarHint.setText("0" + z + ":0" + z1);
                    } else {
                        seekBarHint.setText("0" + z + ":" + z1);
                    }


                }

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                int seekWidth = seekBar.getWidth();
                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
                int labelWidth = seekBarHint.getWidth();
                seekBarHint.setX(offset + seekBar.getX() + val
                        - Math.round(percent * offset)
                        - Math.round(percent * labelWidth / 2));

                if (progress > 0 && myMediaPlayer != null && !myMediaPlayer.isPlaying()) {

                    btn_pause.setBackgroundResource(R.drawable.play);
                    myMediaPlayer.seekTo(progress);
                    //seekBar.setProgress(progress);



                }

            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {




                songSeekBar.setMax(myMediaPlayer.getDuration());

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try {
                    if (myMediaPlayer != null && myMediaPlayer.isPlaying()) {
                        myMediaPlayer.seekTo(seekBar.getProgress());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        });




            songSeekBar.setMax(myMediaPlayer.getDuration());

            btn_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    songSeekBar.setMax(myMediaPlayer.getDuration());


                    if (myMediaPlayer.isPlaying()) {
                        btn_pause.setBackgroundResource(R.drawable.play);

                        myMediaPlayer.pause();
                    } else {
                        btn_pause.setBackgroundResource(R.drawable.pause);

                        myMediaPlayer.start();
                    }


                }
            });
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myMediaPlayer.stop();
                    myMediaPlayer.release();
                    position = ((position + 1) % mySongs.size());

                    Uri u = Uri.parse(mySongs.get(position).toString());
                    myMediaPlayer = MediaPlayer.create(PlayerActivity.this, u);

                  //  songSeekBar.setMax(myMediaPlayer.getDuration());
//                    updateseekBar.start();

                    sname = mySongs.get(position).toString();
                    String sname1 = sname.substring(sname.lastIndexOf("/") + 1);
                    songTextLabel.setText(sname1);
                    myMediaPlayer.start();
                    songSeekBar.setProgress(0);


                }

            });


            btn_repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myMediaPlayer.stop();
                    myMediaPlayer.release();
                    position = position;

                    Uri u = Uri.parse(mySongs.get(position).toString());
                    myMediaPlayer = MediaPlayer.create(PlayerActivity.this, u);

                   // songSeekBar.setMax(myMediaPlayer.getDuration());
                    //                updateseekBar.start();

                    sname = mySongs.get(position).toString();
                    String sname1 = sname.substring(sname.lastIndexOf("/") + 1);
                    songTextLabel.setText(sname1);
                    myMediaPlayer.start();
                    songSeekBar.setProgress(0);
                    btn_pause.setBackgroundResource(R.drawable.pause);


                }

            });


            btn_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myMediaPlayer.pause();
                    myMediaPlayer.release();
                    position = ((position - 1) < 0) ? (mySongs.size() - 1) : (position - 1);

                    Uri u = Uri.parse(mySongs.get(position).toString());
                    myMediaPlayer = MediaPlayer.create(PlayerActivity.this, u);
                    sname = mySongs.get(position).getName();
                    String sname1 = sname.substring(sname.lastIndexOf("/") + 1);

                    songTextLabel.setText(sname1);
                    myMediaPlayer.start();
                    songSeekBar.setProgress(0);


                }
            });
            btn_shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    myMediaPlayer.pause();
                    myMediaPlayer.release();
                    Uri u = Uri.parse(mySongs.get(position).toString());
                    Random rand = new Random();
                    position = rand.nextInt((mySongs.size() - 1) + 1);
                    myMediaPlayer = MediaPlayer.create(PlayerActivity.this, u);
                    sname = mySongs.get(position).getName();
                    String sname1 = sname.substring(sname.lastIndexOf("/") + 1);
                    songTextLabel.setText(sname1);
                    myMediaPlayer.start();
                    songSeekBar.setProgress(0);
                    btn_pause.setBackgroundResource(R.drawable.pause);


                }
            });

            myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    position = ((position + 1) % mySongs.size());
                    sname = mySongs.get(position).getName();
                    String sname1 = sname.substring(sname.lastIndexOf("/") + 1);
                    songTextLabel.setText(sname1);
                    mp.start();
                    songSeekBar.setProgress(0);


                }
            });


            myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mp.start();

                }
            });


            myMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });


        }




    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroy () {
            super.onDestroy();
//
        }


}





