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
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.map.recieve_help_ans_map;
import com.ehelp.map.recievesos_map;
import com.ehelp.receive.QuestionDetail;

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
    public final static String EXTRA_MESSAGE = "com.ehelp.home.MESSAGE";

    // UI相关
    boolean isFirstLoc = true;// 是否首次定位

    private static final String ARG_POSITION = "position";

    private int position;
    //private static final int[] drawables = { R.drawable.f, R.drawable.fi, R.drawable.f, R.drawable.fo};

    //test point
    private Marker mMarker1;
    private Marker mMarker2;
    private Marker mMarker3;
    private Marker mMarker4;
    private Marker mMarker5;
    private Marker mMarker6;
    private InfoWindow mInfoWindow;

    private List<Event> sosList;
    private List<Event> helpList;

    public double lon;
    public double la;


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
            //init();
            int count = mMapView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = mMapView.getChildAt(i);
                if (child instanceof ZoomControls || child instanceof ImageView) {
                    child.setVisibility(View.INVISIBLE);
                }
            }
            setLocation();
//            mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
//                public boolean onMarkerClick(final Marker marker) {
//                    Button button = new Button(getActivity().getApplicationContext());
//                    button.setBackgroundResource(R.drawable.popup);
//                    InfoWindow.OnInfoWindowClickListener listener = null;
//                    if (marker == mMarker1) {
//                        button.setText("这里是跳转");
//                        button.setTextColor(Color.BLACK);
//                    /*这里是跳转button
//                    listener = new InfoWindow.OnInfoWindowClickListener() {
//                        public void onInfoWindowClick() {
//                            LatLng ll = marker.getPosition();
//                            LatLng llNew = new LatLng(ll.latitude + 0.005,
//                                    ll.longitude + 0.005);
//                            marker.setPosition(llNew);
//                            mBaidumap.hideInfoWindow();
//                        }
//                    };
//                    */
//                        LatLng ll = marker.getPosition();
//                        mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
//                        mBaiduMap.showInfoWindow(mInfoWindow);
//                    } else if (marker == mMarker2) {
//                        button.setText("这里是跳转");
//                        button.setTextColor(Color.BLACK);
//                    /*这里是跳转button
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.setIcon(bd);
//                            mBaiduMap.hideInfoWindow();
//                        }
//
//                    });
//                    */
//                        LatLng ll = marker.getPosition();
//                        mInfoWindow = new InfoWindow(button, ll, -47);
//                        mBaiduMap.showInfoWindow(mInfoWindow);
//                    } else if (marker == mMarker3) {
//                        button.setText("这里是跳转");
//                        button.setTextColor(Color.BLACK);
//                    /*这里是跳转button
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.remove();
//                            mBaidumap.hideInfoWindow();
//                        }
//                    });
//                    */
//                        LatLng ll = marker.getPosition();
//                        mInfoWindow = new InfoWindow(button, ll, -47);
//                        mBaiduMap.showInfoWindow(mInfoWindow);
//                    } else if (marker == mMarker4) {
//                        button.setText("这里是跳转");
//                        button.setTextColor(Color.BLACK);
//                    /*这里是跳转button
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.remove();
//                            mBaidumap.hideInfoWindow();
//                        }
//                    });
//                    */
//                        LatLng ll = marker.getPosition();
//                        mInfoWindow = new InfoWindow(button, ll, -47);
//                        mBaiduMap.showInfoWindow(mInfoWindow);
//                    } else if (marker == mMarker5) {
//                        button.setText("这里是跳转");
//                        button.setTextColor(Color.BLACK);
//                    /*这里是跳转button
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.remove();
//                            mBaidumap.hideInfoWindow();
//                        }
//                    });
//                    */
//                        LatLng ll = marker.getPosition();
//                        mInfoWindow = new InfoWindow(button, ll, -47);
//                        mBaiduMap.showInfoWindow(mInfoWindow);
//                    } else if (marker == mMarker6) {
//                        button.setText("这里是跳转");
//                        button.setTextColor(Color.BLACK);
//                    /*这里是跳转button
//                    button.setOnClickListener(new View.OnClickListener() {
//                        public void onClick(View v) {
//                            marker.remove();
//                            mBaidumap.hideInfoWindow();
//                        }
//                    });
//                    */
//                        LatLng ll = marker.getPosition();
//                        mInfoWindow = new InfoWindow(button, ll, -47);
//                        mBaiduMap.showInfoWindow(mInfoWindow);
//                    }
//                    return true;
//                }
//            });
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

//    public void init() {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        // 暂时提供三个点标注在地图上作为例子
//        LatLng pt1 = new LatLng(23.063309, 113.394004);
//        LatLng pt2 = new LatLng(23.052578, 113.410821);
//        LatLng pt3 = new LatLng(23.075286, 113.425934);
//        LatLng pt4 = new LatLng(23.055286, 113.435934);
//        LatLng pt5 = new LatLng(23.045286, 113.415934);
//        LatLng pt6 = new LatLng(23.245286, 113.435934);
//
//        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
//        OverlayOptions o1 = new MarkerOptions().icon(bd).position(pt1);
//        OverlayOptions o2 = new MarkerOptions().icon(bd).position(pt2);
//        OverlayOptions o3 = new MarkerOptions().icon(bd).position(pt3);
//        OverlayOptions o4 = new MarkerOptions().icon(bd).position(pt4);
//        OverlayOptions o5 = new MarkerOptions().icon(bd).position(pt5);
//        OverlayOptions o6 = new MarkerOptions().icon(bd).position(pt6);
//
//        mBaiduMap.addOverlay(o1);
//        mBaiduMap.addOverlay(o2);
//        mBaiduMap.addOverlay(o3);
//        mBaiduMap.addOverlay(o4);
//        mBaiduMap.addOverlay(o5);
//        mBaiduMap.addOverlay(o6);
//
//        builder.include(pt1);
//        builder.include(pt2);
//        builder.include(pt3);
//        builder.include(pt4);
//        builder.include(pt5);
//        builder.include(pt6);
//
//
//        mMarker1 = (Marker) (mBaiduMap.addOverlay(o1));
//        mMarker2 = (Marker) (mBaiduMap.addOverlay(o2));
//        mMarker3 = (Marker) (mBaiduMap.addOverlay(o3));
//        mMarker4 = (Marker) (mBaiduMap.addOverlay(o3));
//        mMarker5 = (Marker) (mBaiduMap.addOverlay(o3));
//        mMarker6 = (Marker) (mBaiduMap.addOverlay(o3));
//    }

    public void setLocation() {
        HomeAdapter que1 = new HomeAdapter(getActivity(), user_id, 1);
        HomeAdapter que2 = new HomeAdapter(getActivity(), user_id, 2);
        helpList = que1.getEvent();
        sosList = que2.getEvent();
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Event help : helpList) {
            LatLng pt = new LatLng(help.getLatitude(), help.getLongitude());
            OverlayOptions o = new MarkerOptions().icon(bd).position(pt);
            mBaiduMap.addOverlay(o);
            builder.include(pt);
            mMarker1 = (Marker) (mBaiduMap.addOverlay(o));
        }

        for (Event help : sosList) {
            LatLng pt = new LatLng(help.getLatitude(), help.getLongitude());
            OverlayOptions o = new MarkerOptions().icon(bd).position(pt);
            mBaiduMap.addOverlay(o);
            builder.include(pt);
            mMarker1 = (Marker) (mBaiduMap.addOverlay(o));
        }
    }
}

