package com.ehelp.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import cn.jpush.android.api.JPushInterface;

//手机振动与手机发声
//import android.media.SoundPool;
//import android.media.AudioManager;
//统计代码
//极光推送
//严苛模式

//手机振动与手机发声
//import android.media.SoundPool;
//import android.media.AudioManager;
//统计代码
//极光推送
//严苛模式


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
    //private SoundPool sp;
    private Vibrator vib;

    private Thread thr;

    Message msg_ =new Message();
    private int count = 0;
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
        new Thread(runnable_).start();

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
                longitude = location.getLongitude();
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

            String locmsg = "{\"id\":"+ s1 + "}";
            String msg = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/neighbor", locmsg);
            Log.v("sendposttest", msg);

            String jsonString;

            if (msg.equals("false")) {
                return;
            } else {
                try {
                    JSONObject JO = new JSONObject(msg);
                if (JO.getInt("status") == 500) {
                    return;
                } else {
                    if (msg.indexOf("]") - msg.indexOf("[") == 1) {
                        jsonString = "{\"platform\":\"android\",\"audience\":\"all\",\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
                    } else {
                        msg = msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"));
                        int temp = msg.indexOf(",");
                        if (temp == -1) {
                            jsonString = "{\"platform\":\"android\",\"audience\":\"all\",\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
                        } else {
                            Log.v("sendposttest", msg);
                            String jsonStringPart1 = "{" + "\"platform\":\"android\","
                                    + "\"audience\":{\"registration_id\":[";

                            String jsonStringPart2 = jsonStringPart1 + msg;

                            jsonString = jsonStringPart2 + "]},\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
                            Log.v("sendposttest", jsonString);
                        }
                    }
                    Log.v("sendposttest", jsonString);
                    sendPostRequest("https://api.jpush.cn/v3/push", jsonString);
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try{
                Thread.sleep(2000);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            String url = "http://120.24.208.130:1501/event/add";
            int type = 2;

            SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
            int id = sp.getInt("user_id", -1);
//            String tests1 = String.valueOf(id);
//            Log.v("sendsostest", tests1);

            String send = "{\"id\":" + id + ",\"type\":" + type
                    + ",\"title\":\"sos\",\"longitude\":" + longitude
                    + ",\"latitude\":" + latitude +"}";

            String msg = RequestHandler.sendPostRequest(
                    url, send);
//            JSONObject jo = new JSONObject(msg);
//            Log.v("sendsostest", msg);
//            event_id =  jo.getInt("event_id");
//            String tests2 = String.valueOf(event_id);
//            Log.v("sendsostest", tests2);
        }
    };

    //向后台发送求救信息
    public void sendsos() throws JSONException{
        String url = "http://120.24.208.130:1501/event/add";
        int type = 2;

        SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
        int id = sp.getInt("user_id", -1);
        String tests1 = String.valueOf(id);
        Log.v("sendsostest", tests1);

        String send = "{\"id\":" + id + ",\"type\":" + type
                + ",\"title\":\"sos\"}";

        String msg = RequestHandler.sendPostRequest(
                url, send);

        if (msg.equals("false")) {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jo = new JSONObject(msg);
            Log.v("sendsostest2", msg);
            if (jo.getInt("status") == 500) {
                Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                        Toast.LENGTH_SHORT).show();
            } else {
                JSONObject value = jo.getJSONObject("value");
                event_id = value.getInt("event_id");
            }
        }
    }
    //向后台取消求救信息
    public void cancelsos() {
        String url = "http://120.24.208.130:1501/event/modify";

        SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
        int id = sp.getInt("user_id", -1);

        String send = "{\"id\":" + id + ",\"event_id\":" + event_id + ",\"state\":" + 1 + "}";

        String msg = RequestHandler.sendPostRequest(
                url, send);
        Log.v("sendsostest", msg);
    }

    public void chooseHealth(View v){
        String url = "http://120.24.208.130:1501/event/modify";
        SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
        int id = sp.getInt("user_id", -1);
        String send = "{\"id\":" + id + ",\"event_id\":" + event_id + ",\"demand_number\":"+1+"}";
        String msg ="false";
        msg = RequestHandler.sendPostRequest(
                url, send);
        if(msg == "false"){
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
            JSONObject jO = new JSONObject(msg);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "设置失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else if(jO.getInt("status") == 200){
                Toast.makeText(getApplicationContext(), "设置成功",
                        Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void chooseSafe(View v){
        String url = "http://120.24.208.130:1501/event/modify";
        SharedPreferences sp = getSharedPreferences("user_id", MODE_PRIVATE);
        int id = sp.getInt("user_id", -1);
        String send = "{\"id\":" + id + ",\"event_id\":" + event_id + ",\"demand_number\":"+2+"}";
        String msg = RequestHandler.sendPostRequest(
                url, send);
        if(msg == "false"){
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
            JSONObject jO = new JSONObject(msg);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "设置失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else if(jO.getInt("status") == 200){
                Toast.makeText(getApplicationContext(), "设置成功",
                        Toast.LENGTH_SHORT).show();
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Runnable runnable_ = new Runnable() {
        @Override
        public void run() {
            int i =0;
            while(i<1000) {
                Log.v("123456", String.valueOf(count));
                getRespondNumber();
                i++;
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    };
    private void getRespondNumber(){
        String url = "http://120.24.208.130:1501/event/get_information";
        String send = "{" +"\"event_id\":" + event_id +"}";
        String msg ="false";
        msg = RequestHandler.sendPostRequest(
                url, send);
        if(msg == "false"){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }else {
            try {
                JSONObject jO = new JSONObject(msg);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "查询关注人数失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                } else if (jO.getInt("status") == 200) {
                    msg_.arg1 = jO.getInt("support_number");
                    count++;
//                    msg_.arg1= count;
                    Log.v("count","count"+String.valueOf(count));
                    mHandler.sendMessage(msg_);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    public Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.arg1 != -1) {
                Button btn = (Button) findViewById(R.id.respond_number);
                int i = msg.arg1;
                btn.setText(String.valueOf(i));
            }else {
                super.handleMessage(msg);
            }
        }
    };

    public void respondNum(View view){
        Intent it = new Intent(sendsos_map.this,RespondPeopleActivity.class);
        it.putExtra("event_id", event_id);
        startActivity(it);
    }
}
