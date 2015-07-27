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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.ehelp.receive.QuestionDetail;

public class SuperAwesomeCardFragment extends Fragment {
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    boolean isFirstLoc = true;// 是否首次定位

    private static final String ARG_POSITION = "position";

    private int position;
    //private static final int[] drawables = { R.drawable.f, R.drawable.fi, R.drawable.f, R.drawable.fo};

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        FrameLayout fl = new FrameLayout(getActivity());
        fl.setLayoutParams(params);
        //fl.setBackgroundResource(drawables[position]);
        final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());

        TextView v = new TextView(getActivity());
        params.setMargins(margin, margin, margin, margin);
        v.setLayoutParams(params);
        v.setGravity(Gravity.TOP);
        if (position == 0) {
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
        } else if (position == 1) {
            v.setText("救命！！！");
            fl.addView(v);
        } else
        if (position == 2) {
            v.setText("谁来帮帮我");
            fl.addView(v);
        } else {
            ListView queList = new ListView(getActivity());
            queList.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
            HomeAdapter que = new HomeAdapter(getActivity());
            queList.setAdapter(que);
            fl.addView(queList);
            //绑定监听
            queList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                    if (index == 0) {
                        Intent intent = new Intent(getActivity(), QuestionDetail.class);
                        startActivity(intent);
                    }
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

}

