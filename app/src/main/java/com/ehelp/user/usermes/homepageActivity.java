package com.ehelp.user.usermes;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ehelp.R;
import com.ehelp.entity.User;
import com.ehelp.map.sendhelp_map;
import com.ehelp.send.CountNum;
import com.ehelp.send.SendQuestion;
import com.ehelp.user.contactlist.ContactlistActivity;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@AILayout(R.layout.activity_homepage)
public class homepageActivity extends AIActionBarActivity implements AbsListView.OnScrollListener,
        RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener {
    //FAB
    @AIView(R.id.label_list_sample_rfal)
    private RapidFloatingActionLayout rfaLayout;
    @AIView(R.id.label_list_sample_rfab)
    private RapidFloatingActionButton rfaButton;
    private RapidFloatingActionHelper rfabHelper;

    //控件
    private ListView listView;
    private Toolbar toolbar;
    private TextView floatTitle;
    private ImageView headerBg;
    //测量值
    private float headerHeight;//顶部高度
    private float minHeaderHeight;//顶部最低高度，即Bar的高度
    private float floatTitleLeftMargin;//header标题文字左偏移量
    private float floatTitleSize;//header标题文字大小
    private float floatTitleSizeLarge;//header标题文字大小（大号）
    //变量
    private int user_id;
    private AlertDialog EditDialog = null;
    private User UserInfo = null;
    private int love_coin;
    private int score;
    private List<User> attention;
    private List<User> fans;
    private List<String> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fab();
        initMeasure();
        setUserInfo();
        setLovingbank();
        setAttentionRelation(0);
        setAttentionRelation(1);
        initView();
        initListViewHeader();
        initListView();
        initEvent();
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
//        showToastMessage("clicked label: " + position);
//        rfabHelper.toggleContent();
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

    private void initMeasure() {
        headerHeight = getResources().getDimension(R.dimen.header_height);
        minHeaderHeight = getResources().getDimension(R.dimen.abc_action_bar_default_height_material);
        floatTitleLeftMargin = getResources().getDimension(R.dimen.float_title_left_margin);
        floatTitleSize = getResources().getDimension(R.dimen.float_title_size);
        floatTitleSizeLarge = getResources().getDimension(R.dimen.float_title_size_large);

        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder().
                        detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().
                detectLeakedSqlLiteObjects().detectLeakedClosableObjects().
                penaltyLog().penaltyDeath().build());

        //获取本地的user_id通过其来获得用户信息
        SharedPreferences sharedPref = this.getSharedPreferences("user_id", Context.MODE_PRIVATE);
        user_id = sharedPref.getInt("user_id", -1);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_main);
        floatTitle = (TextView) findViewById(R.id.tv_main_title);
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListView() {
        if (UserInfo != null) {
            data.add(0, "用户名    " + UserInfo.getName());
            data.add(1, "手机号    " + UserInfo.getPhone());
            data.add(2, "爱心币    " + love_coin);
            data.add(3, "积  分    " + score);

            //性别
            int i = UserInfo.getGender();
            if (i == 1) {
                data.add(4, "性  别     男");
            } else {
                data.add(4, "性  别     女");
            }

            //职业
            int z = UserInfo.getOccupation();
            data.add(5, "职  业     " + getOccupation(z));

            data.add(6, "所在地    " + UserInfo.getLocation());
            data.add(7, "年  龄    " + UserInfo.getAge());
            data.add("111");
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, data);
        listView.setAdapter(adapter);

        //绑定监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
                change(index);
            }
        });
    }

    public String getOccupation(int index) {
        String occ = null;
        switch (index) {
            case 0:
                occ = getResources().getString(R.string.mes_job1);
                break;
            case 1:
                occ = getResources().getString(R.string.mes_job2);
                break;
            case 2:
                occ = getResources().getString(R.string.mes_job3);
                break;
            case 3:
                occ = getResources().getString(R.string.mes_job4);
                break;
            case 4:
                occ = getResources().getString(R.string.mes_job5);
                break;
            case 5:
                occ = getResources().getString(R.string.mes_job6);
                break;
        }
        return occ;
    }

    public void change(int index) {
        //修改用户名
        if (index == 1) {
            EditDialog = new AlertDialog.Builder(homepageActivity.this).create();
            EditDialog.show();
            EditDialog.getWindow().setContentView(R.layout.edit_user_name);
            //click on cancel点击取消
            EditDialog.getWindow().findViewById(R.id.edit_username5)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditDialog.dismiss();
                        }
                    });
            //click ensure点击确定
            EditDialog.getWindow().findViewById(R.id.edit_username6)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //修改用户资料里面的用户名
                            EditText Get_edittext = (EditText)EditDialog.getWindow()
                                    .findViewById(R.id.edit_username4);
                            //获取输入的字符串,通过user_id来修改信息
                            String emp = Get_edittext.getText().toString();
                            String jsonString = "{" +
                                    "\"id\":" + user_id + "," +
                                    "\"name\":\"" +  emp + "\" " + "}";
                            String message = RequestHandler.sendPostRequest(
                                    "http://120.24.208.130:1501/user/modify_information", jsonString);
                            //Username.setText(emp);
                            data.set(0, "用户名    " + emp);
                            Toast.makeText(getApplicationContext(), "用户名设置成功",
                                    Toast.LENGTH_SHORT).show();
                            EditDialog.dismiss();
                        }
                    });
            //使edittext能输入东西
            EditDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            EditDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_VISIBLE);
        } else
        if (index == 5) { //修改性别
            new AlertDialog.Builder(homepageActivity.this).setTitle("选择性别").setIcon(
                    android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                    new String[]{"女", "男"}, 0,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String jsonString = "{" +
                                    "\"id\":" + user_id + "," +
                                    "\"gender\":" + which + "}";
                            String message = RequestHandler.sendPostRequest(
                                    "http://120.24.208.130:1501/user/modify_information", jsonString);
                            if (which == 1) {
                                data.set(4, "性  别     男");
                                Toast.makeText(getApplicationContext(), "修改成功",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                data.set(4, "性  别     女");
                                Toast.makeText(getApplicationContext(), "修改成功",
                                        Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }).show();
        } else
        if (index == 6) { //职业
            new AlertDialog.Builder(homepageActivity.this).setTitle("选择性别").setIcon(
                    android.R.drawable.ic_dialog_info).setSingleChoiceItems(
                    new String[]{"学生", "教师", "工人", "警察", "消防员", "其他"}, 0,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String jsonString = "{" +
                                    "\"id\":" + user_id + "," +
                                    "\"occupation\":" + which + "}";
                            String message = RequestHandler.sendPostRequest(
                                    "http://120.24.208.130:1501/user/modify_information", jsonString);
                            data.set(5, "职  业     " + getOccupation(which));
                            dialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                    }).show();
        }else
        if (index == 7) { //所在地
            EditDialog = new AlertDialog.Builder(homepageActivity.this).create();
            EditDialog.show();
            EditDialog.getWindow().setContentView(R.layout.edit_user_location);
            //click on cancel点击取消
            EditDialog.getWindow().findViewById(R.id.edit_location5)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditDialog.dismiss();
                        }
                    });
            //click ensure点击确定
            EditDialog.getWindow().findViewById(R.id.edit_location6)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText Get_edittext = (EditText)EditDialog.getWindow()
                                    .findViewById(R.id.edit_location4);
                            //获取输入的字符串,通过user_id来修改信息
                            String emp = Get_edittext.getText().toString();
                            String jsonString = "{" +
                                    "\"id\":" + user_id + "," +
                                    "\"location\":\"" + emp + "\" " + "}";
                            String message = RequestHandler.sendPostRequest(
                                    "http://120.24.208.130:1501/user/modify_information", jsonString);
                            data.set(6, "所在地    " + emp);
                            Toast.makeText(getApplicationContext(), "所在地设置成功",
                                    Toast.LENGTH_SHORT).show();
                            EditDialog.dismiss();
                        }
                    });
            EditDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            EditDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_VISIBLE);
        }else
        if (index == 8) { //年龄
            EditDialog = new AlertDialog.Builder(homepageActivity.this).create();
            EditDialog.show();
            EditDialog.getWindow().setContentView(R.layout.edit_age);
            //click on cancel点击取消
            EditDialog.getWindow().findViewById(R.id.edit_age5)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditDialog.dismiss();
                        }
                    });
            //click ensure点击确定
            EditDialog.getWindow().findViewById(R.id.edit_age6)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText Get_edittext = (EditText) EditDialog.getWindow()
                                    .findViewById(R.id.edit_age4);
                            //获取输入的字符串,通过user_id来修改信息
                            int emp1 = Integer.parseInt( Get_edittext.getText().toString());
                            String jsonString = "{" +
                                    "\"id\":" + user_id + "," +
                                    "\"age\":" + emp1 + "}";
                            String message = RequestHandler.sendPostRequest(
                                    "http://120.24.208.130:1501/user/modify_information", jsonString);
                            data.set(7, "年  龄    " + emp1);
                            Toast.makeText(getApplicationContext(), "用户年龄修改成功",
                                    Toast.LENGTH_SHORT).show();
                            EditDialog.dismiss();
                        }
                    });
            EditDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            EditDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                    .SOFT_INPUT_STATE_VISIBLE);
        }
    }

    private void setUserInfo() {
        String jsonString = "{" +
                "\"id\":" + user_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/get_information", jsonString);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject j = new JSONObject(message);
                if (j.getInt("status") == 500){
                    Toast.makeText(getApplicationContext(), "用户未登陆",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new Gson();
                    UserInfo = gson.fromJson(message, User.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLovingbank() {
        String jsonString = "{" +
                "\"user_id\":" + user_id + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/lovingbank", jsonString);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject j = new JSONObject(message);
                if (j.getInt("status") == 500){
                    Toast.makeText(getApplicationContext(), "用户未登陆",
                            Toast.LENGTH_SHORT).show();
                } else {
                    love_coin = j.getInt("love_coin");
                    score = j.getInt("score");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAttentionRelation(int state) {
        String jsonString = "{" +
                "\"id\":" + user_id +
                ",\"operation\":2" +
                ",\"state\":" + state + "}";
        String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/user/relation_manage", jsonString);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        } else {
            try {
                JSONObject j = new JSONObject(message);
                if (j.getInt("status") == 500){
                    Toast.makeText(getApplicationContext(), "用户未登陆",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Gson gson = new Gson();
                    String st = j.getString("user_list");
                    if (state ==0) {
                        attention = gson.fromJson(st, new TypeToken<List<User>>(){}.getType());
                    } else {
                        fans = gson.fromJson(st, new TypeToken<List<User>>(){}.getType());}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initListViewHeader() {
        View headerContainer = LayoutInflater.from(this).inflate(R.layout.header, listView, false);
        headerBg = (ImageView) headerContainer.findViewById(R.id.img_header_bg);

        if (UserInfo != null) {
            setPortrait(headerContainer);
            setNickname(headerContainer);
            setAttention(headerContainer);
            setFans(headerContainer);
            setSignIn(headerContainer);
        }

        listView.addHeaderView(headerContainer);
    }

    /*
* 设置头像
* */
    public void setPortrait(View headerContainer) {
        ImageView portrait = (ImageView) headerContainer.findViewById
                (R.id.portrait111);
        String imageUrl = "http://120.24.208.130:1501/avatar/"
                + UserInfo.getId() + ".jpg";
        Bitmap bmp = returnBitMap(imageUrl);
        if (bmp == null) {
            imageUrl = "http://120.24.208.130:1501/avatar/touxiang.jpg";
            bmp = returnBitMap(imageUrl);
        }

        portrait.setImageBitmap(bmp);
        portrait.setScaleType(ImageView.ScaleType.FIT_XY);

        portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
                intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
                intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
                startActivityForResult(intent, 1);
            }
        });
    }

    //URL转Bitmap
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

    //处理选中图像
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.toString());
            ContentResolver cr = this.getContentResolver();

            //获取图像路径
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index =
                    actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor.getString(actual_image_column_index);

            //得到文件
            String[] sourceStrArray = img_path.split("/");
            sourceStrArray[sourceStrArray.length - 1] = user_id + ".jpg";
            String new_img_path = "";
            for (int i = 1; i < sourceStrArray.length; i++) {
                new_img_path += "/" + sourceStrArray[i];
            }
            File file = new File(img_path);
            if (!file.exists() || file.isDirectory()) {
                Toast.makeText(getApplicationContext(), "图片不存在",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            //修改文件名及上传
            if (file.renameTo(new File(new_img_path))) {
                file = new File(new_img_path);
                Upload(file);
            } else {
                Toast.makeText(getApplicationContext(), "上传失败",
                        Toast.LENGTH_SHORT).show();
            }
            file.renameTo(new File(img_path));
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ImageView imageView = (ImageView) findViewById(R.id.portrait111);
                /* 将Bitmap设定到ImageView */
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(),e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //上传头像
    public void Upload(File img){
        final File image = img;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response;
                String postURL = "http://120.24.208.130:1501/user/upload_avatar";

                response = RequestHandler.uploadFile(postURL, image);

                if (response == "false"){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "上传头像失败，请检查网络是否连接并重试",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    /*
    * 设置昵称
    * */
    public void setNickname(View headerContainer) {
        final TextView Nickname = (TextView) headerContainer.findViewById(R.id.nickname);
        Nickname.setText(UserInfo.getNickname());
        Nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditDialog =
                        new AlertDialog.Builder(homepageActivity.this).create();
                EditDialog.show();
                EditDialog.getWindow().setContentView(R.layout.edit_nickname);
                //click on cancel点击取消
                EditDialog.getWindow().findViewById(R.id.edit_nickname5)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EditDialog.dismiss();
                            }
                        });
                //click ensure点击确定
                EditDialog.getWindow().findViewById(R.id.edit_nickname6)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //修改用户资料里面的用户名
                                EditText Get_edittext = (EditText) EditDialog.getWindow()
                                        .findViewById(R.id.edit_nickname4);
                                //获取输入的字符串,通过user_id来修改信息
                                String emp = Get_edittext.getText().toString();
                                String jsonString = "{" +
                                        "\"id\":" + user_id + "," +
                                        "\"nickname\":\"" + emp + "\" " + "}";
                                String message = RequestHandler.sendPostRequest(
                                        "http://120.24.208.130:1501/user/modify_information", jsonString);
                                Nickname.setText(emp);
                                Toast.makeText(getApplicationContext(), "用户昵称修改成功",
                                        Toast.LENGTH_SHORT).show();
                                EditDialog.dismiss();
                            }
                        });
                //使edittext能输入
                EditDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                EditDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_VISIBLE);
            }
        });
    }

    /*
* 关注
* */
    public void setAttention(View headerContainer) {
        final TextView Eattention = (TextView) headerContainer.findViewById(R.id.attention);
        Eattention.setText("关注  " + attention.size());
        Eattention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(homepageActivity.this, ContactlistActivity.class);
                startActivity(intent);
            }
        });
    }

    /*
* 粉丝
* */
    public void setFans(View headerContainer) {
        final TextView Efans = (TextView) headerContainer.findViewById(R.id.fans);
        Efans.setText("粉丝  " + fans.size());
    }
    //跳转到粉丝列表页面
    public void click_on_fanslist(View view) {
        Intent intent = new Intent(this, FansListActivity.class);
        startActivity(intent);
    }

    /*
* 设置签到
* */
    public void setSignIn(View headerContainer) {
        final Button EsignIn = (Button) headerContainer.findViewById(R.id.signIn);
        String jsonStrng = "{" +
                "\"id\":" + user_id +
                ",\"operation\":" + 1 + "}";
        final String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/account/signin", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }   else {
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    Toast.makeText(getApplicationContext(),
                            "我也不知道为什么出错了，也许是你没登陆吧。。？",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (jO.getInt("type") == 0) {
                        EsignIn.setText("签到");
                        EsignIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                if (SignIn()) {
                                    EsignIn.setText("已签到");
                                }
                            }
                        });
                    } else {
                        EsignIn.setText("已签到");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
* 点击签到
* */
    public Boolean SignIn() {
        String jsonStrng = "{" +
                "\"id\":" + user_id +
                ",\"operation\":" + 0 + "}";
        final String message = RequestHandler.sendPostRequest(
                "http://120.24.208.130:1501/account/signin", jsonStrng);
        if (message == "false") {
            Toast.makeText(getApplicationContext(), "连接失败，请检查网络是否连接并重试",
                    Toast.LENGTH_SHORT).show();
        }   else {
            try {
                JSONObject jO = new JSONObject(message);
                if (jO.getInt("status") == 500) {
                    Toast.makeText(getApplicationContext(),
                            "我也不知道为什么点击签到会出错，也许是你没登陆吧。。？",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "签到成功",
                            Toast.LENGTH_SHORT).show();
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void initEvent() {
        listView.setOnScrollListener(this);
    }

    //个人主页的右上角按钮没有存在的必要
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_homepage, menu);
//        return true;
//    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //Y轴偏移量
        float scrollY = getScrollY(view);

        //变化率
        float headerBarOffsetY = headerHeight - minHeaderHeight;//Toolbar与header高度的差值
        float offset = 1 - Math.max((headerBarOffsetY - scrollY) / headerBarOffsetY, 0f);

        //Toolbar背景色透明度
        toolbar.setBackgroundColor(Color.argb((int) (offset * 255), 72, 118, 255));
        //header背景图Y轴偏移
        headerBg.setTranslationY(scrollY / 2);

        /*** 标题文字处理 ***/
        //标题文字缩放圆心（X轴）
        floatTitle.setPivotX(floatTitle.getLeft() + floatTitle.getPaddingLeft());
        //标题文字缩放比例
        float titleScale = floatTitleSize / floatTitleSizeLarge;
        //标题文字X轴偏移
        floatTitle.setTranslationX(floatTitleLeftMargin * offset);
        //标题文字Y轴偏移：（-缩放高度差 + 大文字与小文字高度差）/ 2 * 变化率 + Y轴滑动偏移
        floatTitle.setTranslationY(
                (-(floatTitle.getHeight() - minHeaderHeight) +//-缩放高度差
                        floatTitle.getHeight() * (1 - titleScale))//大文字与小文字高度差
                        / 2 * offset +
                        (headerHeight - floatTitle.getHeight()) * (1 - offset));//Y轴滑动偏移
        //标题文字X轴缩放
        floatTitle.setScaleX(1 - offset * (1 - titleScale));
        //标题文字Y轴缩放
        floatTitle.setScaleY(1 - offset * (1 - titleScale));

        //判断标题文字的显示
        if (scrollY > headerBarOffsetY) {
            toolbar.setTitle(getResources().getString(R.string.toolbar_title));
            floatTitle.setVisibility(View.GONE);
        } else {
            toolbar.setTitle("");
            //floatTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 得到ListView在Y轴上的偏移
     */
    public float getScrollY(AbsListView view) {
        View c = view.getChildAt(0);

        if (c == null)
            return 0;

        int firstVisiblePosition = view.getFirstVisiblePosition();
        int top = c.getTop();

        float headerHeight = 0;
        if (firstVisiblePosition >= 1)
            headerHeight = this.headerHeight;

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }
}