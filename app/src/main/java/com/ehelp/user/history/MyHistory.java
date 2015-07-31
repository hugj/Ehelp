package com.ehelp.user.history;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.utils.RequestHandler;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_my_history)
public class MyHistory extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    private ViewPager mPager;//页卡内容
    private List<View> listViews; // Tab页面列表
    private TextView t1, t2, t3;// 页卡头标
    //后台连接
    private SharedPreferences SharedPref;
    private String message;
    private String jsonStrng;

    //toolbar
    private Toolbar mToolbar;

    private int wha = 2;//1代表全部，2代表发起，3代表接收
    public final static String EXTRA_MESSAGE = "com.ehelp.receive.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        tvv.setText("我的历史记录");

        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
        InitViewPager();
        InitTextView();

        //set FAB
        fab();
    }

    private void fab(){
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
            Intent intent = new Intent(this, sendhelp_map.class);
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
            Intent intent = new Intent(this, sendhelp_map.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.all) {
            //change to all
            wha = 1;
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.sponsor) {
            //change to my
            wha = 2;
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.accept) {
            //change to what i accepted
            wha = 3;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 初始化头标
     */

    private void InitTextView() {
        t1 = (TextView) findViewById(R.id.text1);
        t2 = (TextView) findViewById(R.id.text2);
        t3 = (TextView) findViewById(R.id.text3);

        t1.setOnClickListener(new MyOnClickListener(0));
        t2.setOnClickListener(new MyOnClickListener(1));
        t3.setOnClickListener(new MyOnClickListener(2));
    }
    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    }
    /**
     * 初始化ViewPager
     */
    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        listViews = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        listViews.add(mInflater.inflate(R.layout.lay1, null));
        listViews.add(mInflater.inflate(R.layout.lay2, null));
        listViews.add(mInflater.inflate(R.layout.lay3, null));
        mPager.setAdapter(new MyPagerAdapter(listViews));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        //初始化，使默认页面卡是是绿色的
        TextView tv = (TextView) findViewById(R.id.text1);
        tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

    }
    /**
     * ViewPager适配器
     */
    public class MyPagerAdapter extends PagerAdapter {
        public List<View> mListViews;

        public MyPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
            return mListViews.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }
    }
    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            TextView tv = (TextView) findViewById(R.id.text1);
            TextView tv2 = (TextView) findViewById(R.id.text2);
            TextView tv3 = (TextView) findViewById(R.id.text3);
            switch (arg0) {
                case 0:
                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    //添加该页面可能的事项。比如跳转之类
                    if(wha == 1){

                    }
                    if(wha == 2) {
                        findformylaunch(1);
//                        
                    }
                    if(wha == 3) {
                        findformyreceive(1);

                    }
                    break;
                case 1:

                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    tv3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    //findformySos();
                    if(wha == 1){

                    }
                    if(wha == 2) {
                        findformylaunch(2);
                    }
                    if(wha == 3) {
                        findformyreceive(2);
                    }
                    break;
                case 2:
                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    //findformyques();
                    if(wha == 1){

                    }
                    if(wha == 2) {
                        findformylaunch(0);//参数0代表全部，1我发起的，2我接收的


                        //LinearLayout lyy = (LinearLayout)findViewById(R.id.lyyyy);
                        //lyy.addView(rlll);
                    }
                    if(wha == 3) {
                        findformyreceive(0);
                    }
                    break;
            }


        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    private void findformylaunch(int ty){
        int id = SharedPref.getInt("user_id", -1);

        if(id != -1){
            jsonStrng = "{" +
                    "\"id\":" + id + "," +"\"type\":\""+ty+"\"}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/event/query_launch", jsonStrng);
            if (message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            try{
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //未获取到
                            Toast.makeText(getApplicationContext(), "未查找到相关信息",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    //当获取到
                    Toast.makeText(getApplicationContext(), "萌萌哒",
                            Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(), "未登录",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void findformyreceive(int ty){
        int id = SharedPref.getInt("user_id", -1);
        if(id != -1){
        jsonStrng = "{" +
                "\"id\":" + id + "," +"\"type\":\""+2+"\"}";
        if (message == "false") {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //未获取到
                        Toast.makeText(getApplicationContext(), "未查找到相关信息",
                                Toast.LENGTH_SHORT).show();
                    }
                });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                return;
            }else {
                //当获取到
                Toast.makeText(getApplicationContext(), "萌萌哒",
                        Toast.LENGTH_SHORT).show();
                //根据返回的type值与ty来比较，然后决定显示哪些


            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        }else{
            Toast.makeText(getApplicationContext(), "未登录",
                    Toast.LENGTH_SHORT).show();
        }

    }
}
