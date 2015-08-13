package com.ehelp.home;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ehelp.R;

import io.rong.imkit.fragment.ConversationFragment;

public class ConversationActivity extends ActionBarActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("聊天");

//        ConversationFragment fragment =
//                (ConversationFragment)getSupportFragmentManager().findFragmentById(R.id.conversation);
//        /* 传入私聊会话 PRIVATE 的参数*/
//        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
//                .appendPath(io.rong.imlib.model.Conversation.ConversationType.PRIVATE.getName().toLowerCase())
//                .appendQueryParameter("targetId", "7").appendQueryParameter("title", "hello").build();
//
//        fragment.setUri(uri);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_conversation, menu);
//        return true;
//    }
//
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
