package com.ehelp.user.lovebank;

import android.content.Context;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ehelp.R;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/13.
 */
public class CoinhistoryAdapter extends BaseAdapter {
    private Context context=null;
    private List<Map<String,Object>> list1 = new ArrayList<Map<String,Object>>();
    private List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
    private List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
    private Map<String,Object> item;
    private int user_id;
    private int type;
    Gson gson = new Gson();

    //构造函数,获取的用户Id，type_为转账记录或交易记录
    CoinhistoryAdapter(Context context, int id, int type_){
        this.context=context;
        user_id = id;
        type = type_;
        // 使用后台线程运行网络连接功能
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());
        setlistitem();//获取到每个item
    }
    //获取转账的历史记录(后台线程)
    public String getTrandata1() { //转出
        String jsonStrng = "{" +
                "\"id\":" + user_id + ",\"type\":0"+ "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/check_trans", jsonStrng);
        return message;
    }
    public String getTrandata2() {//转入
        String jsonStrng = "{" +
                "\"id\":" + user_id + ",\"type\":1"+ "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/check_trans", jsonStrng);
        return message;
    }
    //获取交易历史记录
    public String getEventdata1() { //发送
        String jsonStrng = "{" +
                "\"sender\":" + user_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/check_trade", jsonStrng);
        return message;
    }
    public String getEventdata2() {//接收
        String jsonStrng = "{" +
                "\"receiver\":" + user_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/check_trade", jsonStrng);
        return message;
    }
    public void setlistitem() {
        String message1, message2, message;
        if (type == 1) { //转账
            message1 = getTrandata1(); //转出
            message2 = getTrandata2();//转入
        } else { //交易
            message1 = getEventdata1();
            message2 = getEventdata2();
        }
        for (int i = 0; i < 2; i++) {//将接收的和发送的数据都加入到list里
            if (i == 0) {
                message = message1;
            } else {
                message = message2;
            }
            if (message == "false") {
                item = new HashMap<String, Object>();
                item.put("date", "");
                item.put("man", "");
                item.put("num", "连接失败，请检查网络是否连接并重试");
                if (i == 0) { //如果是转出的
                    list1.add(item);
                    list.add(item);
                } else { //如果是转入的
                    list2.add(item);
                    list.add(item);
                }
            } else {
                try {
                    JSONObject j1 = new JSONObject(message);
                    if (j1.getInt("status") == 500) {
                        item = new HashMap<String, Object>();
                        item.put("date", "");
                        item.put("man", "");
                        item.put("num", "连接失败，请检查网络是否连接并重试");
                        if (i == 0) { //如果是转出的
                            list1.add(item);
                            list.add(item);
                        } else { //如果是转入的
                            list2.add(item);
                            list.add(item);
                        }
                    } else {
                        JSONArray emp = j1.getJSONArray("h_list");
                        if (i == 0) { //转给其他人
                            for (int j = 0; j < emp.length(); j++) { //每个都加入到list里
                                item = new HashMap<String, Object>();
                                item.put("date", emp.getJSONObject(j).getString("time"));
                                item.put("man", emp.getJSONObject(j).getString("receiver"));
                                item.put("num", emp.getJSONObject(j).getInt("love_coin"));
                                list1.add(item);
                                list.add(item);
                            }
                        } else { //别人转给你的
                            for (int j = 0; j < emp.length(); j++) {
                                item = new HashMap<String, Object>();
                                item.put("date", emp.getJSONObject(j).getString("time"));
                                item.put("man", emp.getJSONObject(j).getString("sender"));
                                item.put("num", emp.getJSONObject(j).getInt("love_coin"));
                                list2.add(item);
                                list.add(item);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public List<Map<String,Object>> getList() {
       return list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView c_time = null;
        TextView c_name = null;
        TextView c_coin = null;
        TextView c_time1 = null;
        TextView c_name1 = null;
        TextView c_coin1 = null;
        if (convertView == null) {
            //你转给别人的记录
            convertView = LayoutInflater.from(context).inflate(R.layout.coin_historyitem, null);
            c_time = (TextView) convertView.findViewById(R.id.date1);
            c_name = (TextView) convertView.findViewById(R.id.receiver1);
            c_coin = (TextView) convertView.findViewById(R.id.his_num1);
            c_time.setText((String) (list1.get(position).get("date")));
            c_name.setText((String) (list1.get(position).get("man")));
            int n = (int) list1.get(position).get("num");
            String str = Integer.toString(n);
            c_coin.setText(str);
            //别人转给你的记录
            c_time1 = (TextView) convertView.findViewById(R.id.date2);
            c_name1 = (TextView) convertView.findViewById(R.id.sender1);
            c_coin1 = (TextView) convertView.findViewById(R.id.his_num1);
            c_time1.setText((String) (list2.get(position).get("date")));
            c_name1.setText((String) (list2.get(position).get("man")));
            int n_ = (int) list2.get(position).get("num");
            String str_ = Integer.toString(n_);
            c_coin1.setText(str_);
        }
        return convertView;
    }
}