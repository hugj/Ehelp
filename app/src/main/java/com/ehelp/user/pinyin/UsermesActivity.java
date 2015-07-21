package com.ehelp.user.pinyin;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.ehelp.R;

/**
 * Created by Administrator on 2015/7/20.
 */
public class UsermesActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermes);
        /*RelativeLayout homepage = (RelativeLayout)findViewById(R.id.user_page);
        homepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsermesActivity.this,homepageActivity.class);
                startActivity(intent);
            }
        });
        */
    }
}
