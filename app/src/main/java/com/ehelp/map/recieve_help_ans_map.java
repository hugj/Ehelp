package com.ehelp.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ehelp.entity.comment;
import com.ehelp.evaluate.Evaluation;
import com.ehelp.home.Home;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.sound.RecordingActivity;
import com.ehelp.utils.RequestHandler;
import com.ehelp.video.RecordActivity;
import com.google.gson.Gson;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_recieve_help_ans_map)
public class recieve_help_ans_map extends AIActionBarActivity implements BaiduMap.OnMapClickListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener,
        OnGetRoutePlanResultListener, View.OnClickListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    ImageView Image;
    //浏览路线节点相关
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    PlanNode stNode = null;
    private EditText Eans = null;
    private String ans  =null;
    private double jingdu;
    private double weidu;
    //private Event m_event;
    private int event_id;
    private AlertDialog ResponseDialog = null;
    private List<comment> comments;

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

    //Toolbar
    private Toolbar mToolbar;
    private Menu menu_;
    private int idd;//当前登录ID
    private int user_id;//发起者ID
    private SharedPreferences sp;

    LatLng end_node = null;
    private Event m_event;
//每隔15s刷新回应人数
    Message msg_ =new Message();
    private boolean flag =false;

    private int respond_or_not=0;//0无关1关注2回应

    private Gson gson = new Gson();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

