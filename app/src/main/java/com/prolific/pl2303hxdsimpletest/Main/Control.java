package com.prolific.pl2303hxdsimpletest.Main;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolific.pl2303hxdsimpletest.Utils.Configuration;
import com.prolific.pl2303hxdsimpletest.Widget.DrawView;
import com.prolific.pl2303hxdsimpletest.Widget.SpringProgressView;

import java.util.Timer;
import java.util.TimerTask;

import edu.njupt_tongda.liu.R;
import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * 1.获取部分检测数值
 * 2.控制净化装备
 * 3.操作独立设备
 * 4.自动运行（检测数值与设定的数值相比较）
 */
public class Control extends Activity {

    private Button slipswitch_MSL_fengshan;
    private Button slipswitch_MSL_fulizi;
    private Button slipswitch_MSL_qingxiangji;
    private Button slipswitch_MSL_jiashi;
    private Button slipswitch_MSL_fengshan1;
    private Button slipswitch_MSL_fulizi1;
    private Button slipswitch_MSL_qingxiangji1;
    private Button slipswitch_MSL_jiashi1;
    private SpringProgressView progressView_pm25 = null;
    private SpringProgressView progressView_voc = null;
    private SpringProgressView progressView_co2 = null;
    private Button bt_duli_switch;
    private Button bt_duli_dingshi;
    private Button bt_duli_huafen;
    private Button bt_duli_zhineng_fengliang;
    private Button bt_duli_denglizi;
    private Button bt_duli_xuexi;
    private Button bt_next;
    private Button rb_celue;
    private AnimationDrawable animationDrawable_qingxiangji;
    private AnimationDrawable animationDrawable_jiashi;
    private short[] buf1 = new short[25];
    private Typeface typeFace;
    ButtonClickListener listener = new ButtonClickListener();
    ButtonClickListener_xijie listener1 = new ButtonClickListener_xijie();
    ButtonLongListener xuexi_listener = new ButtonLongListener();
    static byte[] rbuf = new byte[25];
    String TAG = "PL2303HXD_APLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);

