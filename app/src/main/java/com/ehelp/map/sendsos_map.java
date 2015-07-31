package com.ehelp.map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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

//手机振动与手机发声
import android.os.Vibrator;
//import android.media.SoundPool;
//import android.media.AudioManager;
//统计代码
import cn.jpush.android.api.JPushInterface;
//极光推送
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
//严苛模式
import android.os.StrictMode;

import com.ehelp.utils.RequestHandler;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;


public class sendsos_map extends ActionBarActivity implements BaiduMap.OnMapClickListener{


    MapView mMapView = null;    // map View
    BaiduMap mBaidumap = null;


    //定位相关
    LocationClient mLocClient;
    boolean isFirstLoc = true;// 是否首次定位
    public MyLocationListenner myListener = new MyLocationListenner();

    //信息相关
    private Marker mMarker1;
    private Marker mMarker2;
    private Marker mMarker3;
    private Marker mMarker4;
    private Marker mMarker5;
    private Marker mMarker6;
    private InfoWindow mInfoWindow;

    //经纬度
    private double longitude;
    private double latitude;
    //存储事件id
    private int event_id;

    //求救页面信息框
    private EditText et1;

    //Toolbar
    private Toolbar mToolbar;

    //振动发声定义
    private Button button7;
    //private SoundPool sp;
    private Vibrator vib;

    //停止振动发声
    public void Stopvands(View view) {
        this.finish();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_sendsos_map);

