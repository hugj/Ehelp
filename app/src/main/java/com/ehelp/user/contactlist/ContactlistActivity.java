package com.ehelp.user.contactlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.User;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.user.lovebank.TransferActivity;
import com.ehelp.utils.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.androidinject.annotation.annotations.base.AILayout;
import com.wangjie.androidinject.annotation.annotations.base.AIView;
import com.wangjie.androidinject.annotation.present.AIActionBarActivity;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_contactlist)
public class ContactlistActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
	@AIView(R.id.label_list_sample_rfal)
	private RapidFloatingActionLayout rfaLayout;
	@AIView(R.id.label_list_sample_rfab)
	private RapidFloatingActionButton rfaButton;
	private RapidFloatingActionHelper rfabHelper;
	private IphoneTreeView mIphoneTreeView;
	private ConstactAdapter mExpAdapter;
	private List<Group> listGroup;
	private List<User> userList;
	private SharedPreferences sharedPref;
	private String jsonStrng, message;
	private int id;
	Gson gson = new Gson();

	private Toolbar mToolbar;

	private int emp,emp_;//判断是从哪个地方跳入到此页面的，1为爱心币转朋友，0为通讯录

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent =  getIntent();
		emp = intent.getIntExtra("extra", 0);//默认0
		emp_ = intent.getIntExtra("coin", 0);//从CoinActivity传入的爱心币数
		initView();
		init();//fab
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
				"\"type\":" + 0 + "," +
				"\"state\": 0}";
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
				"\"state\":" + 0 + "," +
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
		//画出通讯录
		mIphoneTreeView = (IphoneTreeView) findViewById(R.id.iphone_tree_view);
		mIphoneTreeView.setHeaderView(LayoutInflater.from(this)
				.inflate(R.layout.fragment_constact_head_view, mIphoneTreeView, false));
		mIphoneTreeView.setGroupIndicator(null);
	}
	private  void init() {
		StrictMode.setThreadPolicy(
				new StrictMode.ThreadPolicy.Builder().
						detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
				detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
				penaltyLog().penaltyDeath().build());
		//set toolbar
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("  ");
		setSupportActionBar(mToolbar);
		TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
		tvv.setText("通讯录");

		//下面的圆型按钮
		fab();
	}
	private void fab(){
		RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(context);
		rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
		List<RFACLabelItem> items = new ArrayList<>();
		items.add(new RFACLabelItem<Integer>()
						.setLabel("求救")
						.setResId(R.mipmap.ic_launcher)
						.setIconNormalColor(0xffd84315)
						.setIconPressedColor(0xffbf360c)
						.setWrapper(0)
		);
		items.add(new RFACLabelItem<Integer>()
						.setLabel("求助")
//                        .setResId(R.mipmap.ico_test_c)
						.setDrawable(getResources().getDrawable(R.mipmap.ic_launcher))
						.setIconNormalColor(0xff4e342e)
						.setIconPressedColor(0xff3e2723)
						.setLabelColor(Color.WHITE)
						.setLabelSizeSp(14)
						.setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(0xaa000000, ABTextUtil.dip2px(context, 4)))
						.setWrapper(1)
		);
		items.add(new RFACLabelItem<Integer>()
						.setLabel("提问")
						.setResId(R.mipmap.ic_launcher)
						.setIconNormalColor(0xff056f00)
						.setIconPressedColor(0xff0d5302)
						.setLabelColor(0xff056f00)
						.setWrapper(2)
		);
		rfaContent
				.setItems(items)
				.setIconShadowRadius(ABTextUtil.dip2px(context, 5))
				.setIconShadowColor(0xff888888)
				.setIconShadowDy(ABTextUtil.dip2px(context, 5))
		;

		rfabHelper = new RapidFloatingActionHelper(
				context,
				rfaLayout,
				rfaButton,
				rfaContent
		).build();
	}
	@Override
	public void onRFACItemLabelClick(int position, RFACLabelItem item) {
		if (position == 0) {
			Intent intent = new Intent(this, CountNum.class);
			startActivity(intent);
		} else
		if (position == 1) {
			Intent intent = new Intent(this, sendhelp_map.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, SendQuestion.class);
			startActivity(intent);
		}
		rfabHelper.toggleContent();
	}

	@Override
	public void onRFACItemIconClick(int position, RFACLabelItem item) {
		if (position == 0) {
			Intent intent = new Intent(this, CountNum.class);
			startActivity(intent);
		} else
		if (position == 1) {
			Intent intent = new Intent(this, sendhelp_map.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, SendQuestion.class);
			startActivity(intent);
		}
		rfabHelper.toggleContent();
	}
	//toolbar设置
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_contactlist, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			//添加好友。跳转至添加好友页面
			Intent intent = new Intent(this, AddFriendActivity.class);
			intent.putExtra("QueryPerson", 0);
			intent.putExtra("coin", 0);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}//toolbar设置结束

	//点击查看联系人的信息
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
						String name = jO.getString("name");
						if (emp == 0) { //判断是从通讯录跳转过来的还是转账跳转来的，0为通讯录
							int type;
							//判断是紧急联系人还是好友
							if (arg2 == 0) {
								type = 2;
							} else {
								type = 1;
							}
							Intent intent = new Intent(ContactlistActivity.this, messageActivity.class);
							intent.putExtra("id", id);//传参来确定显示的界面
							startActivity(intent);
						} else {//1为转账，跳到转账页面并传参
							Intent intent = new Intent(ContactlistActivity.this, TransferActivity.class);
							intent.putExtra("id", id);
							intent.putExtra("name", name);
							intent.putExtra("coin", emp_);//用户的爱心币数
							startActivity(intent);
						}

					}
				}catch (JSONException e) {
					e.printStackTrace();
				}
//				Toast.makeText(getApplicationContext(), "点击"+arg2, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
	}
	@Override
    protected void onResume() {
		Drawcontactlist();
		super.onResume();
    }
}
