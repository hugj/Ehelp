package com.ehelp.map;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class VideoActivity extends ActionBarActivity {
    private Toolbar mToolbar;
    private VideoView video_view;
    private int event_id;
    private String url_part = "http://120.24.208.130:1501";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        init();

        event_id = this.getIntent().getIntExtra("event_id", -1);

        if (event_id == -1) {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            RelativeLayout RL = (RelativeLayout) findViewById(R.id.video_layout);
            VideoView vv = new VideoView(VideoActivity.this);
            vv.setId(R.id.id_video);
            RL.addView(vv);

            video_view = (VideoView) findViewById(R.id.id_video);

            String event_id_string = String.valueOf(event_id);

//            final String url_mp3 = "http://120.24.208.130:1501/sound/459.mp3";
//            final String url_mp4 = "http://120.24.208.130:1501/video/459.mp4";

            final String url_mp3 = url_part + "/sound/" + event_id_string + ".mp3";
            final String url_mp4 = url_part + "/video/" + event_id_string + ".mp4";
            if (!RequestHandler.TestGetURL(url_mp3) && !RequestHandler.TestGetURL(url_mp4)) {
                Toast.makeText(getApplicationContext(), "没有相应的音频或视频",
                        Toast.LENGTH_SHORT).show();
            } else if (!RequestHandler.TestGetURL(url_mp3) && RequestHandler.TestGetURL(url_mp4)) {
                Uri uri = Uri.parse(url_mp4);
                video_view.setMediaController(new MediaController(VideoActivity.this));
                video_view.setVideoURI(uri);
                video_view.start();
                video_view.requestFocus();
            } else if (RequestHandler.TestGetURL(url_mp3) && !RequestHandler.TestGetURL(url_mp4)) {
                Uri uri = Uri.parse(url_mp3);
                video_view.setMediaController(new MediaController(VideoActivity.this));
                video_view.setVideoURI(uri);
                video_view.start();
                video_view.requestFocus();
            } else {
                Button button_video = new Button(VideoActivity.this);
                button_video.setId(R.id.id_button_video);
                button_video.setText("点击收看视频");
                RL.addView(button_video);

                RelativeLayout RL2 = (RelativeLayout) findViewById(R.id.audio_layout);

                VideoView vv2 = new VideoView(VideoActivity.this);
                vv2.setId(R.id.id_audio);
                RL2.addView(vv2);

                Button button_audio = new Button(VideoActivity.this);
                button_audio.setId(R.id.id_button_audio);
                button_audio.setText("点击收听音频");
                RL2.addView(button_audio);

                final VideoView video_view2 = (VideoView)findViewById(R.id.id_audio);
                Button b_v = (Button)findViewById(R.id.id_button_video);
                Button b_a = (Button)findViewById(R.id.id_button_audio);

                b_a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(url_mp3);
                        video_view2.setMediaController(new MediaController(VideoActivity.this));
                        video_view2.setVideoURI(uri);
                        video_view2.start();
                        video_view2.requestFocus();
                    }
                });

                b_v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(url_mp4);
                        video_view.setMediaController(new MediaController(VideoActivity.this));
                        video_view.setVideoURI(uri);
                        video_view.start();
                        video_view.requestFocus();
                    }
                });
            }
        }
    }

    private void init() {
        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("音频视频");
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
