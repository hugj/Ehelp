package com.ehelp.map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.user.healthcard.OthershealthcardActivity;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


public class recievesos_map extends ActionBarActivity implements BaiduMap.OnMapClickListener,
        OnGetRoutePlanResultListener {

    ImageView Image;
    //浏览路线节点相关
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    PlanNode stNode = null;

    private EditText Eans;
    private String ans;


    //
    MapView mMapView = null;    // map View
    BaiduMap mBaidumap = null;
    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

    //定位相关
    LocationClient mLocClient;
    boolean isFirstLoc = true;// 是否首次定位
    public MyLocationListenner myListener = new MyLocationListenner();

    //信息相关
    private Marker mMarker1;
    private Marker mMarker2;
    private Marker mMarker3;
    private Marker mMarker4;
    private InfoWindow mInfoWindow;
    LatLng loc;

    //TOOLbar
    private Toolbar mToolbar;

    //用于后台数据交互
    private String message;
    private String message1;
    private String jsonStrng;
    private int idd;//发起者用户ID

    private int event_id;
    private int user_id;
    private String url = "http://120.24.208.130:1501/user/event_manage";
    private SharedPreferences sp;

    private Menu menu_recievesos_map;

    private Gson gson = new Gson();
    public LatLng end_node = null;

    private Event event;

    protected void onCreate(Bundle savedInstanceState) {
        init2();
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_recievesos_map);

        event_id = this.getIntent().getIntExtra("event_id", -1);
        String s = String.valueOf(event_id);
        Log.v("receiversostest", s);

        CharSequence titleLable = "路线规划";
        setTitle(titleLable);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        int count = mMapView.getChildCount();

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("求救信息详情");

        init();

        //显示信息详情
        showdetail();
        // 去除无关图标
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        // 定位相关
        mBaidumap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        //-----------------------

        mBaidumap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            public boolean onMarkerClick(final Marker marker) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.popup);
                InfoWindow.OnInfoWindowClickListener listener = null;
                if (marker == mMarker1) {
                    button.setText("这里是跳转");
                    button.setTextColor(Color.BLACK);
                    /*这里是跳转button
                    listener = new InfoWindow.OnInfoWindowClickListener() {
                        public void onInfoWindowClick() {
                            LatLng ll = marker.getPosition();
                            LatLng llNew = new LatLng(ll.latitude + 0.005,
                                    ll.longitude + 0.005);
                            marker.setPosition(llNew);
                            mBaidumap.hideInfoWindow();
                        }
                    };
                    */
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
                    mBaidumap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarker2) {
                    button.setText("这里是跳转");
                    button.setTextColor(Color.BLACK);
                    /*这里是跳转button
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            marker.setIcon(bd);
                            mBaiduMap.hideInfoWindow();
                        }

                    });
                    */
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaidumap.showInfoWindow(mInfoWindow);
                } else if (marker == mMarker3) {
                    button.setText("这里是跳转");
                    button.setTextColor(Color.BLACK);
                    /*这里是跳转button
                    button.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            marker.remove();
                            mBaidumap.hideInfoWindow();
                        }
                    });
                    */
                    LatLng ll = marker.getPosition();
                    mInfoWindow = new InfoWindow(button, ll, -47);
                    mBaidumap.showInfoWindow(mInfoWindow);
                }
                return true;
            }
        });

        sp = this.getSharedPreferences("user_id", MODE_PRIVATE);
        user_id = sp.getInt("user_id", -1);
    }

    public void healthCardClick(View v){
        Intent intent = new Intent(this, OthershealthcardActivity.class);
        intent.putExtra("user_id",idd);
        startActivity(intent);
    }

    protected void showdetail(){
        jsonStrng = "{" +
                "\"event_id\":" + event_id + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/get_information", jsonStrng);
        int i=1;
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
                        Toast.makeText(getApplicationContext(), "无此事件",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else {
                Toast.makeText(getApplicationContext(), "查询成功",
                        Toast.LENGTH_SHORT).show();
                //修改显示的时间
                TextView tv1 =(TextView) findViewById(R.id.SOStime);
                tv1.setText(jO.getString("time"));
                //地址的文字信息
                TextView tv2 =(TextView) findViewById(R.id.SOSlocation);
                tv2.setText("");
                //健康问题or安全问题
                TextView tv3 =(TextView) findViewById(R.id.problem);
                if(jO.getInt("demand_number") == 1){
                    tv3.setText("健康问题");
                }
                if(jO.getInt("demand_number") == 2){
                    tv3.setText("安全问题");
                }

                //通过发起者id寻找发起者用户名并显示
                idd = jO.getInt("launcher_id");
                findforusername();
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void findforusername(){
        if( idd != -1){
            jsonStrng = "{" +
                    "\"id\":" + idd + "}";
            message1 = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/get_information", jsonStrng);
            if (message1 == "false") {
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
                JSONObject jO1 = new JSONObject(message1);
                if (jO1.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "查询用户名错误",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }else {
                    Toast.makeText(getApplicationContext(), "查询用户名成功",
                            Toast.LENGTH_SHORT).show();
                    //修改显示的用户名
                    TextView tv3 =(TextView) findViewById(R.id.SOSusername);
                    if(jO1.getString("nickname") !="") {
                        tv3.setText(jO1.getString("nickname"));
                    }else if(jO1.getString("name")!=""){
                        tv3.setText(jO1.getString("name"));
                    }else{
                        tv3.setText(jO1.getString("phone"));
                    }

                    return;
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "无此用户",
                    Toast.LENGTH_SHORT).show();
        }
    }
    //设置TOOLBAR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_comment){
            //点击评论按钮
        }

        if (id == R.id.action_video) {
            Intent mIntent = new Intent();
            mIntent.putExtra("event_id", event_id);
            mIntent.setClass(recievesos_map.this, VideoActivity.class);
            startActivity(mIntent);
        }

        //noinspection SimplifiableIfStatement
        if (item.getTitle().toString().equals("回应")||item.getTitle().toString().equals("关注")){
            int operation = 0;
            if(id == R.id.action_concern)
                operation = 1;
            if(id == R.id.action_respond)
                operation = 2;
            String send = "{\"id\":" + user_id + ",\"event_id\":"
                    + event_id + ",\"operation\":" + operation + "}";
            String msg = RequestHandler.sendPostRequest(
                    url, send);
            Log.v("receiversostest", msg);
            if(msg == "false"){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
            try{
                JSONObject jO = new JSONObject(msg);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "操作失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }else if(jO.getInt("status") == 200){
                    Toast.makeText(getApplicationContext(), "操作成功",
                            Toast.LENGTH_SHORT).show();
                    Menu menu =menu_recievesos_map;//(Menu)findViewById(R.id.menu_recievesos_map);
                    if(id == R.id.action_respond) {
                        MenuItem item_concern = menu.findItem(R.id.action_concern);
                        item.setTitle("取消回应");
                        //item_concern.setTitle("不可关注");
                        item_concern.setVisible(false);
                    }
                    if(id == R.id.action_concern) {
                        MenuItem item_respond = menu.findItem(R.id.action_respond);
                        item.setTitle("取消关注");
                        //item_respond.setTitle("不可回应");
                        item_respond.setVisible(false);
                    }
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        if ((item.getTitle() =="取消回应")||(item.getTitle() =="取消关注")) {
            int operation = 0;
            String send = "{\"id\":" + user_id + ",\"event_id\":"
                    + event_id + ",\"operation\":" + operation + "}";
            String msg = RequestHandler.sendPostRequest(
                    url, send);
            Log.v("receiversostest", msg);
            if(msg == "false"){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }
            try{
                JSONObject jO = new JSONObject(msg);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "操作成功",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }else if(jO.getInt("status") == 200){
                    Toast.makeText(getApplicationContext(), "操作成功",
                            Toast.LENGTH_SHORT).show();
                    //if(id == R.id.action_respond)
                    //{
                    //item.setTitle("回应");

                    // }
                    //if(id == R.id.action_concern) {
                    //MenuItem it =menu.findItem(R.id.action_ans);//
                    Menu menu =menu_recievesos_map;//(Menu)findViewById(R.id.menu_recievesos_map);
                    MenuItem item_respond = menu.findItem(R.id.action_respond);
                    item_respond.setVisible(true);
                    item_respond.setTitle("回应");
                    MenuItem item_concern = menu.findItem(R.id.action_concern);
                    item_concern.setVisible(true);
                    item_concern.setTitle("关注");
                    //}
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recievesos_map, menu);
        menu_recievesos_map = menu;
        return true;
    }
//toolbar设置结束
    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public void SearchButtonProcess(View v) {
        //重置浏览节点的路线数据
        route = null;
        mBaidumap.clear();
        // 处理搜索按钮响应
        init();
        PlanNode enNode = PlanNode.withLocation(end_node);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions o2 = new MarkerOptions().icon(bd).position(end_node);
        mBaidumap.addOverlay(o2);


        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.drive) {
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        }
    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(recievesos_map.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {}

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(recievesos_map.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }


//    @Override
//    protected void onPause() {
//        mMapView.onPause();
//        super.onPause();
//    }

    //    @Override
//    protected void onResume() {
//        mMapView.onResume();
//        super.onResume();
//    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }
//
//    @Override
//    protected void onDestroy() {
//        mSearch.destroy();
//        mMapView.onDestroy();
//        super.onDestroy();
//    }


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
            mBaidumap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaidumap.animateMapStatus(u);
            }
            LatLng pt1 = new LatLng(location.getLatitude(),
                    location.getLongitude());
            stNode = PlanNode.withLocation(pt1);
            loc = pt1;
        }

        public void onReceivePoi(BDLocation poiLocation) {}
    }

    public void init() {
        String jsonString = "{" +
                "\"event_id\":" + event_id + "}";
        String message = RequestHandler.sendPostRequest("http://120.24.208.130:1501/event/get_information", jsonString);
        if (message.equals("false")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "无法获取数据，请检查网络连接",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "获取周围用户信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                event = gson.fromJson(message, Event.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 根据所得到的信息调用函数绘制标记
        end_node = new LatLng(event.getLatitude(), event.getLongitude());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions o1 = new MarkerOptions().icon(bd).position(end_node);

        mBaidumap.addOverlay(o1);

        builder.include(end_node);

        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());
    }

    public void init2() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());
    }
}
