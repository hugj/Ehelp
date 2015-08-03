package com.ehelp.user.contactlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PinyinAdapter extends BaseExpandableListAdapter {
    private List<String> strList;

    private AssortPinyinList assort = new AssortPinyinList();

    private Context context;
    private LayoutInflater inflater;
    private LanguageComparator_CN cnSort = new LanguageComparator_CN();

    public PinyinAdapter(Context context, List<String> strList) {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.strList = strList;
        if (strList == null) {
            strList = new ArrayList<String>();
        }
        long time = System.currentTimeMillis();
        sort();
        Toast toast=Toast.makeText(context,
                String.valueOf(System.currentTimeMillis() - time), Toast.LENGTH_SHORT );
        toast.show();

    }
    private void sort() {
        for (String str : strList) {
            assort.getHashList().add(str);
        }
        assort.getHashList().sortKeyComparator(cnSort);
        for(int i=0,length=assort.getHashList().size();i<length;i++) {
            Collections.sort((assort.getHashList().getValueListIndex(i)),cnSort);
        }
    }
    public Object getChild(int group, int child) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueIndex(group, child);
    }

    public long getChildId(int group, int child) {
        // TODO Auto-generated method stub
        return child;
    }

    public View getChildView(int group, int child, boolean arg2,
                             View contentView, ViewGroup arg4) {
        // TODO Auto-generated method stub
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.activity_adapterchat, null);
        }
        TextView textView = (TextView) contentView.findViewById(R.id.name);
        textView.setText(assort.getHashList().getValueIndex(group, child));
        return contentView;
    }

    public int getChildrenCount(int group) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueListIndex(group).size();
    }

    public Object getGroup(int group) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueListIndex(group);
    }

    public int getGroupCount() {
        // TODO Auto-generated method stub
        return assort.getHashList().size();
    }

    public long getGroupId(int group) {
        // TODO Auto-generated method stub
        return group;
    }

    public View getGroupView(int group, boolean arg1, View contentView,
                             ViewGroup arg3) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.list_group_item, null);
            contentView.setClickable(true);
        }
        TextView textView = (TextView) contentView.findViewById(R.id.name);
        if (assort.getFirstChar(assort.getHashList()
                .getValueIndex(group, 0)) == "#"){
            textView.setText("其他");
        } else {
            textView.setText(assort.getFirstChar(assort.getHashList()
                    .getValueIndex(group, 0)));
        }
          return contentView;
    }

    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }
    public AssortPinyinList getAssort() {
        return assort;
    }
}