//        new Thread(runnable_m).start();//初始化关注（取消关注按钮）
        //setContentView(R.layout.activity_recieve_help_ans_map);
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //CharSequence titleLable = "路线规划";
        //setTitle(titleLable);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        int count = mMapView.getChildCount();

        Intent intent = getIntent();
        event_id = getIntent().getIntExtra("event_id",-1);
        //event_id =311;

        //event_id = m_event.getEventId();
        sp = this.getSharedPreferences("user_id", MODE_PRIVATE);
        idd = sp.getInt("user_id", -1);
        getEvent();
        setView();

        init();
        new Thread(runnable_).start();//实时显示关注的人数

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        //final int user_id = m_event.getLauncherId();
        TextView tvv = (TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("求助信息详情");

        if(idd == user_id) {
            tvv.setText("我的求助");
        }

        // fab();

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
    }

    public void getEvent(){
        //int id =-1;
        String jsonStrng = "{" +
                "\"event_id\":" + event_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/get_information", jsonStrng);
        if (message == "false") {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
            user_id= -1;
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
                user_id= -1;
            }else {
                Toast.makeText(getApplicationContext(), "查询成功",
                        Toast.LENGTH_SHORT).show();
                Gson gson = new Gson();
                m_event = gson.fromJson(message, Event.class);
                user_id = jO.getInt("launcher_id");
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.v("12345678", e.toString());
        }


        //return id;
    }

    public void setView(){
        //获取nickname
        //final int user_id = m_event.getLauncherId();
        String nickname = "";
        String title ="";
        String content ="";
        String time = "time";
        String jsonStrng = "{" +
                "\"id\":" + user_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/get_information", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }   else {
            try {
                JSONObject jO = new JSONObject(message);
                nickname = jO.getString("nickname");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("qazwsxed", e.toString());
            }
        }
        jsonStrng = "{" +
                "\"event_id\":" + event_id + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/get_information", jsonStrng);
        TextView tmp = (TextView)findViewById(R.id.user_name);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }   else {
            try {
                JSONObject jO = new JSONObject(message);
                title = jO.getString("title");
                content = jO.getString("content");
                time = jO.getString("time");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("qazwsxed", e.toString());
            }
        }
        tmp.setText(nickname);
        tmp = (TextView)findViewById(R.id.Title);
        tmp.setText(title);
        tmp = (TextView)findViewById(R.id.Content);
        tmp.setText(content);
        tmp = (TextView)findViewById(R.id.helptime);
        tmp.setText(time);

        setListView();
    }

    public Boolean isAuthor(int id) {
        SharedPreferences sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        int phone_user_id = sharedPref.getInt("user_id", -1);
        return (id == phone_user_id);
    }

    /*
    * 加载评论列表
    * */
    public void setListView(){
        //ListView comList = (ListView)findViewById(R.id.comment);
        CommentAdapter com = new CommentAdapter(this, event_id);
        //comList.setAdapter(com);
        comments = com.getEvent();

        LinearLayout commentll = (LinearLayout)findViewById(R.id.comment);
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

    /*
    * 监听点击评论回复
    * */
    @Override
    public void onClick(View v) {
        android.support.v7.widget.CardView card = (android.support.v7.widget.CardView)v;
        final int i =  v.getId();

        ResponseDialog = new AlertDialog.Builder(recieve_help_ans_map.this).create();
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
                Intent intent = new Intent(recieve_help_ans_map.this, ResponseActivity.class);
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
                                Intent intent = new Intent(recieve_help_ans_map.this, Home.class);
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
        setView();
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
            Toast.makeText(recieve_help_ans_map.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(recieve_help_ans_map.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
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


    //toolbar设置
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //final int user_id = m_event.getLauncherId();
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id ==R.id.action_comment){

        }

        if (id == R.id.action_video) {
            Intent mIntent = new Intent();
            mIntent.putExtra("event_id", event_id);
            mIntent.setClass(recieve_help_ans_map.this, VideoActivity.class);
            startActivity(mIntent);
        }

        if(id == R.id.action_cancelhelp){
            cancelhelp();
            return true;
        }
        if(id == R.id.action_endhelp){
            endhelp();
            return true;
        }
        if(id == R.id.action_concern||id == R.id.action_respond) {
            int operation =0;
            if (item.getTitle().toString().equals("回应")) {
                operation = 2;
            }
            if (item.getTitle().toString().equals("关注")) {
                operation = 1;
            }
            action(operation,item);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //final int user_id = m_event.getLauncherId();
        getMenuInflater().inflate(R.menu.menu_recieve_help_ans_map, menu);
        init_View();
        MenuItem item_concern = menu.findItem(R.id.action_concern);
        MenuItem item_respond = menu.findItem(R.id.action_respond);
        MenuItem item_comment = menu.findItem(R.id.action_comment);
        MenuItem item_cancelhelp = menu.findItem(R.id.action_cancelhelp);
        MenuItem item_endhelp = menu.findItem(R.id.action_endhelp);
        Button bt = (Button)findViewById(R.id.video);
        Button dbt = (Button)findViewById(R.id.sound);
        if(idd == user_id){
            item_concern.setVisible(false);
            item_respond.setVisible(false);
            item_cancelhelp.setVisible(true);
            item_endhelp.setVisible(true);
            bt.setVisibility(View.VISIBLE);
            dbt.setVisibility(View.VISIBLE);
        }else {
            item_concern.setVisible(true);
            item_respond.setVisible(true);
            item_cancelhelp.setVisible(false);
            item_endhelp.setVisible(false);
            bt.setVisibility(View.INVISIBLE);
            dbt.setVisibility(View.INVISIBLE);
            if(respond_or_not == 2){
                item_respond.setTitle("取消回应");
                item_concern.setVisible(false);
            }else if(respond_or_not ==1){
                item_respond.setTitle("回应");
                item_concern.setTitle("取消关注");
            }else {
                item_respond.setTitle("回应");
                item_concern.setTitle("关注");
            }
        }
        menu_ = menu;
        return true;
    }
    //toolbar设置结束

//    @Override
//    protected void onPause() {
//        mMapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        mMapView.onResume();
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        mSearch.destroy();
//        mMapView.onDestroy();
//        super.onDestroy();
//    }
@Override
protected void onDestroy() {
//        mSearch.destroy();
//        mMapView.onDestroy();
    flag = true;
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
            }
            LatLng pt1 = new LatLng(location.getLatitude(),
                    location.getLongitude());
            stNode = PlanNode.withLocation(pt1);
        }

        public void onReceivePoi(BDLocation poiLocation) {}
    }

    public void init() {
        init2();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // 暂时提供三个点标注在地图上作为例子
        end_node = new LatLng(m_event.getLatitude(), m_event.getLongitude());

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
        //http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/1123/2050.html参考网址

        //FAB
        fab();
    }

    private void fab(){
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
    /*
    for fab
     */
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else
        if (position == 1) {
            showToastMessage("您正在求助界面");
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }
    @Override
    /*
for fab
 */
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        if (position == 0) {
            Intent intent = new Intent(this, CountNum.class);
            startActivity(intent);
        } else
        if (position == 1) {
            showToastMessage("您正在求助界面");
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    public void submit() {
        Eans = (EditText)findViewById(R.id.edit_message2);
        ans = Eans.getText().toString();
        if (!ans.isEmpty()) {
            String jsonString = "{" + "\"content\":" + ans  + "," + "\"longitude\":" + jingdu + "," +
                    "\"longitude\":" + weidu + "\"type\":1," + "}";
            String message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/event/add", jsonString);
            if (message == "false") {
                Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                        Toast.LENGTH_SHORT).show();
                return;
            }   else {
                Toast.makeText(getApplicationContext(), message,  //测试
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void init2() {
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
                m_event = gson.fromJson(message, Event.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void sound(View v) {
        Intent intent = new Intent(this, RecordingActivity.class);
        intent.putExtra("EVENT_ID", m_event.getEventId());
        startActivity(intent);
    }

    public void video(View v) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("EVENT_ID", m_event.getEventId());
        startActivity(intent);
    }

    Runnable runnable_ = new Runnable() {
        @Override
        public void run() {
            int i =0;
            while(i<2000) {
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
        Intent it = new Intent(recieve_help_ans_map.this,RespondPeopleActivity.class);
        it.putExtra("event_id", event_id);
        startActivity(it);
    }

//    Runnable runnable_m = new Runnable() {
//        @Override
//        public void run() {
//                init_View();
//        }
//    };
    private void init_View(){
        String url = "http://120.24.208.130:1501/user/judge_sup";
        String send = "{" +"\"id\":" + idd +"," +"\"event_id\":" + event_id +"}";
        String msg ="false";
        msg = RequestHandler.sendPostRequest(
                url, send);
        if(msg == "false"){
            respond_or_not = 0;
            return;
        }else {
            try {
                JSONObject jO = new JSONObject(msg);
                if (jO.getInt("status") == 500) {
                    respond_or_not = 0;
                    return;
                } else if (jO.getInt("status") == 200) {
                    respond_or_not = jO.getInt("type");
                    Log.v("respondornot",String.valueOf(respond_or_not));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (menu_ != null) {
                                MenuItem item_concern = menu_.findItem(R.id.action_concern);
                                MenuItem item_respond = menu_.findItem(R.id.action_respond);
                                if (respond_or_not == 2) {
                                    item_respond.setTitle("取消回应");
                                    item_concern.setVisible(false);
                                } else if (respond_or_not == 1) {
                                    item_respond.setTitle("回应");
                                    item_concern.setTitle("取消关注");
                                } else {
                                    item_respond.setTitle("回应");
                                    item_concern.setTitle("关注");
                                }
                            }
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void action(int operation,MenuItem item){
        String send = "{\"id\":" + idd + ",\"event_id\":"
                + event_id + ",\"operation\":" + operation + "}";
        String msg = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/event_manage", send);
        Log.v("receiversostest", msg);
        if (msg == "false") {
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
                            Toast.makeText(getApplicationContext(), "操作失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                } else if (jO.getInt("status") == 200) {
                    Toast.makeText(getApplicationContext(), "操作成功",
                            Toast.LENGTH_SHORT).show();
                    if (item.getTitle().toString().equals("回应")) {
                        MenuItem item_concern = menu_.findItem(R.id.action_concern);
                        item.setTitle("取消回应");
                        item_concern.setTitle("关注");
                        item_concern.setVisible(false);
                    } else if (item.getTitle().toString().equals("关注")) {
                        item.setTitle("取消关注");
                    } else if (item.getTitle().toString().equals("取消关注")) {
                        item.setTitle("关注");
                    } else if (item.getTitle().toString().equals("取消回应")) {
                        MenuItem item_concern = menu_.findItem(R.id.action_concern);
                        item.setTitle("回应");
                        item_concern.setVisible(true);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void endhelp(){
        String jsonStrng = "{" +
                "\"state\":1" + "," +
                "\"id\":" + user_id +","+
                "\"event_id\":" +event_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/modify", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
            return;
        }try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "操作失败,您不是求助者",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else if(jO.getInt("status") == 200){
                Toast.makeText(getApplicationContext(), "操作成功",
                        Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,Evaluation.class);
                intent.putExtra("event_id", event_id);
                startActivity(intent);
                this.finish();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void cancelhelp(){
        String jsonStrng = "{" +
                "\"id\":" + user_id + ",\"event_id\":" +event_id+ "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/remove", jsonStrng);

        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "操作失败,您不是求助者",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else if(jO.getInt("status") == 200){
                Toast.makeText(getApplicationContext(), "操作成功",
                        Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(this,Home.class);
                startActivity(intent);
                this.finish();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