        timer.schedule(clockTask, 20, 500);
        Configuration.mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, Configuration.ACTION_USB_PERMISSION);

        if (!Configuration.mSerial.PL2303USBFeatureSupported()) {
            Toast.makeText(this, "Control:No Support USB host API", Toast.LENGTH_SHORT).show();
            Configuration.mSerial = null;
        }

        thread.start();
        thread.setPriority(9);
        /**设置标题的字体*/
        TextView textView = (TextView) findViewById(R.id.tv_title_control);
        TextView textView_duli = (TextView) findViewById(R.id.tv_duli_title);

        /**typeFace = Typeface.createFromAsset(getAssets(), "fonts/huakang_shaonv_ziti.ttf");
         textView.setTypeface(typeFace);
         textView_duli.setTypeface(typeFace);
         */
        /**画出分隔线*/
        FrameLayout ll = (FrameLayout) findViewById(R.id.fl_control);
        ll.addView(new DrawView(this));
        /**开启线程，监听按钮点击事件*/


        flag.start();


    }

    Thread flag = new Thread(new Runnable() {
        @Override
        public void run() {

            if (Configuration.flag_fengshan) {
                /**风扇旋转*/
                Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                slipswitch_MSL_fengshan.startAnimation(operatingAnim);

                Log.i("Control", String.valueOf(Configuration.flag_fengshan));
            } else {
                slipswitch_MSL_fengshan.clearAnimation();

                Log.i("Control", String.valueOf(Configuration.flag_fengshan));
            }

            /**真的情况下（按钮已经点击过，把负离子从默认的假转化成真）旋转*/
            if (Configuration.flag_fulizi) {
                /**负离子旋转*/
                Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                slipswitch_MSL_fulizi.startAnimation(operatingAnim);
                Log.i("Control", String.valueOf(Configuration.flag_fulizi));

            } else {
                slipswitch_MSL_fulizi.clearAnimation();

            }

            if (Configuration.flag_qingxiang) {
                animationDrawable_qingxiangji.start();
                Log.i("Control", String.valueOf(Configuration.flag_qingxiang));
            } else {
                animationDrawable_qingxiangji.stop();
            }

            if (Configuration.flag_jiashi) {
                animationDrawable_jiashi.start();
                Configuration.flag_jiashi = false;
            } else {

                animationDrawable_jiashi.stop();
                Configuration.flag_jiashi = true;
            }
        }
    });


    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            /*** 根据检测的数值，决定是否开关前端*/


            /*** 按钮开关，使得点击文字与图标均可运行设备*/
            bt_next = (Button) findViewById(R.id.bt_next);
            bt_duli_switch = (Button) findViewById(R.id.bt_duli_switch);
            bt_duli_xuexi = (Button) findViewById(R.id.bt_xuexi);
            bt_duli_huafen = (Button) findViewById(R.id.bt_duli_huafen);
            bt_duli_denglizi = (Button) findViewById(R.id.bt_duli_denglizi);
            bt_duli_zhineng_fengliang = (Button) findViewById(R.id.bt_duli_zhineng_fengliang);
            bt_duli_dingshi = (Button) findViewById(R.id.bt_duli_dingshi);
            slipswitch_MSL_fengshan = (Button) findViewById(R.id.bt_fengshan);
            slipswitch_MSL_fulizi = (Button) findViewById(R.id.bt_fulizi);
            slipswitch_MSL_qingxiangji = (Button) findViewById(R.id.bt_qingxiangji);
            slipswitch_MSL_jiashi = (Button) findViewById(R.id.bt_jiashi);

            slipswitch_MSL_fulizi1 = (Button) findViewById(R.id.bt_fulizi1);
            slipswitch_MSL_qingxiangji1 = (Button) findViewById(R.id.bt_qingxiangji1);
            slipswitch_MSL_fengshan1 = (Button) findViewById(R.id.bt_fengshan1);
            slipswitch_MSL_jiashi1 = (Button) findViewById(R.id.bt_jiashi1);


            /**逐帧动画清香剂与加湿器*/
            animationDrawable_qingxiangji = (AnimationDrawable) slipswitch_MSL_qingxiangji.getBackground();
            animationDrawable_jiashi = (AnimationDrawable) slipswitch_MSL_jiashi.getBackground();

            /**自动还是手动*/
            rb_celue = (Button) findViewById(R.id.rb_shoudong);

            /**独立遥控器*/
            bt_duli_switch = (Button) findViewById(R.id.bt_duli_switch);


            rb_celue.setOnClickListener(listener);
            bt_next.setOnClickListener(listener);
            bt_duli_switch.setOnClickListener(listener);
            bt_duli_xuexi.setOnLongClickListener(xuexi_listener);
            bt_duli_xuexi.setOnClickListener(listener);


            slipswitch_MSL_fengshan.setOnClickListener(listener1);
            slipswitch_MSL_fulizi.setOnClickListener(listener1);
            slipswitch_MSL_qingxiangji.setOnClickListener(listener1);
            slipswitch_MSL_jiashi.setOnClickListener(listener1);

            slipswitch_MSL_fulizi1.setOnClickListener(listener1);
            slipswitch_MSL_qingxiangji1.setOnClickListener(listener1);
            slipswitch_MSL_fengshan1.setOnClickListener(listener1);
            slipswitch_MSL_jiashi1.setOnClickListener(listener1);
        }
    });

    @Override
    protected void onRestart() {
        super.onRestart();

        /*** 获取设定的值;*/
        try {
            timer.schedule(settingTask, 50, 1000);
            Log.i("choose_after", Setting.pm25);
            Log.i("choose_after", Setting.shidu);
            Log.i("choose_after", Setting.co2);
            Log.i("choose_after", Setting.voc);
        } catch (Exception e) {
            Log.e("Control", "cannot read data from Setting");
            e.printStackTrace();
        }
    }


    /*** 对自动手动进行监听* */
    private final class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_shoudong:
                    /**真->手动*/
                    if (Configuration.shoudong) {
                        if (Configuration.next) {
                            rb_celue.setBackgroundResource(R.drawable.moudle_auto);

                            slipswitch_MSL_fengshan.setEnabled(false);
                            slipswitch_MSL_qingxiangji.setEnabled(false);
                            slipswitch_MSL_fulizi.setEnabled(false);
                            slipswitch_MSL_jiashi.setEnabled(false);
                            slipswitch_MSL_fengshan1.setEnabled(false);
                            slipswitch_MSL_qingxiangji1.setEnabled(false);
                            slipswitch_MSL_fulizi1.setEnabled(false);
                            slipswitch_MSL_jiashi1.setEnabled(false);
                            //Configuration.next=false;
                            Configuration.shoudong = false;
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(Control.this)
                                    .setTitle("系统提示！")
                                    .setMessage("请设置自动模式参数，再开启自动模式呦");
                            setPositiveButton(builder);
                            setNegativeButton(builder).create().show();

                        }

                    } else {
                        rb_celue.setBackgroundResource(R.drawable.moudle_handle);

                        slipswitch_MSL_fengshan.setEnabled(true);
                        slipswitch_MSL_qingxiangji.setEnabled(true);
                        slipswitch_MSL_fulizi.setEnabled(true);
                        slipswitch_MSL_jiashi.setEnabled(true);
                        slipswitch_MSL_fengshan1.setEnabled(true);
                        slipswitch_MSL_qingxiangji1.setEnabled(true);
                        slipswitch_MSL_fulizi1.setEnabled(true);
                        slipswitch_MSL_jiashi1.setEnabled(true);


                        slipswitch_MSL_fengshan.setOnClickListener(listener1);
                        slipswitch_MSL_fulizi.setOnClickListener(listener1);
                        slipswitch_MSL_qingxiangji.setOnClickListener(listener1);
                        slipswitch_MSL_jiashi.setOnClickListener(listener1);

                        slipswitch_MSL_fengshan1.setOnClickListener(listener1);
                        slipswitch_MSL_fulizi1.setOnClickListener(listener1);
                        slipswitch_MSL_qingxiangji1.setOnClickListener(listener1);
                        slipswitch_MSL_jiashi1.setOnClickListener(listener1);

                        Configuration.shoudong = true;
                        Configuration.next = false;
                    }
                    break;
                case R.id.bt_duli_switch: //独立净化开启

                    write("M");
                    Log.i("duli", "独立净化器开");
                    bt_duli_switch.setBackgroundResource(R.drawable.duli);
                    bt_duli_denglizi.setEnabled(true);
                    bt_duli_huafen.setEnabled(true);
                    bt_duli_zhineng_fengliang.setEnabled(true);
                    bt_duli_dingshi.setEnabled(true);

                    bt_duli_denglizi.setOnClickListener(listener1);
                    bt_duli_huafen.setOnClickListener(listener1);
                    bt_duli_zhineng_fengliang.setOnClickListener(listener1);
                    bt_duli_dingshi.setOnClickListener(listener1);
                    break;
                case R.id.bt_next:
                    bt_next.setBackgroundResource(R.drawable.auto_setting_press);
                    Intent intent_next = new Intent();
                    intent_next.setClass(Control.this, Setting.class);
                    startActivity(intent_next);
                    break;


            }
        }
    }

    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
        //调用setPositiveButton 方法添加“确定”按钮
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(Control.this, Setting.class);
                startActivity(intent);

                Configuration.next = true;
            }
        });
    }

    private AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
        //调用setNegativeButton 方法添加“确定”按钮
        return builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Configuration.next = false;
            }
        });
    }

    /*** 在手动情况下，对加湿器、风扇、清新器、喷香剂监控*/
    private final class ButtonClickListener_xijie implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (!Configuration.isFastDoubleClick()) {
                switch (v.getId()) {

                    case R.id.bt_fengshan1:
                    case R.id.bt_fengshan:
                        /** 判断风扇当前状态，为假，说明风扇没有打开，此时发送“G”,打开风扇开启旋转*/
                        if (!Configuration.flag_fengshan) {
                            write("G");
                            /**动态显示*/
                            Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                            LinearInterpolator lin = new LinearInterpolator();
                            operatingAnim.setInterpolator(lin);
                            slipswitch_MSL_fengshan.startAnimation(operatingAnim);
                            Configuration.flag_fengshan = true;
                        } else {
                            write("H");
                            slipswitch_MSL_fengshan.clearAnimation();
                            Configuration.flag_fengshan = false;
                        }
                        break;
                    case R.id.bt_fulizi1:
                    case R.id.bt_fulizi:
                        if (!Configuration.flag_fulizi) {
                            write("E");
                            Configuration.flag_fulizi = true;
                            Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                            LinearInterpolator lin = new LinearInterpolator();
                            operatingAnim.setInterpolator(lin);
                            slipswitch_MSL_fulizi.startAnimation(operatingAnim);

                            /**Configuration.flag_fulizi = true;  代表开的状态*/
                        } else {
                            write("F");
                            slipswitch_MSL_fulizi.clearAnimation();
                            Configuration.flag_fulizi = false;
                        }
                        break;
                    case R.id.bt_qingxiangji1:
                    case R.id.bt_qingxiangji:
                        if (!Configuration.flag_qingxiang) {
                            write("A");
                            animationDrawable_qingxiangji.start();
                            Configuration.flag_qingxiang = true;
                        } else {
                            write("B");
                            animationDrawable_qingxiangji.stop();
                            Configuration.flag_qingxiang = false;
                        }
                        break;
                    case R.id.bt_jiashi1:
                    case R.id.bt_jiashi:
                        if (!Configuration.flag_jiashi) {
                            write("C");
                            Log.i("control", "加湿器开");
                            animationDrawable_jiashi.start();
                            Configuration.flag_jiashi = true;
                        } else {
                            write("D");
                            Log.i("control", "加湿器关");
                            animationDrawable_jiashi.stop();
                            Configuration.flag_jiashi = false;
                        }
                        break;

                    case R.id.bt_duli_huafen:
                        write("K");
                        Log.i("duli", "花粉开");
                        break;

                    case R.id.bt_duli_dingshi:
                        write("I");
                        Log.i("duli", "定时开");
                        break;

                    case R.id.bt_duli_zhineng_fengliang:
                        write("L");
                        Log.i("duli", "智能/风量");
                        break;

                    case R.id.bt_duli_denglizi:
                        write("J");
                        Log.i("duli", "等离子开");
                        break;
                }

            } else {
                Toast.makeText(Control.this, "不要点那么快嘛", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private final class ButtonLongListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            if (v == bt_duli_xuexi) {
                if (Configuration.flag == 0) {
                    write("N");
                    Log.i("duli", "学习模式开启");
                    Toast.makeText(Control.this, "进入学习模式", Toast.LENGTH_SHORT).show();
                    bt_duli_xuexi.setText("学习模式");
                    bt_duli_xuexi.setTextSize(12);
                    Configuration.flag = 1;
                } else if (Configuration.flag == 1) {
                    write("N");
                    Configuration.flag = 0;
                    Log.i("duli", "控制模式开启");
                    bt_duli_xuexi.setText("学习");
                    bt_duli_xuexi.setTextSize(15);
                }
            }
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setClass(Control.this, Test.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void write(String strWrite) {
        try {
            Configuration.mSerial.write(strWrite.getBytes(), strWrite.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onResume() {
        Log.d(TAG, "Enter onResume");
        super.onResume();
        String action = getIntent().getAction();
        Log.d(TAG, "onResume:" + action);

        try {


            if (!Configuration.mSerial.isConnected()) {
                if (Configuration.SHOW_DEBUG) {
                    Log.d(TAG, "New instance : " + Configuration.mSerial);
                }
                if (!Configuration.mSerial.enumerate()) {

                    Configuration.mSerial.InitByBaudRate(PL2303Driver.BaudRate.B115200, 700);

                    return;
                } else {
                    Log.d(TAG, "onResume:enumerate succeeded!");
                }
            }
            progressView_pm25.setCurrentCount(Configuration.pm25_in);
            progressView_voc.setCurrentCount(Configuration.voc_in);
            progressView_co2.setCurrentCount(Configuration.co2_in);


        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Leave onResume");
    }


    /*** 延时接受数据*/
    private Timer timer = new Timer();

    private TimerTask settingTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 2;
            handler.sendMessage(message);
        }
    };
    private TimerTask clockTask = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {


                        if (Configuration.mSerial.InitByBaudRate(PL2303Driver.BaudRate.B115200, 700)) {

                            int len;
                            StringBuffer sbHex = new StringBuffer();
                            len = Configuration.mSerial.read(rbuf);
                            if (len < 0) {
                                Log.d(TAG, "Fail to bulkTransfer(read data)");
                                return;
                            }

                            if (len > 0) {
//								if (Configuration.SHOW_DEBUG) {
//									Log.d(TAG, "read len : " + len);
//								}
//								for (int j = 0; j < len; j++) {
//									sbHex.append((char) (rbuf[j] & 0x000000FF));
//									Log.i(TAG, String.valueOf(rbuf[j] & 0x000000FF));
//									buf1[j]=(short)(rbuf[j] & 0x000000FF);
//								}
                                buf1[10] = (short) (rbuf[10] & 0x000000FF);
                                buf1[11] = (short) (rbuf[11] & 0x000000FF);

                                buf1[14] = (short) (rbuf[14] & 0x000000FF);
                                buf1[15] = (short) (rbuf[15] & 0x000000FF);
                                buf1[16] = (short) (rbuf[16] & 0x000000FF);
                                buf1[18] = (short) (rbuf[18] & 0x000000FF);
//								if (buf1[0] == 0x42 && buf1[1] == 0x4D && buf1[2] == 0x00 && buf1[3] == 0x0F) {
//									//if (sum == (short)(buf1[17] << 8 + buf1[18]))
                                {
                                    Configuration.Control_pm25_in = buf1[10];
                                    Configuration.Control_pm25_in = (short) (Configuration.Control_pm25_in << 8);
                                    Configuration.Control_pm25_in = (short) (Configuration.Control_pm25_in + buf1[11]);
                                    Configuration.Control_co2_in = buf1[14];
                                    Configuration.Control_co2_in = (short) (Configuration.Control_co2_in << 8);
                                    Configuration.Control_co2_in = (short) (Configuration.Control_co2_in + buf1[15]);
                                    Configuration.Control_voc_in = buf1[16];
                                    Configuration.Control_shidu_in = buf1[18];
                                    progressView_pm25 = (SpringProgressView) findViewById(R.id.spring_progress_view_pm25);
                                    progressView_pm25.setMaxCount(300.0f);
                                    progressView_voc = (SpringProgressView) findViewById(R.id.spring_progress_view_voc);
                                    progressView_voc.setMaxCount(50.0f);
                                    progressView_co2 = (SpringProgressView) findViewById(R.id.spring_progress_view_co2);
                                    progressView_co2.setMaxCount(2000.0f);
                                    progressView_pm25.setCurrentCount(Configuration.Control_pm25_in);
                                    progressView_voc.setCurrentCount(Configuration.Control_voc_in);
                                    progressView_co2.setCurrentCount(Configuration.Control_co2_in);
                                    //	}
                                }
                            }
                        } else {
                            progressView_pm25.setCurrentCount(Configuration.pm25_in);
                            progressView_voc.setCurrentCount(Configuration.voc_in);
                            progressView_co2.setCurrentCount(Configuration.co2_in);
                        }
                        //isConnected
                    } catch (Exception e) {
                        Log.e(TAG, "Test Read DatefromSerial Failed" + e.getMessage());
                    }

                    break;
                case 2:
                    /*** 当前值>设定——>开启净化***获取 设置里的数据*/
                    try {
                        /**setting_flag  为假，代表没有保存，如果要执行自动模式，必须先保存（设为真）
                         * !Configuration.shoudong  自动模式
                         *Configuration.setting_flag 已设置 */
                        if (!Configuration.shoudong && Configuration.setting_flag) {
                            if (Configuration.Control_pm25_in > Integer.valueOf(Setting.pm25)) {

                                /**负离子的状态，为假（未开），则开*
                                 * !Configuration.flag_fulizi没有开*/
                                if (!Configuration.flag_fulizi) {

                                    Thread.sleep(20);
                                    write("E");
                                    Configuration.flag_fulizi = true;
                                    Log.i("Control", "负离子开");

                                    slipswitch_MSL_fulizi.setEnabled(true);
                                    slipswitch_MSL_fulizi.setBackgroundResource(R.drawable.fulizi);
                                    Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    operatingAnim.setInterpolator(lin);
                                    slipswitch_MSL_fulizi.startAnimation(operatingAnim);
                                    slipswitch_MSL_fulizi.setEnabled(false);
                                }
                            } else {
                                if (Configuration.flag_fulizi) {
                                    write("F");
                                    Configuration.flag_fulizi = false;
                                    Log.i("Control", "负离子关");
                                    slipswitch_MSL_fulizi.setEnabled(true);
                                    slipswitch_MSL_fulizi.clearAnimation();
                                    slipswitch_MSL_fulizi.setEnabled(false);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Control", "Control cannot Campare date from two Activities" + e.getMessage());
                    }
                    /*** 判断设定值与当前值大小：* 检测风扇是否开启*当前值超出预期，kaifengs* 当前值低于设定的最小值，关风扇*/

                case 3:
                    try {
                        if (!Configuration.shoudong && Configuration.setting_flag) {
                            if (Configuration.Control_co2_in > Integer.valueOf(Setting.co2)) {


                                if (!Configuration.flag_fengshan) {

                                    write("G");


                                    Log.i("control", "排风扇开");
                                    slipswitch_MSL_fengshan.setEnabled(true);
                                    slipswitch_MSL_fengshan.setBackgroundResource(R.drawable.fengshan);
                                    /**风扇旋转*/
                                    Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    operatingAnim.setInterpolator(lin);
                                    slipswitch_MSL_fengshan.startAnimation(operatingAnim);
                                    slipswitch_MSL_fengshan.setEnabled(false);
                                    Configuration.flag_fengshan = true;
                                }
                            } else {
                                if (Configuration.flag_fengshan) {
                                    write("H");
                                    Log.i("control", "排风扇关");
                                    slipswitch_MSL_fengshan.setEnabled(true);
                                    slipswitch_MSL_fengshan.setBackgroundResource(R.drawable.fengshan);
                                    slipswitch_MSL_fengshan.clearAnimation();
                                    slipswitch_MSL_fengshan.setEnabled(false);
                                    Configuration.flag_fengshan = false;
                                }
                            }
                        } else {
//							Toast.makeText(Control.this,"不满足条件风扇不开",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("Control", "Control cannot Campare date from two Activities" + e.getMessage());
                    }
                case 4:
                    /*** 当前值超过预期，开启净化;当前值低于预期，关闭净化*/
                    try {
                        if (!Configuration.shoudong && Configuration.setting_flag) {
                            if (Configuration.Control_shidu_in > Integer.valueOf(Setting.shidu))

                            {
                                if (!Configuration.flag_fengshan) {
                                    wait(20);
                                    write("G");
                                    Log.i("control", "排风扇开");

                                    slipswitch_MSL_fengshan.setEnabled(true);
                                    slipswitch_MSL_fengshan.setBackgroundResource(R.drawable.fengshan);
                                    Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                                    /**风扇旋转*/
                                    LinearInterpolator lin = new LinearInterpolator();
                                    operatingAnim.setInterpolator(lin);
                                    slipswitch_MSL_fengshan.startAnimation(operatingAnim);
                                    slipswitch_MSL_fengshan.setEnabled(false);
                                    Configuration.flag_fengshan = true;
                                }
                            } else {
                                if (Configuration.flag_fengshan) {
                                    write("H");
                                    Log.i("control", "排风扇关");
                                    slipswitch_MSL_fengshan.setEnabled(true);
                                    slipswitch_MSL_fengshan.setBackgroundResource(R.drawable.fengshan);
                                    slipswitch_MSL_fengshan.clearAnimation();
                                    slipswitch_MSL_fengshan.setEnabled(false);
                                    Configuration.flag_fengshan = false;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Control", "Control cannot Campare date from two Activities" + e.getMessage());
                    }
                case 5:
                    try {
                        /**
                         * 自动模式并且点击Setting中的保存按钮才执行
                         */
                        if (!Configuration.shoudong && Configuration.setting_flag) {
                            if (Configuration.Control_voc_in > Integer.valueOf(Setting.voc)) {
                                if (!Configuration.flag_fulizi) {

                                    wait(2);
                                    write("E");
                                    Log.i("control", "负离子开");
                                    slipswitch_MSL_qingxiangji.setEnabled(true);
                                    Animation operatingAnim = AnimationUtils.loadAnimation(Control.this, R.anim.refresh);
                                    LinearInterpolator lin = new LinearInterpolator();
                                    operatingAnim.setInterpolator(lin);
                                    slipswitch_MSL_fulizi.startAnimation(operatingAnim);
                                    slipswitch_MSL_qingxiangji.setEnabled(false);
                                    Configuration.flag_fulizi = true;
                                }
                            } else {
                                if (Configuration.flag_fulizi) {
                                    write("F");
                                    Log.i("control", "负离子关");
                                    slipswitch_MSL_qingxiangji.setEnabled(true);
                                    slipswitch_MSL_fulizi.clearAnimation();
                                    slipswitch_MSL_qingxiangji.setEnabled(false);
                                    Configuration.flag_fulizi = false;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("Control", "Control cannot Campare date from two Activities" + e.getMessage());
                    }
            }

        }
    };

    private boolean openUsbSerial() {
        Log.d(TAG, "Enter  openUsbSerial");
        boolean conection = false;

        if (null == Configuration.mSerial) {
            Toast.makeText(this, "connected failed ", Toast.LENGTH_SHORT).show();
            conection = false;
        }
        if (Configuration.mSerial.isConnected()) {
            if (Configuration.SHOW_DEBUG) {
                Log.d(TAG, "openUsbSerial : isConnected ");
            }


            if (!Configuration.mSerial.InitByBaudRate(PL2303Driver.BaudRate.B115200, 700)) {
                if (!Configuration.mSerial.PL2303Device_IsHasPermission()) {
                    Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
                    conection = false;
                }

                if (Configuration.mSerial.PL2303Device_IsHasPermission() && (!Configuration.mSerial.PL2303Device_IsSupportChip())) {
                    Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
                    conection = false;
                }
            } else {
                //Toast.makeText(this, "connect successed", Toast.LENGTH_SHORT).show();

                conection = true;
            }
        }//isConnected

        Log.d(TAG, "Leave openUsbSerial");

        return conection;
    }//openUsbSerial
}