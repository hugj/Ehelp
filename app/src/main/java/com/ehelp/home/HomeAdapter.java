package com.ehelp.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;
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
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
public class HomeAdapter extends BaseAdapter {

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
    private JSONArray eventList;
    Gson gson = new Gson();

    HomeAdapter(Context context, int id, int type_, ACache eventCache_){
        this.context=context;
        user_id = id;
        type = type_;
        eventCache = eventCache_;

        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        init();
    }

    HomeAdapter(Context context, int id, int type_){
        this.context=context;
        user_id = id;
        type = type_;
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());
        setList2();
    }

    public void init(){
        //数据初始化
        getDefault();
        getTitleList();
        setList();
    }

    /*
    * 获取默认头像
    * */
    public void getDefault() {
        if (eventCache.getAsBitmap(type + "morentouxiang") == null) {
            String url = "http://120.24.208.130:1501/avatar/touxiang.jpg";
            defaultPortrait = returnFirstBitMap(url);
            eventCache.put(type + "morentouxiang", defaultPortrait);
        }
    }

    /*
 * get cache as a list(for test only)
 * @param
 * @return a list contain cache
 */
    public void getTitleList(){
        if (eventCache.getAsJSONArray(type + "") == null) {
            getFirstRemoteTitleList(type);
        } else {
            eventList = eventCache.getAsJSONArray(type + "");
        }
    }

    /*
* get events' titles from server as a list
* only return title
* @param
* @return a list contains events' titles
*/
    public ACache getFirstRemoteTitleList(int type_){
        final int type = type_;
        String message;
        String postURL = "http://120.24.208.130:1501/event/get_nearby_event";
        if (type == 0) {
            postURL = "http://120.24.208.130:1501/event/get_events";
        }
        String postString = "{" +
                "\"id\":" + user_id + ",\"state\":0," +
                "\"type\":" + type + "}";

        message = RequestHandler.sendPostRequest(postURL, postString);

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
                    eventCache.put(type + "", eventArray);

                    for (int i = 0; i < eventArray.length(); i++) {
                        int auncher_id = eventArray.getJSONObject(i).getInt("launcher_id");
                        String url = "http://120.24.208.130:1501/avatar/" +
                                auncher_id + ".jpg";
                        Bitmap image = returnFirstBitMap(url);
                        if (image == null) {
                            image = eventCache.getAsBitmap(type + "morentouxiang");
                        }
                        eventCache.put(type + "touxiang" + i, image);
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
    public ACache getRemoteTitleList(int type_){
        final int type = type_;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                String postURL = "http://120.24.208.130:1501/event/get_nearby_event";
                if (type == 0) {
                    postURL = "http://120.24.208.130:1501/event/get_events";
                }
                String postString = "{" +
                        "\"id\":" + user_id + ",\"state\":0," +
                        "\"type\":" + type + "}";

                message = RequestHandler.sendPostRequest(postURL, postString);

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
                            eventCache.put(type + "", eventArray);

                            for (int i = 0; i < eventArray.length(); i++) {
                                int auncher_id = eventArray.getJSONObject(i).getInt("launcher_id");
                                String url = "http://120.24.208.130:1501/avatar/" +
                                        auncher_id + ".jpg";
                                Bitmap image = returnBitMap(url);
                                if (image == null) {
                                    image = eventCache.getAsBitmap(type + "morentouxiang");
                                }
                                eventCache.put(type + "touxiang" + i, image);
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

    public void setList() {
        try {
            if (eventList == null) {
                item=new HashMap<String,Object>();
                item.put("Avatar", R.drawable.icon);
                item.put("标题", "连接失败，请检查网络是否连接并重试");
                item.put("用户", "");
                item.put("时间", "");
                item.put("悬赏", "");
                list.add(item);
            } else {
                for (int i = 0; i < eventList.length(); i++) {
                    item=new HashMap<String,Object>();
                    item.put("Avatar", eventCache.getAsBitmap(type + "touxiang" + i));
                    item.put("标题", eventList.getJSONObject(i).getString("title"));
                    item.put("用户", eventList.getJSONObject(i).getString("launcher"));
                    item.put("时间", eventList.getJSONObject(i).getString("time"));
                    item.put("悬赏", eventList.getJSONObject(i).getString("love_coin") + "爱心币");
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

    public void setList2() {
        //数据初始化
        try {
            String jsonStrng = "{" +
                    "\"id\":" + user_id + ",\"state\":0," +
                    "\"type\":" + type + "}";

            String message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/event/get_nearby_event", jsonStrng);
            Log.v("asdf3qweqw", message);
            if (message == "false") {
                item=new HashMap<String,Object>();
                item.put("Avatar", R.drawable.icon);
                item.put("标题", "连接失败，请检查网络是否连接并重试");
                item.put("用户", "");
                item.put("时间", "");
                item.put("悬赏", "");
                list.add(item);
            } else {
                JSONObject j1 = new JSONObject(message);
                String st = j1.getString("event_list");
                events = gson.fromJson(st, new TypeToken<List<Event>>(){}.getType());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getEventList() {
        return eventList;
    }

    public List<Event> getEvent() {
        return events;
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
        //iv.setImageBitmap(eventCache.getAsBitmap(type + "touxiang" + 1));
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