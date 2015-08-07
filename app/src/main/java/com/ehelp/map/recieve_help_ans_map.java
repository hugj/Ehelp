package com.ehelp.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
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
import com.ehelp.home.Home;
import com.ehelp.home.SuperAwesomeCardFragment;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.utils.RequestHandler;
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
    private Event m_event;
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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_recieve_help_ans_map);
        //CharSequence titleLable = "路线规划";
        //setTitle(titleLable);
        // 初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        int count = mMapView.getChildCount();

        Intent intent = getIntent();
        m_event = (Event)intent.getSerializableExtra(SuperAwesomeCardFragment.EXTRA_MESSAGE);
        event_id = m_event.getEventId();
        setView();

        init();

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("求助信息详情");

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


    }

    public void setView(){
        //事件信息
        TextView tmp = (TextView)findViewById(R.id.user_name);
        tmp.setText(getNickname());
        tmp = (TextView)findViewById(R.id.Title);
        tmp.setText(m_event.getTitle());
        tmp = (TextView)findViewById(R.id.Content);
        tmp.setText(m_event.getContent());

        //评论
        setListView();
    }

    public String getNickname(){
        final int user_id = m_event.getLauncherId();
        String nickname = "";
        String jsonStrng = "{" +
                "\"id\":" + user_id + "}";
        final String message = RequestHandler.sendPostRequest(
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
            }
        }
        return nickname;
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
        LatLng pt2 = new LatLng(23.03777, 113.496627); //LatLng代表地图上经纬度提供的位置
        PlanNode enNode = PlanNode.withLocation(pt2);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions o2 = new MarkerOptions().icon(bd).position(pt2);
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
    /*public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItem = menu.findItem(R.id.action_ans);
        if (isCreateConnectionSuccess) {
            //menuItem.setIcon(R.drawable.apps_bright);
            menuItem.setTitle("改变过");
        }
        return true;
    }*/
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if ((id == R.id.action_ans)&&item.getTitle().toString().equals("回应")){
            item.setTitle("取消回应");
            return true;
        }
        if ((id == R.id.action_ans)&&(item.getTitle() =="取消回应")) {

            //MenuItem it =menu.findItem(R.id.action_ans);
            item.setTitle("回应");
            //it.setIcon(.....);无法改变图标就访问http://www.dewen.io/q/5332/寻找答案。
            //Intent intent = new Intent(this, ContactlistActivity.class);
            //startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recieve_help_ans_map, menu);
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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // 暂时提供三个点标注在地图上作为例子
        LatLng pt4 = new LatLng(23.063309, 113.394004);
        LatLng pt2 = new LatLng(23.062578, 113.410821);
        LatLng pt3 = new LatLng(23.045286, 113.405934);

        BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
        OverlayOptions o1 = new MarkerOptions().icon(bd).position(pt4);
        OverlayOptions o2 = new MarkerOptions().icon(bd).position(pt2);
        OverlayOptions o3 = new MarkerOptions().icon(bd).position(pt3);

        mBaidumap.addOverlay(o1);
        mBaidumap.addOverlay(o2);
        mBaidumap.addOverlay(o3);

        builder.include(pt4);
        builder.include(pt2);
        builder.include(pt3);

        LatLng pt5 = new LatLng(23.03777, 113.496627);
        OverlayOptions o5 = new MarkerOptions().icon(bd).position(pt5);
        mBaidumap.addOverlay(o5);

        mMarker1 = (Marker) (mBaidumap.addOverlay(o1));
        mMarker2 = (Marker) (mBaidumap.addOverlay(o2));
        mMarker3 = (Marker) (mBaidumap.addOverlay(o3));
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
                // 这里是未完成的页面跳转
                // getMenuInflater().inflate(R.menu.menu_send_help, menu);
            }
        }
    }
}
