package com.ehelp.map;

import android.app.AlertDialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.entity.comment;
import com.ehelp.home.Home;
import com.ehelp.utils.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

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


public class sendsos_map extends ActionBarActivity implements BaiduMap.OnMapClickListener,View.OnClickListener {


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
    private boolean flag =false;

    String send;


    private List<comment> comments;
    private AlertDialog ResponseDialog = null;


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
        setListView();

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
        new Thread(runnable).start();

        //推送求救信息
        //thread.start();
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
        flag = true;
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
//    Thread thread =new Thread(new Runnable() {
//        @Override
//        public void run() {
//        }
//    });

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

            if (msg.equals("false")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                try {
                    JSONObject jo = new JSONObject(msg);
                    if (jo.getInt("status") == 500) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        JSONObject value = jo.getJSONObject("value");
                        event_id = value.getInt("event_id");
                        SharedPreferences spf = getApplicationContext().getSharedPreferences("user_id", Context.MODE_PRIVATE);
                        String s1 = String.valueOf(id);
                        Log.v("sendposttest", s1);

                        String locmsg = "{\"id\":"+ s1 + "}";
                        String msg_ = RequestHandler.sendPostRequest(
                                "http://120.24.208.130:1501/user/neighbor", locmsg);
                        Log.v("sendposttest", msg_);

                        String jsonString;

                        if (msg_.equals("false")) {
                            return;
                        } else {
                            try {
                                JSONObject JO = new JSONObject(msg_);
                                if (JO.getInt("status") == 500) {
                                    return;
                                } else {
                                    if (msg_.indexOf("]") - msg_.indexOf("[") == 1) {
                                        jsonString = "{\"platform\":\"android\",\"audience\":\"all\",\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
                                    } else {
                                        msg_ = msg_.substring(msg_.indexOf("[") + 1, msg_.indexOf("]"));
                                        int temp = msg.indexOf(",");
                                        if (temp == -1) {
                                            jsonString = "{\"platform\":\"android\",\"audience\":\"all\",\"notification\":{\"alert\":\"有人正在求救！事件号：" + event_id + "\"}}";
                                        } else {
                                            Log.v("sendposttest", msg_);
                                            String jsonStringPart1 = "{" + "\"platform\":\"android\","
                                                    + "\"audience\":{\"registration_id\":[";

                                            String jsonStringPart2 = jsonStringPart1 + msg_;

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

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
                if(flag == true){
                    break;
                }
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

    public void setListView(){
        //ListView comList = (ListView)findViewById(R.id.comment);
        CommentAdapter com = new CommentAdapter(this, event_id);
        //comList.setAdapter(com);
        comments = com.getEvent();

        LinearLayout commentll = (LinearLayout)findViewById(R.id.SOScomment);
        for (int i = 0; i < comments.size(); i++) {
            //时间
            TextView time=new TextView(this);
            time.setText(comments.get(i).getTime());
            RelativeLayout.LayoutParams tim=new RelativeLayout.LayoutParams(-2,-2);
            tim.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            time.setLayoutParams(tim);

            //内容
            TextView content=new TextView(this);
            content.setText(comments.get(i).getContent());

            //作者
            TextView Author=new TextView(this);
            String Response = comments.get(i).getParent_author();
            if (Response == null) {
                Author.setText(comments.get(i).getAuthor());
            } else {
                Author.setText(comments.get(i).getAuthor() + " 回复：" + Response);
            }
            RelativeLayout.LayoutParams aut=new RelativeLayout.LayoutParams(-2,-2);
            aut.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            Author.setLayoutParams(aut);

            //作者和时间
            RelativeLayout rl=new RelativeLayout(this);
            rl.addView(Author);
            rl.addView(time);

            //单条评论
            LinearLayout l1=new LinearLayout(this);
            l1.setOrientation(LinearLayout.VERTICAL);
            l1.addView(rl);
            l1.addView(content);

            android.support.v7.widget.CardView card = new android.support.v7.widget.CardView(this);
            card.addView(l1);
            card.setOnClickListener(this);
            card.setId(i);

            commentll.addView(card);
        }
    }
    public Boolean isAuthor(int id) {
        SharedPreferences sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        int phone_user_id = sharedPref.getInt("user_id", -1);
        return (id == phone_user_id);
    }

    /*
    * 监听点击评论回复
    * */
    @Override
    public void onClick(View v) {
        final int i =  v.getId();

        ResponseDialog = new AlertDialog.Builder(sendsos_map.this).create();
        ResponseDialog.show();
        ResponseDialog.getWindow().setContentView(R.layout.response_comment);

        //设置弹出框内容
        LinearLayout pop = (LinearLayout)ResponseDialog.getWindow().findViewById(R.id.pop);
        LinearLayout resp = new LinearLayout(this);
        TextView respText=new TextView(this);
        respText.setText("回复");
        respText.setTextSize(20);
        resp.addView(respText);
        pop.addView(resp);
        //点击回复
        resp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sendsos_map.this, ResponseActivity.class);
                intent.putExtra("comment", comments.get(i));
                startActivity(intent);
                ResponseDialog.dismiss();
            }
        });


        if (isAuthor(comments.get(i).getAuthor_id())) {
            LinearLayout dele = new LinearLayout(this);
            TextView deleText=new TextView(this);
            deleText.setText("删除");
            deleText.setTextSize(20);
            dele.addView(deleText);
            pop.addView(dele);

            //点击删除
            dele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jsonStrng = "{" +
                            "\"id\":" + comments.get(i).getAuthor_id() +
                            ",\"event_id\":" + comments.get(i).getEvent_id() +
                            ",\"comment_id\":" + comments.get(i).getComment_id() + "}";

                    String message = RequestHandler.sendPostRequest(
                            "http://120.24.208.130:1501/comment/remove", jsonStrng);
                    if (message == "false") {
                        Toast.makeText(getApplicationContext(), "删除失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jO = new JSONObject(message);
                            if (jO.getInt("status") == 500) {
                                Toast.makeText(getApplicationContext(), "删除失败",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "删除成功",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(sendsos_map.this, Home.class);
                                startActivity(intent);
                                ResponseDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

    }
}
