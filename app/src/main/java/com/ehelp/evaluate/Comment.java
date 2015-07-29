package com.ehelp.evaluate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendHelp;
import com.ehelp.send.SendQuestion;
import com.ehelp.user.pinyin.AssortView;
import com.ehelp.user.pinyin.PinyinAdapter;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.androidinject.annotation.annotations.base.AIView;
import com.wangjie.androidinject.annotation.present.AIActionBarActivity;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.List;

//import android.view.View.OnLongClickListener;

/**
 * Created by kyy on 2015/7/19.
 */
@AILayout(R.layout.activity_comment)
public class Comment extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private Toolbar mToolbar;
    static int starnum = 1;//public static int starnumm =1;

    private PinyinAdapter adapter;
    private ExpandableListView eListView;
    private AssortView assortView;
    private List<String> names;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("评价");


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
        ratBar.setStepSize(1);//步进为1
        ratBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //doing actions
                //rating是传入的星级。
            }
        });

        //FAB
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("求救")
                        .setResId(R.mipmap.ic_launcher)
                        .setIconNormalColor(0xffd84315)
                        .setIconPressedColor(0xffbf360c)
                        .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("求助")
//                        .setResId(R.mipmap.ico_test_c)
                        .setDrawable(getResources().getDrawable(R.mipmap.ic_launcher))
                        .setIconNormalColor(0xff4e342e)
                        .setIconPressedColor(0xff3e2723)
                        .setLabelColor(Color.WHITE)
                        .setLabelSizeSp(14)
                        .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(context, 4)))
                        .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                        .setLabel("提问")
                        .setResId(R.mipmap.ic_launcher)
                        .setIconNormalColor(0xff056f00)
                        .setIconPressedColor(0xff0d5302)
                        .setLabelColor(0xff056f00)
                        .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(context, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(context, 5))
        ;

        rfabHelper = new RapidFloatingActionHelper(
                context,
                rfaLayout,
                rfaButton,
                rfaContent
        ).build();
    }
    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else
        if (position == 1) {
            Intent intent = new Intent(this, SendHelp.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else
        if (position == 1) {
            Intent intent = new Intent(this, SendHelp.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }


    //toolbar设置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if ((id == R.id.action_settings)){
            return true;
        }

        return super.onOptionsItemSelected(item);
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
