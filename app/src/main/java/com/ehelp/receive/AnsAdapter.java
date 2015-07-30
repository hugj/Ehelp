package com.ehelp.receive;

import android.content.Context;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ehelp.R;
import com.ehelp.entity.answer;
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
 * Created by gj on 2015/7/30.
 */
public class AnsAdapter extends BaseAdapter {

    private Context context=null;
    private List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    private Map<String,Object> item;
    private String tit;
    private double longitude;
    private double latitude;
    private int event_id;
    private int type;
    private List<answer> answerList;
    Gson gson = new Gson();

    AnsAdapter(Context context, int id){
        this.context=context;
        event_id = id;

        // 使用后台线程运行网络连接功能
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //数据初始化
        setList();
    }


    public void setList() {
        //数据初始化
        try {
            String jsonStrng = "{" +
                    "\"event_id\":" + 12 + "}";
            String message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/event/anslist", jsonStrng);
            if (message == "false") {
                item=new HashMap<String,Object>();
                item.put("头像", R.drawable.icon);
                item.put("内容", "连接失败，请检查网络是否连接并重试");
                item.put("用户", "");
                item.put("时间", "");
                item.put("采纳", "");
                list.add(item);
            } else {
                JSONObject j1 = new JSONObject(message);
                if (j1.getInt("status") == 500) {
                    item=new HashMap<String,Object>();
                    item.put("头像", R.drawable.icon);
                    item.put("内容", "因为迷之原因拉取不到回答。。。");
                    item.put("用户", "");
                    item.put("时间", "");
                    item.put("采纳", "");
                    list.add(item);
                }
                String st = j1.getString("answer_list");
                answerList = gson.fromJson(st, new TypeToken<List<answer>>(){}.getType());
                answer bb = answerList.get(0);
                String aa = answerList.get(0).getContent();
                for (int i = 0; i < answerList.size(); i++) {
                    item=new HashMap<String,Object>();
                    item.put("头像", R.drawable.icon);
                    item.put("内容", answerList.get(i).getContent());
                    item.put("用户", answerList.get(i).getAuthoridId());
                    item.put("时间", answerList.get(i).getTime());
                    item.put("采纳", answerList.get(i).getIs_adopted());
                    list.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setImageResource((Integer) ((list.get(position)).get("头像")));
        //内容
        TextView title=new TextView(context);
        RelativeLayout.LayoutParams lp_tv=new RelativeLayout.LayoutParams(-2,-2);
        lp_tv.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        title.setLayoutParams(lp_tv);
        title.setTextSize(title.getTextSize());
        title.setText((String) (list.get(position).get("内容")));
        //用户
        TextView user=new TextView(context);
        RelativeLayout.LayoutParams lp_sex=new RelativeLayout.LayoutParams(-2,-2);
        lp_sex.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        user.setLayoutParams(lp_sex);
        user.setText((list.get(position).get("用户")) + "");
        //时间
        TextView time=new TextView(context);
        RelativeLayout.LayoutParams lp_time=new RelativeLayout.LayoutParams(-2,-2);
        lp_sex.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        time.setLayoutParams(lp_sex);
        time.setText((String)(list.get(position).get("时间")));
        //采纳
        TextView  Reward=new TextView(context);
        RelativeLayout.LayoutParams lp_age=new RelativeLayout.LayoutParams(-2,-2);
        lp_age.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        Reward.setLayoutParams(lp_age);
        Reward.setText((list.get(position).get("采纳")) + "");

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
        rl1.setLayoutParams(new RelativeLayout.LayoutParams(-1,-2));
        rl1.addView(l12);
        rl1.addView(Reward);
        //返回视图
        LinearLayout returnView=new LinearLayout(context);
        returnView.setLayoutParams(new ListView.LayoutParams(-1,-2));//注意:ListView.LayoutParams
        returnView.setOrientation(LinearLayout.VERTICAL);
        returnView.addView(rl1);
        returnView.addView(title);

        return returnView;
    }

}