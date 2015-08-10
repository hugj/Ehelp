package com.ehelp.user.contactlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.home.Home;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.utils.RequestHandler;
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

import io.rong.imkit.RongIM;

@AILayout(R.layout.activity_message)
public class messageActivity extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    private RelativeLayout myLay = null;
    private AlertDialog myDialog = null;

    //TOOLbar
    private Toolbar mToolbar;
    private TextView Setname;

    private int type = 0;//传进来的参数，用户的类型，0非好友，1好友，2紧急联系人
    private int idd;//要查看的用户的id
    private int id;//当前用户id

    //与后台联系的变量
    private String message;
    private String jsonStrng;
    private SharedPreferences SharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_message);

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("详细资料");

        Intent intent = getIntent();
        type = intent.getIntExtra("type",0);//默认0非好友。1表示好友，2表示紧急联系人
        idd = intent.getIntExtra("id",-1);//intent时传入的id
        SharedPref = this.getSharedPreferences("user_id", MODE_PRIVATE);
        id = SharedPref.getInt("user_id", -1);

        //根据用户类型初始化页面
        init();

        //根据用户ID从后台获取数据显示信息详情
        show123();

        //add contact添加紧急联系人
        Button add_excontact = (Button)findViewById(R.id.addexcontact);
        add_excontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageActivity.this.add_dialog();
            }
        });
        //delete contact 删除好友
        Button Delete_contact = (Button)findViewById(R.id.delete_contact);
        Delete_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageActivity.this.delete_dialog();
            }
        });
    }

    protected void init(){
        if(type == 0){
            Button btn = (Button)findViewById(R.id.addexcontact);
            btn.setVisibility(View.INVISIBLE);
            Button btn2 = (Button)findViewById(R.id.delete_contact);
            btn2.setText("添加为好友");
        }else if(type == 1){

        }else if(type == 2){
            Button btn = (Button)findViewById(R.id.addexcontact);
            btn.setText("删除紧急联系人");
            Button btn2 = (Button)findViewById(R.id.delete_contact);
            btn2.setVisibility(View.INVISIBLE);
        }else {
            Toast.makeText(getApplicationContext(), "参数传入错误",
                    Toast.LENGTH_SHORT).show();
        }
        //set FAB
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

    //显示详细资料的信息
    protected void show123(){
        if(idd != -1){
            jsonStrng = "{" +
                    "\"id\":" + idd + "}";
            message = RequestHandler.sendPostRequest(
                    "http://120.24.208.130:1501/user/get_information", jsonStrng);
            if (message == "false") {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            try{
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "查询数据库错误",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(getApplicationContext(), "用户未注册",
//                            Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    Toast.makeText(getApplicationContext(), "查询数据库成功",
                            Toast.LENGTH_SHORT).show();
                    //修改显示的用户名
                    TextView tv =(TextView) findViewById(R.id.detail_name2);
                    String emp,emp_;
                    emp = jO.getString("nickname");
                    emp_ = jO.getString("name");
                    if(emp.equals("")) {
                        tv.setText(emp_);
                    } else {
                        tv.setText(emp);
                    }
                    //修改显示的手机号
                    TextView tv1 =(TextView) findViewById(R.id.detail_phone2);
                    tv1.setText(jO.getString("phone"));
                    //修改显示的备注姓名
//                    TextView tv2 =(TextView) findViewById(R.id.detail_setname2);
//                    tv2.setText("");
                    //显示个人职业
                    TextView tv2 = (TextView)findViewById(R.id.detail_job2);
                    int i = jO.getInt("occupation");
                    switch (i) {
                        case 0:
                            tv2.setText(R.string.mes_job1);
                            break;
                        case 1:
                            tv2.setText(R.string.mes_job2);
                            break;
                        case 2:
                            tv2.setText(R.string.mes_job3);
                            break;
                        case 3:
                            tv2.setText(R.string.mes_job4);
                            break;
                        case 4:
                            tv2.setText(R.string.mes_job5);
                            break;
                        case 5:
                            tv2.setText(R.string.mes_job6);
                            break;
                    }
                    //修改显示的性别
                    TextView tv3 =(TextView) findViewById(R.id.detail_file2);
                    if(jO.getInt("gender")==1) {
                        tv3.setText("男");
                    }
                    //修改显示的所在地
                    TextView tv4 =(TextView) findViewById(R.id.detail_loacl2);
                    tv4.setText(jO.getString("location"));
                    //修改显示的年龄
                    TextView tv5 =(TextView) findViewById(R.id.detail_age2);
                    tv5.setText(String.valueOf(jO.getInt("age")));
                    return;
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "无此用户",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //add ex contact
    protected void add_dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(type == 2){
            builder.setMessage("是否删除紧急联系人（保留好友）？");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(0,0);
                    manage_relation(1,2);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",1);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();


                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else {
            builder.setMessage("是否添加为紧急联系人？");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(1, 0);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",2);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

    }
    //删除好友
    protected void delete_dialog() {
        AlertDialog.Builder delete = new AlertDialog.Builder(this);
        if(type == 0){
            delete.setMessage("确定添加为好友？");
            delete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(1, 2);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",1);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();
                }
            });
            delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            delete.create().show();
        }else {
            delete.setMessage("确定删除此好友？");
            delete.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    manage_relation(0, 2);
                    //刷新当前页面
                    Intent intent = new Intent(messageActivity.this, messageActivity.class);
                    intent.putExtra("type",0);//0表示非好友1表示好友2表示紧急联系人
                    intent.putExtra("id",idd);
                    startActivity(intent);
                    messageActivity.this.finish();
                }
            });
            delete.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            delete.create().show();
        }

    }


    public void manage_relation(int a,int b){
        //a代表0删除关系或1添加关系
        //b代表0紧急联系人操作2好友操作
        int id = SharedPref.getInt("user_id", -1);

        jsonStrng = "{" +
                "\"id\":" + id + "," +
                "\"user_id\":" + idd + "," +
                "\"operation\":" + a + "," +
                "\"type\":" + b + "}";
        message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/relation_manage", jsonStrng);
        if (message == "false") {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        try{
            JSONObject jO = new JSONObject(message);
            if (jO.getInt("status") == 500) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //返回500
                        Toast.makeText(getApplicationContext(), "操作失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }else {
                //返回200
                Toast.makeText(getApplicationContext(), "操作成功",
                        Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //toolbar设置
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ((id == R.id.action_conversation)){
            String IMid = String.valueOf(idd);
            RongIM.getInstance().startPrivateChat(messageActivity.this, IMid, "聊天");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
