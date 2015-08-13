package com.ehelp.home;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ehelp.R;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

public class ConversationListActivity extends ActionBarActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("用户列表");
//
//        /* 创建 conversationlist 的Fragment */
//        ConversationListFragment fragment =
//                (ConversationListFragment)getSupportFragmentManager().findFragmentById(R.id.conversationlist);
//
//        /* 给 IMKit 传递默认的参数，用于显示*/
//        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
//                .appendPath("conversationlist")
//                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话采用聚合显示
//                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true") //设置群组会话采用聚合显示
//                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false") // 设置讨论组不采用聚合显示
//                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true") //设置系统会话采用聚合显示
//                .build();
//
//        fragment.setUri(uri);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_conversation_list, menu);
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
