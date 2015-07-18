package com.ehelp.user.pinyin;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ehelp.user.pinyin.AssortView.OnTouchAssortListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
	/** Called when the activity is first created. */

	private PinyinAdapter adapter;
	private ExpandableListView eListView;
	private AssortView assortView;
	private List<String> names;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		eListView = (ExpandableListView) findViewById(R.id.elist);
		assortView = (AssortView) findViewById(R.id.assort);
		names=new ArrayList<String>();
		names.add("lxz");
		names.add("Ada");
		names.add("ܽming");
		names.add("nini");
		names.add("dada");
		names.add("jingjing");
		names.add("lucy");
		names.add("er");
		names.add("L");
		names.add("xdsfsdggs");
		names.add("diao");
		names.add("nih");
		names.add("Java");
		names.add("gen");
		names.add("qianqian");
		names.add("有道");
		names.add("明明");
		names.add("yes");
		names.add("jack");
		names.add("jackson");
		adapter = new PinyinAdapter(this,names);
		eListView.setAdapter(adapter);

		for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
			eListView.expandGroup(i);
		}
		assortView.setOnTouchAssortListener(new OnTouchAssortListener() {
			
			View layoutView=LayoutInflater.from(MainActivity.this)
					.inflate(R.layout.alert_dialog_menu_layout, null);
			TextView text =(TextView) layoutView.findViewById(R.id.content);
			PopupWindow popupWindow ;
			
			public void onTouchAssortListener(String str) {
			   int index=adapter.getAssort().getHashList().indexOfKey(str);
			   if(index!=-1) {
					eListView.setSelectedGroup(index);;
			   }
				if(popupWindow!=null){
				text.setText(str);
				}
				else {
				      popupWindow = new PopupWindow(layoutView,
							80, 80,	false);
					popupWindow.showAtLocation(getWindow().getDecorView(),
							Gravity.CENTER, 0, 0);
				}
				text.setText(str);
			}
			public void onTouchAssortUP() {
				if(popupWindow!=null)
				popupWindow.dismiss();
				popupWindow=null;
			}
		});
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}