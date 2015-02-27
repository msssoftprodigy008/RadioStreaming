package com.example.anuj_sharma.radiosample;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

import static android.media.MediaPlayer.OnCompletionListener;
import static android.media.MediaPlayer.OnPreparedListener;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private MediaPlayer player;
    Button btn_playpause;
    String Radio_Url = "http://2773.live.streamtheworld.com:80/WXLMFM_SC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        //Initialize click listener
        btn_playpause.setOnClickListener(this);
    }

    void init() {
        btn_playpause = (Button) findViewById(R.id.btn_playpause);
    }

    void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource(Radio_Url);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_playpause) {
            //StartRadio(Radio_Url);
           // startPlaying();
            if(player==null)
            {
                initializeMediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                new playradio().execute();
                btn_playpause.setText("pause");
            }
            else if(player!=null && player.isPlaying())
            {
                btn_playpause.setText("play");
                player.pause();
            }
            else if(player!=null && !player.isPlaying())
            {
                btn_playpause.setText("pause");
                player.start();
            }

                /*player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                new playradio().execute();
                btn_playpause.setText("pause");*/


        }
    }

    void startPlaying() {
        if (btn_playpause.getText().toString().equalsIgnoreCase("play")) {
            if (player != null) {
                player.prepareAsync();
                player.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        player.start();
                    }
                });
                btn_playpause.setText("pause");
            }
        } else {
            player.pause();
            btn_playpause.setText("play");
        }

    }

    class playradio extends AsyncTask<String, Void, String>
    {
        ProgressDialog mDialog;
        Boolean prepared;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog = new ProgressDialog(MainActivity.this);
            mDialog.setMessage("Buffering...");
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                player = new MediaPlayer();
                player.setDataSource(Radio_Url);

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                player.prepare();
                return "true";
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    prepared = true;
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(mDialog.isShowing())
            {
                mDialog.dismiss();
                if(result!=null)
                {
                    player.start();
                    prepared = false;
                }
            }
            Log.d("Player is about to start","Player is about to start");

            player.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    btn_playpause.setText("play");
                    player.stop();
                    player.reset();
                    player.release();
                    player = null;
                }
            });

        }
    }

}
