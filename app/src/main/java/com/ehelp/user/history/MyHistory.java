package com.ehelp.user.history;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.map.sendhelp_map;
import com.ehelp.map.sendhelpcomeback_map;
import com.ehelp.map.sendsos_map;
import com.ehelp.receive.QuestionDetail;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
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

@AILayout(R.layout.activity_my_history)
public class MyHistory extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    private SharedPreferences sharedPref;
    private int user_id;
    private List<Event> events;

    private ViewPager mPager;//页卡内容
    private List<View> listViews; // Tab页面列表
    private TextView t1, t2, t3;// 页卡头标
    //后台连接
    private SharedPreferences SharedPref;
    private String message;
    private String jsonStrng;

    //toolbar
    private Toolbar mToolbar;

    private int wha = 1;//1代表发起，2代表接收
    public final static String EXTRA_MESSAGE = "com.ehelp.user.history.MESSAGE";

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

        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
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
        if (id == R.id.sponsor) {
            //change to my luancd
            wha = 1;
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.accept) {
            //change to what i accepted
            wha = 2;
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

                    ListView hisList = (ListView)findViewById(R.id.lay1);
                    //wha 1代表发起的 2代表接受的
                    HistoryAdapter his = new HistoryAdapter(MyHistory.this, user_id, wha, 1);
                    hisList.setAdapter(his);

                    events = his.getEvent();

                    //绑定监听
                    hisList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                            Intent intent = new Intent(MyHistory.this, sendhelpcomeback_map.class);
                            int eventID = events.get(index).getEventId();
                            intent.putExtra(EXTRA_MESSAGE, eventID);
                            startActivity(intent);
                        }
                    });
                    break;
                case 1:

                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    tv3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                    ListView hisList2 = (ListView)findViewById(R.id.lay2);
                    //wha 1代表发起的 2代表接受的
                    HistoryAdapter his2 = new HistoryAdapter(MyHistory.this, user_id, wha, 2);
                    hisList2.setAdapter(his2);

                    events = his2.getEvent();

                    //绑定监听
                    hisList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                            Intent intent = new Intent(MyHistory.this, sendsos_map.class);
                            int eventID = events.get(index).getEventId();
                            intent.putExtra(EXTRA_MESSAGE, eventID);
                            startActivity(intent);
                        }
                    });
                    break;
                case 2:
                    tv.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tv3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                    ListView hisList3 = (ListView)findViewById(R.id.lay3);
                    //wha 1代表发起的 2代表接受的
                    HistoryAdapter his3 = new HistoryAdapter(MyHistory.this, user_id, wha, 0);
                    hisList3.setAdapter(his3);

                    events = his3.getEvent();

                    //绑定监听
                    hisList3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                            Intent intent = new Intent(MyHistory.this, QuestionDetail.class);
                            int eventID = events.get(index).getEventId();
                            intent.putExtra("qusetiondatail", events.get(index));
                            startActivity(intent);
                        }
                    });
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
}