        this.sendBroadcast(getIntent());

        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        int count = mMapView.getChildCount();
        init();

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("紧急求救状态");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        // 去除无关图标
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls || child instanceof ImageView) {
                child.setVisibility(View.INVISIBLE);
            }
        }
        // 地图点击事件处理
        mBaidumap.setOnMapClickListener(this);

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

        //发送求救信息到后台
        try {
            sendsos();
        } catch (JSONException j) {
            j.printStackTrace();
        }

        //推送求救信息
        thread.start();
        //振动发声
        vibandsp();
        //sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //sp.load(getApplicationContext(), R.raw.alarm, 1);
    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */


    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }
    /*/设置TOOLBAR
    这个界面右上角无按钮

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            MenuItem it =(MenuItem)findViewById(R.id.action_ans);
            it.setTitle("取消回应");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sendsos_map, menu);
        return true;
    }
    //toolbar设置结束    */
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        JPushInterface.onResume(this);
    }

    //摧毁页面
    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        vib.cancel();
        cancelsos();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onDestroy();
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
            mBaidumap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaidumap.animateMapStatus(u);
                longitude = location.getLatitude();
                latitude = location.getLatitude();
            }

            //获取当前经纬度
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    }

    public void init() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // 暂时提供三个点标注在地图上作为例子
        LatLng pt1 = new LatLng(23.063309, 113.394004);
        LatLng pt2 = new LatLng(23.052578, 113.410821);
        LatLng pt3 = new LatLng(23.075286, 113.425934);
        LatLng pt4 = new LatLng(23.055286, 113.435934);
        LatLng pt5 = new LatLng(23.045286, 113.415934);
        LatLng pt6 = new LatLng(23.245286, 113.435934);

        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions o1 = new MarkerOptions().icon(bd).position(pt1);
        OverlayOptions o2 = new MarkerOptions().icon(bd).position(pt2);
        OverlayOptions o3 = new MarkerOptions().icon(bd).position(pt3);
        OverlayOptions o4 = new MarkerOptions().icon(bd).position(pt4);
        OverlayOptions o5 = new MarkerOptions().icon(bd).position(pt5);
        OverlayOptions o6 = new MarkerOptions().icon(bd).position(pt6);

        mBaidumap.addOverlay(o1);
        mBaidumap.addOverlay(o2);
        mBaidumap.addOverlay(o3);
        mBaidumap.addOverlay(o4);
        mBaidumap.addOverlay(o5);
        mBaidumap.addOverlay(o6);

        builder.include(pt1);
        builder.include(pt2);
        builder.include(pt3);
        builder.include(pt4);
        builder.include(pt5);
        builder.include(pt6);


        mMarker1 = (Marker) (mBaidumap.addOverlay(o1));
        mMarker2 = (Marker) (mBaidumap.addOverlay(o2));
        mMarker3 = (Marker) (mBaidumap.addOverlay(o3));
        mMarker4 = (Marker) (mBaidumap.addOverlay(o3));
        mMarker5 = (Marker) (mBaidumap.addOverlay(o3));
        mMarker6 = (Marker) (mBaidumap.addOverlay(o3));
    }

    //开始振动发声
    private void vibandsp() {
        //手机振动发声
        //振动代码
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {1000, 1000, 1000, 1000};//设定振动模式，单位:ms
        vib.vibrate(pattern, 2);

        //发声代码
        //sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        //int i  = sp.load(getApplicationContext(), R.raw.alarm, 1);
        //String s = String.valueOf(i);
        //Toast t = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG);
        //t.show();
        //sp.play(1, 1, 1, 0, -1, 1);
    }

    //极光推送
    public static String sendPostRequest(String urlString, String jsondata){

        try {

            //StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
            //StrictMode.setThreadPolicy(policy);

            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true); // permit to use the inputstream
            conn.setDoOutput(true); // permit to use the outputstrem
            conn.setUseCaches(false); // deny to use the cache
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            String auth = "ODcyM2QxNjAzMGNhODZkNTMwYjM1ZTIyOjdkZmIxYmMzN2FiYjcyODBmNzdjMWNjNw==";
            conn.setRequestProperty("Content-Type", "application/json");// set the request Content-Type
            conn.setRequestProperty("Authorization", "Basic " + auth);

            byte data[] = jsondata.getBytes("UTF-8"); // use utf-8 coding format to transformat string to a byte array
            conn.getOutputStream().write(data);

            StringBuffer sBuffer = new StringBuffer();
            if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                String line = null;
                InputStream in = conn.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(in));
                while((line = bReader.readLine()) != null) {
                    sBuffer.append(line);
                }
                return sBuffer.toString();
            }

        } catch (Exception e) {

            e.printStackTrace();
            System.out.print("error");
        }

        return "false";

    }

    //在新的线程中推送信息
    Thread thread =new Thread(new Runnable() {
        @Override
        public void run() {
            SharedPreferences spf = getApplicationContext().getSharedPreferences("user_id", Context.MODE_PRIVATE);
            int id = spf.getInt("user_id", -1);
            String s1 = String.valueOf(id);
            Log.v("sendposttest", s1);
            /*if (id == -1) {
                Toast.makeText(getApplicationContext(), "id获取失败", Toast.LENGTH_LONG).show();
            }*/

            /*if (longitude == 0 || latitude == 0) {
                Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                        Toast.LENGTH_SHORT).show();
            }*/

            //String locmsg = "{" + "\"id\":" + id + "}";
            //String s2 = "37";
            String locmsg = "{\"id\":"+ s1 + "}";
            String msg = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/neighbor", locmsg);
            //Log.v("Ehelp", msg);
            Log.v("sendposttest", msg);
            /*if (msg == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }*/
            String jsonString;
            int t1 = msg.indexOf("]");
            int t2 = msg.indexOf("[");
            String ss1 = String.valueOf(t1);
            String ss2 = String.valueOf(t2);
            Log.v("sendposttest", ss1);
            Log.v("sendposttest", ss2);
            if (msg.indexOf("]") - msg.indexOf("[") == 1) {
                jsonString = "{\"platform\":\"android\",\"audience\":\"all\",\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
            } else {
                msg = msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"));
                Log.v("sendposttest", msg);
                String jsonStringPart1 = "{" + "\"platform\":\"android\","
                        + "\"audience\":{\"registration_id\":[";

                String jsonStringPart2 = jsonStringPart1 + msg;

                jsonString = jsonStringPart2 + "]},\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
                Log.v("sendposttest", jsonString);
            }
            Log.v("sendposttest", jsonString);
            sendPostRequest("https://api.jpush.cn/v3/push", jsonString);
            //sendPostRequest("https://api.jpush.cn/v3/push", "{\"platform\":\"all\",\"audience\":\"all\",\"notification\":{\"alert\":\"有人正在求救！\"}}");
        }
    });

    //向后台发送求救信息
    public void sendsos() throws JSONException{
        String url = "http://120.24.208.130:1501/event/add";
        int type = 2;

        SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
        int id = sp.getInt("user_id", -1);
        String tests1 = String.valueOf(id);
        Log.v("sendsostest", tests1);

        String ss1 = String.valueOf(longitude);
        String ss2 = String.valueOf(latitude);
        Log.v("sendsostest", ss1);
        Log.v("sendsostest", ss2);

        String send = "{\"id\":" + id + ",\"type\":" + type
                + ",\"title\":\"sos\",\"longitude\":" + longitude
                + ",\"latitude\":" + latitude +"}";
        /*String send = "{\"id\":" + id + ",\"type\":" + type
                + ",\"title\":\"sos\"}";*/

        String msg = RequestHandler.sendPostRequest(
                url, send);
        JSONObject jo = new JSONObject(msg);
        Log.v("sendsostest2", msg);
        event_id = jo.getInt("event_id");
        String s = String.valueOf(event_id);
        Log.v("sendposttest1", s);
        String tests2 = String.valueOf(event_id);
        Log.v("sendsostest", tests2);
    }
    //向后台取消求救信息
    public void cancelsos() {
        String url = "http://120.24.208.130:1501/event/modify";

        SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
        int id = sp.getInt("user_id", -1);
        String tests1 = String.valueOf(id);
        Log.v("sendsostest", tests1);

        String send = "{\"id\":" + id + ",\"event_id\":" + event_id + "}";

        String msg = RequestHandler.sendPostRequest(
                url, send);
        Log.v("sendsostest", msg);
    }
}
