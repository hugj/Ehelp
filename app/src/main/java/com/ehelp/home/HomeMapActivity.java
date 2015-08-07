package com.ehelp.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
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
import com.ehelp.utils.RequestHandler;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.androidinject.annotation.annotations.base.AIView;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@AILayout(R.layout.activity_home_map)
public class HomeMapActivity extends BaseFragment implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {

    @AIView(R.id.rfab_group_sample_fragment_a_rfal)
    private RapidFloatingActionLayout rfaLayout;
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private int user_id;


    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private ACache eventCache;// event cache

    boolean isFirstLoc = true;// 是否首次定位

    MapView mMapView;
    BaiduMap mBaiduMap;

    private List<Event> events;
    public final static String EXTRA_MESSAGE = "event_id";

    // UI相关

    private static final String ARG_POSITION = "position";

    private int position;
    //private static final int[] drawables = { R.drawable.f, R.drawable.fi, R.drawable.f, R.drawable.fo};

    private List<Event> helpList;
    private List<Event> sosList;
    public double lon;
    public double lat;
    boolean isVaild = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mMapView = (MapView) getActivity().findViewById(R.id.bmapView);
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
    }

    public void setUserID(int id) {
        user_id = id;
    }

    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        showToastMessage("clicked label: " + position);
        rfabHelper.toggleContent();
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        showToastMessage("clicked icon: " + position);
        rfabHelper.toggleContent();
    }

    @Override
    public void onInitialRFAB(RapidFloatingActionButton rfab) {
        this.rfaButton = rfab;
    }


    @Override
    public String getRfabIdentificationCode() {
        return getString(R.string.rfab_identification_code_fragment_a);
    }

    @Override
    public String getTitle() {
        return "附近的人";
    }
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
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
