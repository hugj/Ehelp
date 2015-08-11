package com.ehelp.receive;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.Event;
import com.ehelp.entity.answer;
import com.ehelp.home.Home;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.utils.ActivityCollector;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_question_detail)
public class QuestionDetail extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private Toolbar mToolbar;

    // submit()
    private Event m_event;
    private EditText Equestion;
    private EditText Edesc_ques;
    private EditText Eshare_money;
    private String question;
    private String share_money;
    private String desc_ques;
    public final static String EXTRA_MESSAGE = "com.ehelp.receive.MESSAGE";
    private AlertDialog AnsDialog = null;
    private int phone_user_id;
    private List<answer> answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        m_event = (Event)intent.getSerializableExtra("qusetiondatail");
        init();
        setView();
        ActivityCollector.getInstance().addActivity(this);
    }

    private void init() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //set toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("问题详情");

        SharedPreferences sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        phone_user_id = sharedPref.getInt("user_id", -1);

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

    public String getNickname() {
        final int user_id = m_event.getLauncherId();
        String nickname = "";
        String jsonStrng = "{" +
                "\"id\":" + user_id + "}";
        final String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/get_information", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }   else {
            try {
                JSONObject jO = new JSONObject(message);
                nickname = jO.getString("nickname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nickname;
    }

    public void setView(){
        //问题详情
        TextView tmp = (TextView)findViewById(R.id.user_name);
        tmp.setText(getNickname());
        tmp = (TextView)findViewById(R.id.Title);
        tmp.setText(m_event.getTitle());
        tmp = (TextView)findViewById(R.id.Content);
        tmp.setText(m_event.getContent());

        String imageUrl = "http://120.24.208.130:1501/avatar/touxiang.jpg";
        //Uri u = Uri.parse(imageUrl);
        ImageView imView;
        imView = (ImageView) findViewById(R.id.single_icon1);
        //imView.setImageURI(u);
        imView.setImageBitmap(returnBitMap(imageUrl));

        //回答列表
        ListView ansList = (ListView)findViewById(R.id.answerList);
        int event_id = m_event.getEventId();
        AnsAdapter ans = new AnsAdapter(this, event_id);
        ansList.setAdapter(ans);
        
        answers = ans.getAnswerList();

        //绑定监听
        if (phone_user_id == m_event.getLauncherId()) {
            ansList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                    if (answers.get(index).getIs_adopted() == 0) {
                        onClickAns(answers.get(index).getId());
                    }
                }
            });
        }
    }

    /*
* 监听点击回答
* */
    public void onClickAns(int ansID_) {
        final int ansID = ansID_;
        AnsDialog = new AlertDialog.Builder(QuestionDetail.this).create();
        AnsDialog.show();
        AnsDialog.getWindow().setContentView(R.layout.response_comment);

        //设置弹出框内容
        LinearLayout pop = (LinearLayout)AnsDialog.getWindow().findViewById(R.id.pop);
        LinearLayout resp = new LinearLayout(this);
        TextView respText=new TextView(this);
        respText.setText("采纳该回答");
        respText.setTextSize(20);
        resp.addView(respText);
        pop.addView(resp);
        //点击
        resp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change(ansID);
                AnsDialog.dismiss();
            }
        });

    }

    /*
    * 修改采纳回答相关信息
    * */
    public void change(int ansID){
        String jsonStrng = "{" +
                "\"answer_id\":" + ansID +
                ",\"is_adopted\":" + 1 + "}";
        final String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/event/update_answer", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    Toast.makeText(getApplicationContext(), "因为迷之原因采纳失败。。。",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "采纳回答成功",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QuestionDetail.this, Home.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ans) {
            //点击添加回答
            Intent intent = new Intent(this, AddAns.class);
            intent.putExtra(EXTRA_MESSAGE, m_event.getEventId());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
//设置完毕
    /*
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.action_ans:
                    msg += "Click edit";
                    break;
            }
            return true;
        }
    };*/
}
