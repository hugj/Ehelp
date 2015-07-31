package com.ehelp.send;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.home.Home;
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

import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_send_help)
public class SendHelp extends AIActionBarActivity implements RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;
    private Toolbar mToolbar;

    //submit()
    private EditText Eevents;
    private EditText Edesc_event;
    private EditText Eshare_money;
    private EditText Eneed_peo;
    private String event;
    private String share_money;
    private String desc_event;
    public String jingdu;
    public String weidu;
    public String need_peo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_test);
        Intent intent = getIntent();
        init();

        ActivityCollector.getInstance().addActivity(this);

        //get the information of sendhelp_map location
        Bundle bunde = this.getIntent().getExtras();
        jingdu = bunde.getString("longitude").toString();
        weidu = bunde.getString("latitude").toString();
        Toast.makeText(SendHelp.this, jingdu + weidu, Toast.LENGTH_SHORT).show();
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
        setSupportActionBar(mToolbar);
        TextView tvv =(TextView) findViewById(R.id.titlefortoolbar);
        tvv.setText("发送求助信息");
        //FAB
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
            showToastMessage("您正在求助界面");
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
            showToastMessage("您正在求助界面");
        } else {
            Intent intent = new Intent(this, SendQuestion.class);
            startActivity(intent);
        }
        rfabHelper.toggleContent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_help, menu);
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
            init();
            int user_id = 12;
            Eevents = (EditText)findViewById(R.id.edit_message2);
            Edesc_event = (EditText)findViewById(R.id.edit_message3);
            Eshare_money = (EditText)findViewById(R.id.edit_message4);
            Eneed_peo = (EditText)findViewById(R.id.edit_message5);
            event = Eevents.getText().toString();
            desc_event = Edesc_event.getText().toString();
            share_money = Eshare_money.getText().toString();
            need_peo = Eneed_peo.getText().toString();
            if (!event.isEmpty() && !need_peo.isEmpty()) {
                double Djingdu = Double.valueOf(jingdu.toString());
                double Dweidu = Double.valueOf(weidu.toString());
                int Ishare_money = Integer.parseInt(share_money);
                int Ineed_peo = Integer.parseInt(need_peo);

                String jsonStrng = "{" +
                        "\"id\":" + user_id + ",\"type\":1," +
                        "\"title\":\"" + event + "\"," +
                        "\"content\":\"" + desc_event + "\"," +
                        "\"longitude\":" +  Djingdu + "," +
                        "\"latitude\":" + Dweidu + "," +
                        "\"love_coin\":" + Ishare_money + "," +
                        "\"demand_number\":" + Ineed_peo + " " + "}";
                Toast.makeText(getApplicationContext(), user_id + "," + event + ","
                                + desc_event + "," + Djingdu + ",",
                        Toast.LENGTH_SHORT).show();
                String message = RequestHandler.sendPostRequest(
                        "http://120.24.208.130:1501/event/add", jsonStrng);
                if (message == "false") {
                    Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                            Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    JSONObject jO = null;
                    try {
                        jO = new JSONObject(message);
                        if (jO.getInt("status") == 500) {
                            if (jO.getInt("value") == -2) {
                                Toast.makeText(getApplicationContext(), "爱心币不足",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "提交失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ActivityCollector.getInstance().exit();
                            Intent intent = new Intent(this, Home.class);
                            startActivity(intent);
//                            SendHelp.this.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

