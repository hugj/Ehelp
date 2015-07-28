package com.ehelp.evaluate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ehelp.R;

//import android.view.View.OnLongClickListener;

/**
 * Created by kyy on 2015/7/19.
 */
public class Comment extends ActionBarActivity {
    static int starnum = 1;//public static int starnumm =1;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.fragment_activity);}

        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_comment);
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        //http://www.cnblogs.com/salam/archive/2010/11/30/1892143.html

        //button1.setOnClickListener(new button1());

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("评价");
        setSupportActionBar(mToolbar);

        //添加按钮事件
        Button button  =(Button)findViewById(R.id.button_comment_send);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "通过setOnClickListener（）方法实现",
                        Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(activity_comment.this, activity_home.class);
                //activity_comment.this.startActivity(intent);
            }
        });
        RatingBar ratBar = (RatingBar)findViewById(R.id.ratingBar);
        ratBar.setStepSize (1);//步进为1
        ratBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //doing actions
                //rating是传入的星级。
            }
        });

    }

    //写一个返回星级INT类型的函数。
    /*public void btn_Listener(View v){
        //<提交>后的跳转用intent实现。
        Toast.makeText(getApplicationContext(), "xml代码内调用实现tijiao",
                Toast.LENGTH_SHORT).show();
    }*/
   /* public void btn1_Listener(View v){
        //1星后的跳转用intent实现。
        Toast.makeText(getApplicationContext(), "xml代码内调用实现1",
                Toast.LENGTH_SHORT).show();
        starnum = 1;
    }
    public void btn2_Listener(View v){
        //2星后的跳转用intent实现。
        Toast.makeText(getApplicationContext(), "xml代码内调用实现2",
                Toast.LENGTH_SHORT).show();
        starnum = 2;
    }
    public void btn3_Listener(View v){
        //3星后的跳转用intent实现。
        Toast.makeText(getApplicationContext(), "xml代码内调用实现3",
                Toast.LENGTH_SHORT).show();
        starnum = 3;
    }
    public void btn4_Listener(View v){
        //4星后的跳转用intent实现。
        Toast.makeText(getApplicationContext(), "xml代码内调用实现4",
                Toast.LENGTH_SHORT).show();
        starnum = 4;
    }
    public void btn5_Listener(View v){
        //5星后的跳转用intent实现。
        Toast.makeText(getApplicationContext(), "xml代码内调用实现5",
                Toast.LENGTH_SHORT).show();
                starnum = 5;
    }

//获取星级
    public int getStarnum(){
        return starnum;
    }*/

    //获取评论内容
    public String getComment(){
        String str1="";
        EditText editText1 =(EditText)findViewById(R.id.editText_comment);
        str1=editText1.getText().toString();
        return str1;
    }
}
