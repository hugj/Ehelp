package com.ehelp.user.lovebank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.ehelp.R;

/*
 *爱心币管理
 */
public class CoinActivity extends ActionBarActivity {

    private TextView coin_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
        Intent intent = getIntent();
        String str = intent.getStringExtra("coin");
        coin_num = (TextView)findViewById(R.id.coin_num);
        coin_num.setText(str);//设置爱心币数量
    }

}
