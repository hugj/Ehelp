package com.ehelp.map;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ehelp.utils.RequestHandler;

import com.ehelp.R;

public class AudioActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private VideoView video_view;
    private int event_id;
    private String url_part = "http://120.24.208.130:1501";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        init();

        event_id = this.getIntent().getIntExtra("event_id", -1);

        if (event_id == -1) {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_LONG).show();
        } else {
            String event_id_string = String.valueOf(event_id);

//            final String url_mp3 = "http://120.24.208.130:1501/sound/200.mp3";

            final String url_mp3 = url_part + "/sound/" + event_id_string + ".mp3";

            boolean mp3 = RequestHandler.TestGetURL(url_mp3);

            if (!mp3) {
                Toast.makeText(getApplicationContext(), "没有相应的音频",
                        Toast.LENGTH_SHORT).show();
            } else {
                RelativeLayout RL = (RelativeLayout)findViewById(R.id.audio_layout);
                RL.setGravity(Gravity.CENTER);
                VideoView vv = new VideoView(AudioActivity.this);
                vv.setId(R.id.id_audio);
                vv.setBackgroundDrawable(getResources().getDrawable(R.drawable.audio));
                RL.addView(vv);

                video_view = (VideoView) findViewById(R.id.id_audio);

                Uri uri = Uri.parse(url_mp3);
                video_view.setMediaController(new MediaController(AudioActivity.this));
                video_view.setVideoURI(uri);
                video_view.start();
                video_view.requestFocus();
            }
        }
    }

    private void init() {
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("音频");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_video, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
