package com.ehelp.user.contactlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.User;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ContactlistActivity extends Activity {
	
	private IphoneTreeView mIphoneTreeView;
	private ConstactAdapter mExpAdapter;
	private List<Group> listGroup;
	private List<User> userList;
	private SharedPreferences sharedPref;
	private String jsonStrng, message;
	private int id;
	Gson gson = new Gson();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactlist);
		initView();
		Drawcontactlist();
		click_on_message();
	}

	private void Drawcontactlist() {
		listGroup=new ArrayList<Group>();
		sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
		id = sharedPref.getInt("user_id", -1);
		jsonStrng = "{" +
				"\"id\":" + id + "," +
				"\"operation\":" + 2 + "," +
				"\"type\":" + 0 +"}";
		message = RequestHandler.sendPostRequest(
				"http://120.24.208.130:1501/user/relation_manage", jsonStrng);
		//紧急联系人列表
		Group group=new Group();
		List<Child> listChild=new ArrayList<Child>();
		group.setGroupName("我的紧急联系人");
		if (message == "false") {
			Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
					Toast.LENGTH_SHORT).show();
		}
		try {
			JSONObject jO = new JSONObject(message);
			if (jO.getInt("status") == 500) {
				Toast.makeText(getApplicationContext(), "您当前没有紧急联系人",
						Toast.LENGTH_SHORT).show();
			} else {
				String user_list = jO.getString("user_list");
				userList = gson.fromJson(user_list, new TypeToken<List<User>>(){}.getType());
				for (int i = 0; i < userList.size(); i++) {
					Child child=new Child();
					String emp,emp_, phone;
					emp = userList.get(i).getNickname();
					emp_ = userList.get(i).getName();
					//假如昵称为空则设置为用户名
					if (emp == "") {
						child.setUsername(emp_);
					} else {
						child.setUsername(emp);
					}
					phone = userList.get(i).getPhone();
					child.setHeadphoto("http://d.hiphotos.baidu.com/zhidao/pic/item/562c11dfa9ec8a13e028c4c0f603918fa0ecc0e4.jpg");
					child.setPhone(phone);
					child.setOnline_status("1");//1在线，0不在线
					listChild.add(child);
				}
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		group.setChildList(listChild);
		listGroup.add(group);

		//好友列表
		group=new Group();
		listChild=new ArrayList<Child>();
		group.setGroupName("我的好友");
		//获取好友列表
		jsonStrng = "{" +
				"\"id\":" + id + "," +
				"\"operation\":" + 2 + "," +
				"\"type\":" + 2 +"}";
		message = RequestHandler.sendPostRequest(
				"http://120.24.208.130:1501/user/relation_manage", jsonStrng);
		if (message == "false") {
			Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
					Toast.LENGTH_SHORT).show();
		}
		try {
			JSONObject jO = new JSONObject(message);
			if (jO.getInt("status") == 500) {
				Toast.makeText(getApplicationContext(), "您当前没有好友",
						Toast.LENGTH_SHORT).show();
			} else {
				String user_list = jO.getString("user_list");
				userList = gson.fromJson(user_list, new TypeToken<List<User>>(){}.getType());
				for (int i = 0; i < userList.size(); i++) {
					Child child=new Child();
					String emp, emp_, phone;
					emp = userList.get(i).getNickname();
					emp_ = userList.get(i).getName();
					//假如昵称为空则设置为用户名
					if (emp == "") {
						child.setUsername(emp_);
					} else {
						child.setUsername(emp);
					}
					phone = userList.get(i).getPhone();
					child.setUsername(emp);
					child.setHeadphoto("http://wenwen.soso.com/p/20090901/20090901120135-1666292770.jpg");
					child.setPhone(phone);
                    child.setOnline_status("1");//1在线，0不在线
					listChild.add(child);
				}
			}
		}catch (JSONException e) {
			e.printStackTrace();
		}
		group.setChildList(listChild);
		listGroup.add(group);

		mExpAdapter = new ConstactAdapter(this, listGroup, mIphoneTreeView);
		mIphoneTreeView.setAdapter(mExpAdapter);
	}

	private void initView() {
		mIphoneTreeView = (IphoneTreeView) findViewById(R.id.iphone_tree_view);
		mIphoneTreeView.setHeaderView(LayoutInflater.from(this)
				.inflate(R.layout.fragment_constact_head_view, mIphoneTreeView, false));
		mIphoneTreeView.setGroupIndicator(null);
	}
	private void click_on_message() {
		mIphoneTreeView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,int arg3, long arg4) {
				/*
				 *arg0发生点击动作的ExpandableListView,arg1视图view，arg2 group在ExpandableListView
				 *的位置索引 arg3 child在group的位置，arg4child的行id
				 */
				String Phone = mExpAdapter.getChild(arg2, arg3).getPhone();
				jsonStrng = "{" +
						"\"phone\":\"" + Phone + "\"}";
				message = RequestHandler.sendPostRequest(
						"http://120.24.208.130:1501/user/get_information", jsonStrng);
				if (message == "false") {
					Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
							Toast.LENGTH_SHORT).show();
				}
				try {
					JSONObject jO = new JSONObject(message);
					if (jO.getInt("status") == 500) {
						Toast.makeText(getApplicationContext(), "没有此用户",
								Toast.LENGTH_SHORT).show();
					} else {
						int id = jO.getInt("id");
						int type;
						//判断是紧急联系人还是好友
						if (arg2 == 0) {
							type = 2;
						} else {
							type = 1;
						}
						Intent intent = new Intent(ContactlistActivity.this, messageActivity.class);
						intent.putExtra("type",type);
						intent.putExtra("id",id);
						startActivity(intent);
					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
//				Toast.makeText(getApplicationContext(), "点击"+arg2, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}
}
