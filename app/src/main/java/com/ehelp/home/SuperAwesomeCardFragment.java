/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ehelp.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.map.recieve_help_ans_map;
import com.ehelp.map.recievesos_map;
import com.ehelp.receive.QuestionDetail;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SuperAwesomeCardFragment extends Fragment {
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    MapView mMapView;
    BaiduMap mBaiduMap;
    private int user_id;
    private List<Event> events;
    public final static String EXTRA_MESSAGE = "event_id";

    // UI相关
    boolean isFirstLoc = true;// 是否首次定位

    private static final String ARG_POSITION = "position";

    private int position;
    //private static final int[] drawables = { R.drawable.f, R.drawable.fi, R.drawable.f, R.drawable.fo};

    private List<Event> helpList;
    private List<Event> sosList;
    public double lon;
    public double lat;
    boolean isVaild = false;


    public static SuperAwesomeCardFragment newInstance(int position) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //SDKInitializer.initialize(getApplicationContext());
        position = getArguments().getInt(ARG_POSITION);
    }

    public void getUserID(int id) {
        user_id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);
        //fl.setBackgroundColor(0x666666);
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        TextView v = new TextView(getActivity());
        params.setMargins(margin, margin, margin, margin);
        v.setLayoutParams(params);
        v.setGravity(Gravity.TOP);
        System.out.println(position);
        if (position == 0) {
            fl.removeAllViews();
            mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
            mMapView = new MapView(getActivity());
            mMapView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.FILL_PARENT));
            fl.addView(mMapView);
            mBaiduMap = mMapView.getMap();
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            // 定位初始化
            mLocClient = new LocationClient(getActivity());
            mLocClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            mLocClient.setLocOption(option);
            mLocClient.start();
            int count = mMapView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = mMapView.getChildAt(i);
                if (child instanceof ZoomControls || child instanceof ImageView) {
                    child.setVisibility(View.INVISIBLE);
                }
            }
            setLocation();
        } else if (position == 1) {
            fl.removeAllViews();
            ListView queList = new ListView(getActivity());
            queList.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            HomeAdapter que = new HomeAdapter(getActivity(), user_id, 2);
            queList.setAdapter(que);
            queList.setDividerHeight(20);
            fl.addView(queList);

            events = que.getEvent();

            //绑定监听
            queList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                    Intent intent = new Intent(getActivity(), recievesos_map.class);
                    int eventid = events.get(index).getEventId();
                    intent.putExtra(EXTRA_MESSAGE, eventid);
                    startActivity(intent);
                }
            });
        } else
        if (position == 2) {
            fl.removeAllViews();
            ListView queList = new ListView(getActivity());
            queList.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            //queList.setBackgroundColor(0x666666);
            //queList.setAlpha(125);
            queList.setDividerHeight(20);
            HomeAdapter que = new HomeAdapter(getActivity(), user_id, 1);
            queList.setAdapter(que);
            fl.addView(queList);

            events = que.getEvent();

            //绑定监听
            queList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                    Intent intent = new Intent(getActivity(), recieve_help_ans_map.class);
                    intent.putExtra(EXTRA_MESSAGE, events.get(index));
                    startActivity(intent);
                }
            });
        } else
        if (position == 3) {
            fl.removeAllViews();
            ListView queList = new ListView(getActivity());
            queList.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            HomeAdapter que = new HomeAdapter(getActivity(), user_id, 0);
            queList.setAdapter(que);
            queList.setDividerHeight(20);
            fl.addView(queList);

            events = que.getEvent();

            //绑定监听
            queList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                    Intent intent = new Intent(getActivity(), QuestionDetail.class);
                    intent.putExtra("qusetiondatail", events.get(index));
                    startActivity(intent);
                }
            });
        }
        return fl;
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 15); // 更新定位焦点与缩放级别;
                mBaiduMap.animateMapStatus(u);
            }
            lon = location.getLongitude();
            lat = location.getLatitude();
            if (!isVaild) {
                send_info();
                isVaild = true;
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mMapView != null) {
            isFirstLoc = true; // 将画面焦点切换到定位点
            mMapView.onResume();
        }
        super.onResume();
    }


    @Override
    public void onDestroy() {
        // 退出时销毁定位
        if (mMapView != null) {
            mLocClient.stop();
            // 关闭定位图层
            mMapView.onDestroy();
            mMapView = null;
        }
        super.onDestroy();
    }
    public void setLocation() {
        HomeAdapter que1 = new HomeAdapter(getActivity(), user_id, 1);
        HomeAdapter que2 = new HomeAdapter(getActivity(), user_id, 2);
        helpList = que1.getEvent();
        sosList = que2.getEvent();

        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //遍历所有求助类型显示到地图上
        for (Event help : helpList) {
            LatLng pt = new LatLng(help.getLatitude(), help.getLongitude());
//          OverlayOptions o = new MarkerOptions().icon(bd).position(pt);
            MarkerOptions markerOptions = new MarkerOptions().icon(bd).position(pt);
            Marker mMarker1 = (Marker) (mBaiduMap.addOverlay(markerOptions));
            Bundle bundle = new Bundle();
            int type = 1;
            bundle.putSerializable("event", help);
            bundle.putInt("type", type);
            mMarker1.setExtraInfo(bundle);
        }

        //遍历所有sos信息
        for (Event sos : sosList) {
            LatLng pt = new LatLng(sos.getLatitude(), sos.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions().icon(bd).position(pt);
            builder.include(pt);
            Marker mMarker1 = (Marker) (mBaiduMap.addOverlay(markerOptions));
            Bundle bundle = new Bundle();
            int eventid = sos.getEventId();
            int type = 2;
            bundle.putInt("eventid", eventid);
            bundle.putInt("type", type);
            mMarker1.setExtraInfo(bundle);
        }
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity().getApplicationContext(), "Marker被点击了！", Toast.LENGTH_SHORT).show();
                int type = (int) marker.getExtraInfo().get("type");
                if (type == 2) {
                    int eventid = (int) marker.getExtraInfo().get("eventid");
                    Intent intent = new Intent(getActivity(), recievesos_map.class);
                    intent.putExtra(EXTRA_MESSAGE, eventid);
                    startActivity(intent);
                } else {
                    Event event = (Event) marker.getExtraInfo().get("event");
                    Intent intent = new Intent(getActivity(), recieve_help_ans_map.class);
                    intent.putExtra(EXTRA_MESSAGE, event);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    //初次传输地理位置信息
    public void send_info() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());
        String jsonStrng = "{" +
                "\"id\":" + user_id + ",\"type\":1," +
                "\"longitude\":" +  lon + "," +
                "\"latitude\":" + lat + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/modify_information", jsonStrng);
        if (message == "false") {
            Toast.makeText(getActivity().getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }else {
            JSONObject jO = null;
            try {
                jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    Toast.makeText(getActivity().getApplicationContext(), "fuck",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "good job",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

