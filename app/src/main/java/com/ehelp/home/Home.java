package com.ehelp.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.ehelp.R;
import com.ehelp.home.widget.PagerSlidingTabStrip;
import com.ehelp.send.SendHelp;
import com.ehelp.send.SendQuestion;
import com.ehelp.send.SendSOS;
import com.ehelp.user.Myhistory;
import com.ehelp.user.pinyin.ContactlistActivity;
import com.ehelp.user.pinyin.Health;
import com.ehelp.user.pinyin.SettingActivity;
import com.ehelp.user.pinyin.homepageActivity;
import com.ehelp.utils.ActivityCollector;
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

import cn.jpush.android.api.JPushInterface;

@AILayout(R.layout.activity_home)
public class Home extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private int user_id;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        init();
    }

    private void init() {
        // 使用后台线程运行网络连接功能
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //极光推送初始化
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        SDKInitializer.initialize(getApplicationContext());
        initViews();
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

        //获取reg id
        //get_rid();


        // 收集activity，以便退出登录时集中销毁
        ActivityCollector.getInstance().addActivity(this);

        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
    }
    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
//        showToastMessage("clicked label: " + position);
//        rfabHelper.toggleContent();
        if (position == 0) {
            Intent intent = new Intent(this, SendSOS.class);
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
            Intent intent = new Intent(this, SendSOS.class);
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

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //mToolbar.setTitle("");
        //mToolbar.setTitleTextColor(0xffececec);
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("Ehelp");

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Toast.makeText(getApplicationContext(), "action_settings",
                                Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		/* findView */
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
///*

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                //colorChange(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        initTabsValue();
    }

    public void ContactList(View view) {

        Intent intent = new Intent(this, ContactlistActivity.class);
        startActivity(intent);
    }

    public void user_page_click(View v) {
        Intent intent = new Intent(this, homepageActivity.class);
        startActivity(intent);
    }

    public void health(View view) {
        Intent intent = new Intent(this, Health.class);
        startActivity(intent);
    }

    public void history(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, Myhistory.class);
        startActivity(intent);
    }
    public void Setting_page_click(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }
    /**
     * mPagerSlidingTabStrip榛樿鍊奸厤缃�
     *
     */
    private void initTabsValue() {
        // 搴曢儴娓告爣棰滆壊
        mPagerSlidingTabStrip.setIndicatorColor(Color.BLUE);
        // tab鐨勫垎鍓茬嚎棰滆壊
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab鑳屾櫙
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#4876FF"));
        // tab搴曠嚎楂樺害
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float)0.1, getResources().getDisplayMetrics()));
        // 娓告爣楂樺害
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics()));
        // 閫変腑鐨勬枃瀛楅鑹�
        mPagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
        // 姝ｅ父鏂囧瓧棰滆壊
        mPagerSlidingTabStrip.setTextColor(Color.parseColor("#A2B5FF"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        //mShareActionProvider.setShareIntent(intent);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // switch (item.getItemId()) {
        // case R.id.action_settings:
        // Toast.makeText(MainActivity.this, "action_settings", 0).show();
        // break;
        // case R.id.action_share:
        // Toast.makeText(MainActivity.this, "action_share", 0).show();
        // break;
        // default:
        // break;
        // }
        return super.onOptionsItemSelected(item);
    }

    /* ***************FragmentPagerAdapter***************** */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = { "附近的人", "求救", "求助", "提问" };

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            SuperAwesomeCardFragment it = SuperAwesomeCardFragment.newInstance(position);
            it.getUserID(user_id);
            return it;
        }

    }

    /*private void get_rid_() {
        SharedPreferences spf = getApplicationContext().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        String id = spf.getString("user_id", "default");
        if (id == "default") {
            Toast.makeText(getApplicationContext(), "id获取失败", Toast.LENGTH_LONG).show();
        }

        String identity_id = JPushInterface.getRegistrationID(getApplicationContext());

        String jsonString = "{" + "\"id\":\""+ id +"\"," + "\"identity_id\":\"" + identity_id + "\"" + "}";
        String msg = RequestHandler.sendPostRequest("http://120.24.208.130:1501/user/modify_information", jsonString);
        if (msg == "false") {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            get_rid_();
        }
    };
    public void get_rid() {
        new Thread(runnable).start();
    }*/


}
