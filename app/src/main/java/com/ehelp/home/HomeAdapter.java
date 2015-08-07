package com.ehelp.home;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

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
    private String tit;
    private double longitude;
    private double latitude;
    private int user_id;
    private int type;
    private List<Event> events;
    private ACache eventCache; // event cache
    Gson gson = new Gson();

    HomeAdapter(Context context, int id, int type_, ACache eventCache_){
        this.context=context;
        user_id = id;
        type = type_;
        eventCache = eventCache_;
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
        String message = getTitleList();
        setList(message);
    }

    /*
 * get cache as a list(for test only)
 * @param
 * @return a list contain cache
 */
    public String getTitleList(){
        //String cache = eventCache.getAsString(type + "");
        String cache = "";
        if (eventCache.getAsString(Integer.toString(type)) == null) {
            eventCache.put(Integer.toString(type), cache);
        } else {
            cache = eventCache.getAsString(type + "");
        }
        if ((cache.isEmpty()) || (cache.equals("false"))) {
            cache = getRemoteTitleList(type).getAsString(type + "");
        }
        return cache;
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
                String response;
                String postURL = "http://120.24.208.130:1501/event/get_nearby_event";
                String postString = "{" +
                        "\"id\":" + user_id + ",\"state\":0," +
                        "\"type\":" + type + "}";

                response = RequestHandler.sendPostRequest(postURL, postString);
                eventCache.put(type + "", response);
            }
        }).start();

        return eventCache;
    }

    public void setList(String message) {
        try {
            if (message.equals("false")) {
                item=new HashMap<String,Object>();
                item.put("头像", R.drawable.icon);
                item.put("标题", "连接失败，请检查网络是否连接并重试");
                item.put("用户", "");
                item.put("时间", "");
                item.put("悬赏", "");
                list.add(item);
            } else {
                JSONObject j1 = new JSONObject(message);
                String st = j1.getString("event_list");
                events = gson.fromJson(st, new TypeToken<List<Event>>(){}.getType());
                for (int i = 0; i < events.size(); i++) {
                    item=new HashMap<String,Object>();
                    item.put("头像", R.drawable.icon);
                    item.put("标题", events.get(i).getTitle());
                    item.put("用户", events.get(i).getLauncher());
                    item.put("时间", events.get(i).getTime());
                    item.put("悬赏", "10爱心币");
                    list.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                item.put("头像", R.drawable.icon);
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

    public List<Event> getEvent() {
        return events;
    }

    public int getCount() {return list.size();}
    public Object getItem(int position) {return position;}
    public long getItemId(int position) {return position;}

    public View getView(int position, View convertView, ViewGroup parent) {
        //头像
        ImageView iv=new ImageView(context);
        RelativeLayout.LayoutParams lp_iv=new RelativeLayout.LayoutParams(80,80);
        lp_iv.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(lp_iv);
        iv.setScaleType(ScaleType.CENTER_INSIDE);
        iv.setImageResource((Integer) ((list.get(position)).get("头像")));
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