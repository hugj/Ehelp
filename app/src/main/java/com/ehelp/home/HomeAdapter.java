package com.ehelp.home;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.ehelp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gj on 2015/7/27.
 */
public class HomeAdapter extends BaseAdapter {

    Context context=null;
    List<Map<String,Object>> list=null;

    HomeAdapter(Context context){
        this.context=context;
        //数据初始化
        list=new ArrayList<Map<String,Object>>();
        Map<String,Object> item;
        item=new HashMap<String,Object>();
        item.put("头像", R.drawable.icon);
        item.put("姓名","11111");
        item.put("性别","男");
        item.put("年龄", "10爱心币");
        list.add(item);
        item=new HashMap<String,Object>();
        item.put("头像",R.drawable.icon);
        item.put("姓名","22222");
        item.put("性别","男");
        item.put("年龄", "1爱心币");
        list.add(item);
        item=new HashMap<String,Object>();
        item.put("头像",R.drawable.icon);
        item.put("姓名","33333");
        item.put("性别","女");
        item.put("年龄", "0爱心币");
        list.add(item);
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
        //姓名
        TextView name=new TextView(context);
        RelativeLayout.LayoutParams lp_tv=new RelativeLayout.LayoutParams(-2,-2);
        lp_tv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        name.setLayoutParams(lp_tv);
        name.setTextSize(name.getTextSize()+10);
        name.setText((String)(list.get(position).get("姓名")));
        //性别
        TextView sex=new TextView(context);
        RelativeLayout.LayoutParams lp_sex=new RelativeLayout.LayoutParams(-2,-2);
        lp_sex.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        sex.setLayoutParams(lp_sex);
        sex.setText((String)(list.get(position).get("性别")));
        //年龄
        TextView  age=new TextView(context);
        RelativeLayout.LayoutParams lp_age=new RelativeLayout.LayoutParams(-2,-2);
        lp_age.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        age.setLayoutParams(lp_age);
        age.setText((String)(list.get(position).get("年龄")));
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