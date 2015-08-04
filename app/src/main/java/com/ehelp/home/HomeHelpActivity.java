package com.ehelp.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.map.recieve_help_ans_map;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.listener.OnRapidFloatingButtonSeparateListener;

import java.util.List;

@AILayout(R.layout.activity_home_help)
public class HomeHelpActivity extends BaseFragment implements OnRapidFloatingButtonSeparateListener
        , SwipeRefreshLayout.OnRefreshListener{

    private RapidFloatingActionButton rfaButton;
    private int user_id;
    private List<Event> events;
    public final static String EXTRA_MESSAGE = "event_id";
    private static final int REFRESH_COMPLETE = 2;
    private ACache eventCache;// event cache
    private SwipeRefreshLayout mSwipeLayout;
    private ListView SOSList;
    private HomeAdapter SOS;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        if (null == rfaButton) {
            return;
        }
        rfaButton.setOnRapidFloatingButtonSeparateListener(this);
    }

    public void init() {
        // initalize Listview and SwipeLayout
        mSwipeLayout = (SwipeRefreshLayout) findViewById_(R.id.id_swipe_ly);
        SOSList = (ListView)findViewById_(R.id.HomeSOS);
        SOSList.setDividerHeight(20);

        // get eventCache
        eventCache = ACache.get(getActivity());
//
//        try {
//            mDatas = getTitleList();
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        setList();

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_green_dark, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }

    public void onRefresh()
    {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_COMPLETE:
                    eventCache = SOS.getRemoteTitleList(2);
                    setList(); // change the data in listview
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
            super.handleMessage(msg);
        };
    };

    public void setList(){
        SOS = new HomeAdapter(getActivity(), user_id, 1, eventCache);
        SOSList.setAdapter(SOS);

        events = SOS.getEvent();

        //绑定监听
        SOSList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                Intent intent = new Intent(getActivity(), recieve_help_ans_map.class);
                intent.putExtra(EXTRA_MESSAGE, events.get(index));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onInitialRFAB(RapidFloatingActionButton rfab) {
        this.rfaButton = rfab;
        if (null == rfaButton) {
            return;
        }
        rfaButton.setOnRapidFloatingButtonSeparateListener(this);
    }

    @Override
    public String getRfabIdentificationCode() {
        return getString(R.string.rfab_identification_code_fragment_b);
    }

    @Override
    public String getTitle() {
        return "求助";
    }

    public void setUserID(int id) {
        user_id = id;
    }
    @Override
    public void onRFABClick() {
        showToastMessage("B RFAB clicked");
    }
}
