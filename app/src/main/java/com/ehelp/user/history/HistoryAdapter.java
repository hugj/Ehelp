package com.ehelp.user.history;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.home.ACache;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by gj on 2015/7/27.
 */
public class HistoryAdapter extends BaseAdapter {

    private static final int REFRESH_COMPLETE = 2;
    private Context context=null;
    private List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    private Map<String,Object> item;
    private int user_id;
    private int type;
    private List<Event> events;
    private ACache eventCache; // event cache
    private Bitmap bitmap = null;
    private Bitmap defaultPortrait = null;
    private JSONArray HisList;
    Gson gson = new Gson();

    HistoryAdapter(Context context, int id, int io, int type_, ACache eventCache_){
        this.context=context;
        user_id = id;
        type = type_;
        eventCache = eventCache_;

        // 使用后台线程运行网络连接功能
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //数据初始化
        init(io);
    }

    public void init(int io){
        //数据初始化
        getDefault();
        getTitleList(io);
        setList(io);
    }
    
    //发起的
    public String setListo() {
        //数据初始化
        String jsonStrng = "{" +
                "\"id\":" + user_id +
                ",\"type\":" + type +"}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/query_launch", jsonStrng);
        return message;
    }

    //参与的
    public String setListi() {
        //数据初始化
        String jsonStrng = "{" +
                "\"id\":" + user_id + ",\"type\":2"+"}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/query_join", jsonStrng);
        return message;
    }

    //关注的
    public String setListc() {
        //数据初始化
        String jsonStrng = "{" +
                "\"id\":" + user_id + ",\"type\":1"+ "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/query_join", jsonStrng);
        return message;
    }

    /*
    * 获取默认头像
    * */
    public void getDefault() {
        if (eventCache.getAsBitmap("morentouxiang") == null) {
            String url = "http://120.24.208.130:1501/avatar/touxiang.jpg";
            defaultPortrait = returnFirstBitMap(url);
            eventCache.put("morentouxiang", defaultPortrait);
        }
    }

    /*
 * get cache as a list(for test only)
 * @param
 * @return a list contain cache
 */
    public void getTitleList(int io){
        if (io == 1) { //发起的事件
            if (eventCache.getAsJSONArray(type + "sponsorHistory") == null) {
                getFirstRemoteTitleList(io);
            } else {
                HisList = eventCache.getAsJSONArray(type + "sponsorHistory");
            }
        } else if(io == 2){ //接受的事件
            if (eventCache.getAsJSONArray(type + "acceptedHistory") == null) {
                getFirstRemoteTitleList(io);
            } else {
                HisList = eventCache.getAsJSONArray(type + "acceptedHistory");
            }
        }else {//关注的事件
            if (eventCache.getAsJSONArray(type + "concernHistory") == null) {
                getFirstRemoteTitleList(io);
            } else {
                HisList = eventCache.getAsJSONArray(type + "concernHistory");
            }
        }
    }

    /*
* get events' titles from server as a list
* only return title
* @param
* @return a list contains events' titles
*/
    public ACache getFirstRemoteTitleList(int io){
        String message;
        if (io == 1) { //发起的事件
            message = setListo();
        } else if(io == 2){ //接受的事件
            message = setListi();
        }else{//关注的事件
            message = setListc();
        }

        if (message.equals("false")) {
            eventCache = null;
        } else {
            JSONObject j1 = null;
            try {
                j1 = new JSONObject(message);
                if (j1.getInt("status") == 500) {
                    eventCache = null;
                } else {
                    JSONArray eventArray = j1.getJSONArray("event_list");

                    if (io == 1) { //发起的事件
                        eventCache.put(type + "sponsorHistory", eventArray);
                    } else if(io == 2){ //接受的事件
                        eventCache.put(type + "acceptedHistory", eventArray);
                    }else{//关注的事件
                        eventCache.put(type + "concernHistory", eventArray);
                    }

                    for (int i = 0; i < eventArray.length(); i++) {
                        int auncher_id = eventArray.getJSONObject(i).getInt("launcher_id");
                        String url = "http://120.24.208.130:1501/avatar/" +
                                auncher_id + ".jpg";
                        Bitmap image = returnFirstBitMap(url);
                        if (image == null) {
                            image = eventCache.getAsBitmap("morentouxiang");
                        }
                        if (io == 1) { //发起的事件
                            eventCache.put(type + "sponsorHistory" + "touxiang" + i, image);
                        } else if(io == 2){ //接受的事件
                            eventCache.put(type + "acceptedHistory" + "touxiang" + i, image);
                        }else{//关注的事件
                            eventCache.put(type + "concernHistory" + "touxiang" + i, image);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return eventCache;
    }


    /*
 * get events' titles from server as a list
 * only return title
 * @param
 * @return a list contains events' titles
 */
    public ACache getRemoteTitleList(int type_, int io_){
        final int type = type_;
        final int io = io_;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                if (io == 1) { //发起的事件
                    message = setListo();
                } else if(io == 2){ //接受的事件
                    message = setListi();
                }else{//关注的事件
                    message = setListc();
                }

                if (message.equals("false")) {
                    eventCache = null;
                } else {
                    JSONObject j1 = null;
                    try {
                        j1 = new JSONObject(message);
                        if (j1.getInt("status") == 500) {
                            eventCache = null;
                        } else {
                            JSONArray eventArray = j1.getJSONArray("event_list");

                            if (io == 1) { //发起的事件
                                eventCache.put(type + "sponsorHistory", eventArray);
                            } else if(io == 2){ //接受的事件
                                eventCache.put(type + "acceptedHistory", eventArray);
                            }else{//关注的事件
                                eventCache.put(type + "concernHistory", eventArray);
                            }

                            for (int i = 0; i < eventArray.length(); i++) {
                                int launcher_id = eventArray.getJSONObject(i).getInt("launcher_id");
                                String url = "http://120.24.208.130:1501/avatar/" +
                                        launcher_id + ".jpg";
                                Bitmap image = returnBitMap(url);
                                if (image == null) {
                                    image = eventCache.getAsBitmap("morentouxiang");
                                }

                                if (io == 1) { //发起的事件
                                    eventCache.put(type + "sponsorHistory" + "touxiang" + i, image);
                                } else if(io == 2){ //接受的事件
                                    eventCache.put(type + "acceptedHistory" + "touxiang" + i, image);
                                }else{//关注的事件
                                    eventCache.put(type + "concernHistory" + "touxiang" + i, image);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        return eventCache;
    }

    public void setList(int io) {
        try {
            if (HisList == null) {
                item=new HashMap<String,Object>();
                item.put("Avatar", R.drawable.icon);
                item.put("标题", "连接失败，请检查网络是否连接并重试");
                item.put("用户", "");
                item.put("时间", "");
                item.put("悬赏", "");
                list.add(item);
            } else {
                for (int i = 0; i < HisList.length(); i++) {
                    item=new HashMap<String,Object>();

                    if (io == 1) { //发起的事件
                        item.put("Avatar", eventCache.getAsBitmap(type +
                                "sponsorHistory" + "touxiang" + i));
                    } else if(io == 2){ //接受的事件
                        item.put("Avatar", eventCache.getAsBitmap(type +
                                "acceptedHistory" + "touxiang" + i));
                    }else{//关注的事件
                        item.put("Avatar", eventCache.getAsBitmap(type + "" +
                                "concernHistory" + "touxiang" + i));
                    }

                    item.put("标题", HisList.getJSONObject(i).getString("title"));
                    item.put("用户", HisList.getJSONObject(i).getString("launcher"));
                    item.put("时间", HisList.getJSONObject(i).getString("time"));
                    int state = HisList.getJSONObject(i).getInt("state");
                    if (state == 0) {
                        item.put("悬赏", "进行中");
                    } else {
                        item.put("悬赏", "已结束");
                    }
                    list.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap returnFirstBitMap(String url_){
        final String url = url_;
        URL myFileUrl = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap returnBitMap(String url_){
        final String url = url_;
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL myFileUrl = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                            .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return bitmap;
    }

    public JSONArray getEventList() {
        return HisList;
    }

    public int getCount() {return list.size();}
    public Object getItem(int position) {return position;}
    public long getItemId(int position) {return position;}

    public View getView(int position, View convertView, ViewGroup parent) {
        //Avatar
        ImageView iv=new ImageView(context);
        RelativeLayout.LayoutParams lp_iv=new RelativeLayout.LayoutParams(80,80);
        lp_iv.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(lp_iv);
        iv.setScaleType(ScaleType.CENTER_INSIDE);
        Bitmap image = ((Bitmap) (list.get(position)).get("Avatar"));
        if (image != null) {
            iv.setImageBitmap(image);
        }
        //标题
        TextView title=new TextView(context);
        RelativeLayout.LayoutParams lp_tv=new RelativeLayout.LayoutParams(-2,-2);
        lp_tv.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        title.setLayoutParams(lp_tv);
        title.setTextSize(title.getTextSize());
        title.setText((String) (list.get(position).get("标题")));
        //用户
        TextView user=new TextView(context);
        RelativeLayout.LayoutParams lp_sex=new RelativeLayout.LayoutParams(-2,-2);
        lp_sex.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        user.setLayoutParams(lp_sex);
        user.setText((String)(list.get(position).get("用户")));
        //时间
        TextView time=new TextView(context);
        RelativeLayout.LayoutParams lp_time=new RelativeLayout.LayoutParams(-2,-2);
        lp_sex.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        time.setLayoutParams(lp_sex);
        time.setText((String)(list.get(position).get("时间")));
        //悬赏
        TextView  Reward=new TextView(context);
        RelativeLayout.LayoutParams lp_age=new RelativeLayout.LayoutParams(-2,-2);
        lp_age.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        Reward.setLayoutParams(lp_age);
        Reward.setText((String) (list.get(position).get("悬赏")));

        //线性布局1
        LinearLayout l11=new LinearLayout(context);
        l11.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        l11.setOrientation(LinearLayout.VERTICAL);
        l11.addView(user);
        l11.addView(time);
        //线性布局2
        LinearLayout l12=new LinearLayout(context);
        l12.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        l12.setOrientation(LinearLayout.HORIZONTAL);
        l12.addView(iv);
        l12.addView(l11);
        //相对布局1
        RelativeLayout rl1=new RelativeLayout(context);
        rl1.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
        rl1.addView(l12);
        rl1.addView(Reward);
        //返回视图

        final int margin = 16;
        ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);


        //params.setMargins(margin, margin, margin, margin);
        LinearLayout returnView=new LinearLayout(context);
        returnView.setLayoutParams(new ListView.LayoutParams(-1,-2));//注意:ListView.LayoutParams
        returnView.setOrientation(LinearLayout.VERTICAL);
        returnView.addView(rl1);
        returnView.addView(title);

        return returnView;
    }

}