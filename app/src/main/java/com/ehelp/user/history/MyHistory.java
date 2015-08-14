package com.ehelp.user.history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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
import android.widget.TextView;

import com.ehelp.R;
import com.ehelp.home.BaseFragment;
import com.ehelp.home.widget.PagerSlidingTabStrip;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
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

@AILayout(R.layout.activity_my_history)
public class MyHistory extends AIActionBarActivity implements
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
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
    private List<BaseFragment> fragments = new ArrayList<>();

    private String token = "false";
    private int wha = 1;//1代表发起，2代表接收，3代表关注
    private sosHistoryFragmentActivity sos = new sosHistoryFragmentActivity();
    private HelpHistoryFragmentActivity help = new HelpHistoryFragmentActivity();
    private QueHistoryFragmentActivity que = new QueHistoryFragmentActivity();
    private int position_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {

        initViews();
        fab();

        //获取reg id
        //get_rid();
        /*SharedPreferences spf = getApplicationContext().getSharedPreferences("user_id", Context.MODE_PRIVATE);
        int id = spf.getInt("user_id", -1);
        String s = String.valueOf(id);
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();*/


        // 收集activity，以便退出登录时集中销毁
        ActivityCollector.getInstance().addActivity(this);

        sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
    }

    private void fab() {
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
//        showToastMessage("clicked label: " + position);
//        rfabHelper.toggleContent();
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else if (position == 1) {
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
        } else if (position == 1) {
            Intent intent = new Intent(this, sendhelp_map.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        //mToolbar.setTitleTextColor(0xffececec);
        setSupportActionBar(mToolbar);
        TextView tvv = (TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("我的记录");

        //wha 1代表发起的 2代表接受的
        sos.setwha(wha);
        fragments.add(sos);
        help.setwha(wha);
        fragments.add(help);
        que.setwha(wha);
        fragments.add(que);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    /**
     * mPagerSlidingTabStrip榛樿鍊奸厤缃�
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
                (float) 0.1, getResources().getDisplayMetrics()));
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
        getMenuInflater().inflate(R.menu.menu_my_history, menu);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        //mShareActionProvider.setShareIntent(intent);
        return super.onCreateOptionsMenu(menu);
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
//            if (fragments.size() == 3) {
//                if (position_ == 0) {
//                    sos.changewha(wha);
//                } else if (position_ == 1) {
//                    help.changewha(wha);
//                } else {
//                    que.changewha(wha);
//                }
//            }

            sos.setwha(wha);
            help.setwha(wha);
            que.setwha(wha);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.accept) {
            //change to what i accepted
            wha = 2;
//            if (fragments.size() == 3) {
//                if (position_ == 0) {
//                    sos.changewha(wha);
//                } else if (position_ == 1) {
//                    help.changewha(wha);
//                } else {
//                    que.changewha(wha);
//                }
//            }
            sos.setwha(wha);
            help.setwha(wha);
            que.setwha(wha);
            return true;
        }
        if (id == R.id.concern) {
            wha = 3;
//            if (fragments.size() == 3) {
//                if (position_ == 0) {
//                    sos.changewha(wha);
//                } else if (position_ == 1) {
//                    help.changewha(wha);
//                } else {
//                    que.changewha(wha);
//                }
//            }
            sos.setwha(wha);
            help.setwha(wha);
            que.setwha(wha);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* ***************FragmentPagerAdapter***************** */
    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            position_ = position;
            fragments.get(position).setUserID(user_id);
            return fragments.get(position);
        }

    }

}