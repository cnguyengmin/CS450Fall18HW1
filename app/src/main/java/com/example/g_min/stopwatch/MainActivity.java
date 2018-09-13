package com.example.g_min.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView tv_count=null;
    private Button startBttn=null;
    private Button stopBttn=null;
    private Timer time=null;
    private Counter ctr = null;

    private AudioAttributes aa= null;
    private SoundPool soundPool=null;
    private int bloopSound =0;
    private int tick = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        this.tv_count = findViewById(R.id.tv_count);
        this.startBttn =findViewById(R.id.startBttn);
        this.stopBttn = findViewById(R.id.stopBttn);

        this.time = new Timer();

        this.startBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startBttn.getText().equals("Start")){
                    startBttn.setText("Reset");
                    startBttn.setBackgroundColor(Color.rgb(255,140,0));
                    time.scheduleAtFixedRate(ctr, 0,100);
                    stopBttn.setEnabled(true);

                }else{
                    time.cancel();
                    MainActivity.this.time = new Timer();
                    MainActivity.this.ctr = new Counter();
                    MainActivity.this.ctr.count=0;
                    MainActivity.this.ctr.sec=0;
                    MainActivity.this.ctr.mins=0;
                    MainActivity.this.tv_count.setText("00:00.0");
                    startBttn.setText("Start");
                    startBttn.setBackgroundColor(Color.GREEN);
                    stopBttn.setText("Stop");
                    stopBttn.setBackgroundColor(Color.RED);
                    stopBttn.setEnabled(false);

                }
            }
        });

        this.stopBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(stopBttn.getText().equals("Stop")){
                    time.cancel();
                    stopBttn.setText("Resume");
                    stopBttn.setBackgroundColor(Color.GREEN);

                }else{
                    stopBttn.setText("Stop");
                    stopBttn.setBackgroundColor(Color.RED);
                    int pre_count = ctr.count;
                    int pre_sec = ctr.sec;
                    int pre_mins = ctr.mins;
                    MainActivity.this.time = new Timer();
                    MainActivity.this.ctr = new Counter();
                    ctr.count = pre_count;
                    ctr.sec = pre_sec;
                    ctr.mins = pre_mins;
                    time.scheduleAtFixedRate(ctr, 0,100);


                }


            }
        });


        int mins = getPreferences(MODE_PRIVATE).getInt("MINS",0);
        int sec = getPreferences(MODE_PRIVATE).getInt("SECOND",0);
        int count = getPreferences(MODE_PRIVATE).getInt("COUNT",0);

        String clock;
        String minute = "";
        String second = "";
        if (mins <10){
            minute = "0" + Integer.toString(mins);
        } else{
            minute = Integer.toString(mins);
        }
        if (sec <10){
            second = "0" + Integer.toString(sec);
        } else{
            second = Integer.toString(sec);
        }
        clock = minute + ":" + second +"." + Integer.toString(count);
        this.tv_count.setText(clock);
        this.ctr = new Counter();
        this.ctr.count=count;
        this.ctr.sec=sec;
        this.ctr.mins=mins;

        if(MainActivity.this.tv_count.getText().equals("00:00.0")){
            this.startBttn.setText("Start");
            this.stopBttn.setEnabled(false);
        }else{
            this.startBttn.setText("Reset");
            this.startBttn.setBackgroundColor(Color.rgb(255,140,0));
            this.stopBttn.setText("Resume");
            this.stopBttn.setBackgroundColor(Color.GREEN);
        }

        this.aa = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();

        this.soundPool=new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(aa).build();

        this.bloopSound = this.soundPool.load(this,R.raw.bloop,1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(bloopSound,1f, 1f,1,0,1f);
                Animator anim = AnimatorInflater.loadAnimator(MainActivity.this,R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //factory method
        Toast.makeText(this, "Stopwatch is ready", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        getPreferences(MODE_PRIVATE).edit().putInt("MINS", ctr.mins).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("SECOND", ctr.sec).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        getPreferences(MODE_PRIVATE).edit().putInt("MINS", ctr.mins).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("SECOND", ctr.sec).apply();
        getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
    }

    class Counter extends TimerTask{
        private int count;
        private int sec;
        private int mins;

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String clock;
                    String minute = "";
                    String second = "";

                    if (mins <10){
                        minute = "0" + Integer.toString(mins);
                    }else{
                        minute = Integer.toString(mins);
                    }
                    if (sec <10){
                        second = "0" + Integer.toString(sec);
                    }else{
                        second = Integer.toString(sec);
                    }
                    clock = minute + ":" + second +"." + Integer.toString(count);

                    MainActivity.this.tv_count.setText(clock);
                    count++;

                    if (count == 10){
                        sec = sec + 1;
                        count = 0;
                    }
                    if (sec == 60){
                        mins = mins +1;
                        sec = 0;
                    }
                }
            });
        }


    }
}
