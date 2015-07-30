package com.ehelp.user.pinyin;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.ehelp.R;

public class SlipButton extends View implements OnTouchListener{

    private boolean NowChoose = true;//记录当前按钮是否打开,true为打开,flase为关闭
    private boolean OnSlip = false;//记录用户是否在滑动的变量
    private float DownX,NowX;//按下时的x,当前的x,
    private Rect Btn_On,Btn_Off;//打开和关闭状态下,游标的Rect  

    private boolean isChgLsnOn = false;
    private OnChangedListener ChgLsn;

    private Bitmap bg_on,bg_off,slip_btn;

    public SlipButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub  
        init();
    }
    public SlipButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub  
        init();
    }
    private void init(){//初始化  
        //载入图片资源  
        bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.split_on);
        bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.split_off);
        slip_btn = BitmapFactory.decodeResource(getResources(), R.drawable.split_button);
        //获得需要的Rect数据,绘制的矩形
        Btn_Off = new Rect(0,0,slip_btn.getWidth(),slip_btn.getHeight());
        Btn_On = new Rect(bg_on.getWidth()-slip_btn.getWidth(), 0,
                bg_on.getWidth(), slip_btn.getHeight());
        setOnTouchListener(this);//设置监听器
    }
    @Override
    protected void onDraw(Canvas canvas) {//重载绘图函数
        // TODO Auto-generated method stub  
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x;
        {
            if (NowX < (bg_off.getWidth() / 2))//滑动到前半段与后半段的背景不同,在此做判断
                canvas.drawBitmap(bg_on, matrix, paint);//画出关闭时的背景
            else
                canvas.drawBitmap(bg_off, matrix, paint);//画出打开时的背景
            //是否是在滑动状态,
            if (OnSlip) {
                //是否划出指定范围,不能让游标跑到外头
                if (NowX >= bg_off.getWidth())
                    //减去游标1/2的长度
                    x = bg_off.getWidth() - slip_btn.getWidth() / 2;
                else
                    x = NowX - slip_btn.getWidth() / 2;
                //非滑动状态
            } else {
                //根据现在的开关状态设置画游标的位置
                if (NowChoose)
                    x = Btn_On.left;
                else
                    x = Btn_Off.left;
            }
            if (x < 0)//对游标位置进行异常判断...
                x = 0;
            else if (x > bg_off.getWidth() - slip_btn.getWidth())
                x = bg_off.getWidth() - slip_btn.getWidth();
            canvas.drawBitmap(slip_btn, x, 0, paint);//画出游标.
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub  
        switch(event.getAction())//根据动作来执行代码  
        {
            case MotionEvent.ACTION_MOVE://滑动
                NowX = event.getX();
                break;
            case MotionEvent.ACTION_DOWN://按下
                if(event.getX()>bg_off.getWidth()||event.getY()>bg_off.getHeight())
                    return false;
                OnSlip = true;
                DownX = event.getX();
                NowX = DownX;
                break;
            case MotionEvent.ACTION_UP://松开
                OnSlip = false;
                boolean LastChoose = NowChoose;
                if(event.getX()>=(bg_off.getWidth()/2))
                    NowChoose = true;
                else
                    NowChoose = false;
                if(isChgLsnOn&&(LastChoose!=NowChoose))//如果设置了监听器,就调用其方法..
                    ChgLsn.OnChanged(NowChoose);
                break;
            default:
        }
        invalidate();//重画控件  
        return true;
    }
    //设置监听器,当状态修改的时候
    public void SetOnChangedListener(OnChangedListener l){
        isChgLsnOn = true;
        ChgLsn = l;
    }
}  