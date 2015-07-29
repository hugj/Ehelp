package com.ehelp.home;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;

import org.json.JSONArray;
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

    private Context context=null;
    private List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    private Map<String,Object> item;
    private String tit;
    private double longitude;
    private double latitude;
    private int user_id;

    HomeAdapter(Context context, int id){
        this.context=context;
        user_id = id;
        //数据初始化

        getList();
    }

    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -8) {
                String jsonStrng = "{" +
                        "\"id\":" + user_id + "}";
                String message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/user/get_information", jsonStrng);
                if (message == "false") {
                    item=new HashMap<String,Object>();
                    item.put("头像", R.drawable.icon);
                    item.put("标题", "连接失败，请检查网络是否连接并重试");
                    item.put("用户", "");
                    item.put("悬赏", "10爱心币");
                    list.add(item);
                }   else {
                    try {
                        JSONObject jO = new JSONObject(message);
                        longitude = jO.getDouble("longitude");
                        latitude = jO.getDouble("latitude");

                        jsonStrng = "{" +
                                "\"longitude\":" + longitude +
                                ",\"latitude\":" + latitude + ",\"state\":0," +
                                "\"type\":0" + "}";

                        message = RequestHandler.sendPostRequest(
                                "http://120.24.208.130:1501/event/get_nearby_event", jsonStrng);
                        if (message == "false") {
                            item=new HashMap<String,Object>();
                            item.put("头像", R.drawable.icon);
                            item.put("标题", "连接失败，请检查网络是否连接并重试");
                            item.put("用户", "");
                            item.put("悬赏", "10爱心币");
                            list.add(item);
                        } else {
                            JSONObject j1 = new JSONObject(message);
                            JSONArray eventList = j1.getJSONArray("event_list");
                            for (int i = 0; i < eventList.length(); i++) {
                                item=new HashMap<String,Object>();
                                item.put("头像", R.drawable.icon);
                                item.put("标题", eventList.getJSONObject(i).getString("content"));
                                item.put("用户", eventList.getJSONObject(i).getString("launcher"));
                                item.put("悬赏", "10爱心币");
                                list.add(item);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };



    public void getList() {

        //数据初始化
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(-8);
            }
        }).start();
    }

    public int getCount() {return list.size();}
    public Object getItem(int position) {return position;}
    public long getItemId(int position) {return position;}

    public View getView(int position, View convertView, ViewGroup parent) {
        //头像
        ImageView iv=new ImageView(context);
        RelativeLayout.LayoutParams lp_iv=new RelativeLayout.LayoutParams(70,70);
        lp_iv.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(lp_iv);
        iv.setScaleType(ScaleType.CENTER_INSIDE);
        iv.setImageResource((Integer)((list.get(position)).get("头像")));
        //标题
        TextView name=new TextView(context);
        RelativeLayout.LayoutParams lp_tv=new RelativeLayout.LayoutParams(-2,-2);
        lp_tv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        name.setLayoutParams(lp_tv);
        name.setTextSize(name.getTextSize()+10);
        name.setText((String)(list.get(position).get("标题")));
        //用户
        TextView sex=new TextView(context);
        RelativeLayout.LayoutParams lp_sex=new RelativeLayout.LayoutParams(-2,-2);
        lp_sex.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sex.setLayoutParams(lp_sex);
        sex.setText((String)(list.get(position).get("用户")));
        //悬赏
        TextView  age=new TextView(context);
        RelativeLayout.LayoutParams lp_age=new RelativeLayout.LayoutParams(-2,-2);
        lp_age.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        age.setLayoutParams(lp_age);
        age.setText((String)(list.get(position).get("悬赏")));
        //相对布局1
        RelativeLayout rl1=new RelativeLayout(context);
        rl1.setLayoutParams(new RelativeLayout.LayoutParams(-1,-2));
        rl1.addView(iv);
        rl1.addView(name);
        //相对布局2
        RelativeLayout rl2=new RelativeLayout(context);
        rl2.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        rl2.addView(sex);
        rl2.addView(age);
        //返回视图
        LinearLayout returnView=new LinearLayout(context);
        returnView.setLayoutParams(new ListView.LayoutParams(-1,-2));//注意:ListView.LayoutParams
        returnView.setOrientation(LinearLayout.VERTICAL);
        returnView.addView(rl1);
        returnView.addView(rl2);

        return returnView;
    }

}