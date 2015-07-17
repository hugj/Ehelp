package com.ehelp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ehelp.account.login;
import com.ehelp.send.SendHelp;
import com.ehelp.send.SendQuestion;
import com.ehelp.send.SendSOS;


public class Test extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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
    public void login(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
    }
    public void sendsos(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SendSOS.class);
        startActivity(intent);
    }
    public void sendhelp(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SendHelp.class);
        startActivity(intent);
    }
    public void sendque(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SendQuestion.class);
        startActivity(intent);
    }
}
