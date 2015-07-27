package com.ehelp.user.pinyin;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ehelp.R;

public class ChangephoneActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changephone);
        //点击验证按钮事件
        Button Changephone = (Button)findViewById(R.id.accountsafe_change_phone9);
        Changephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "绑定成功",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
