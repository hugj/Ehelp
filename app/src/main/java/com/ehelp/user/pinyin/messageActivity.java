package com.ehelp.user.pinyin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ehelp.R;
public class messageActivity extends ActionBarActivity {

    private RelativeLayout myLay = null;
    private AlertDialog myDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //click on set name
        myLay = (RelativeLayout) findViewById(R.id.detail_setname);
        myLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                myDialog = new AlertDialog.Builder(messageActivity.this).create();
                myDialog.show();
                myDialog.getWindow().setContentView(R.layout.activity_messetname);
                //click on cancel
                myDialog.getWindow()
                        .findViewById(R.id.setname5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                //click on ensure
                myDialog.getWindow()
                        .findViewById(R.id.setname6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "set name sucessfully",
                                        Toast.LENGTH_SHORT).show();
                                myDialog.dismiss();
                            }
                        });
                myDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
        //add contact
        Button add_excontact = (Button)findViewById(R.id.addexcontact);
        add_excontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageActivity.this.dialog();
            }
        });
    }
    //add ex contact
    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("是否添加？");
        builder.setTitle("添加紧急联系人");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                messageActivity.this.finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //test-------------------------------------------------------
    public void test_click(View view) {
        Intent intent = new Intent(this, UsermesActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
