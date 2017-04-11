
package com.prolific.pl2303hxdsimpletest.Main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolific.pl2303hxdsimpletest.Utils.Configuration;
import com.prolific.pl2303hxdsimpletest.Widget.DrawView;

import java.util.Timer;
import java.util.TimerTask;

import edu.njupt_tongda.liu.R;
import tw.com.prolific.driver.pl2303.PL2303Driver;


public class Test extends Activity {

	private long nowtime;// 保存当前时间

	private TextView tv_pm25_out;
	private TextView tv_pm10_out;
	private TextView tv_wendu_out;
	private TextView tv_shidu_out;
	private TextView tv_pm25_in;
	private TextView tv_pm10_in;
	private TextView tv_voc;
	private TextView tv_co2;
	private TextView tv_wendu_in;
	private TextView tv_shidu_in;
	private TextView tv_air_outdoor;
	private Button bt_next;
	private TextView tv_kongqizhiliang;
	private  short[]  buf1 = new short[512];
	private static boolean flag_weather =true;
	private TextView tv_time_now;
	private TextView tv_date;
	private TextView tv_city_online;
	private TextView tv_weather_online;
	static int i= 0;
	private short sum = 0;
	String TAG = "PL2303HXD_APLog";
	static byte[] rbuf = new byte[30];
	double[][] a ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		/**500毫秒获取数据一次*/
		timer.schedule(task, 1, 500);

		tv_pm25_out = (TextView) findViewById(R.id.tv_pm25_out);
		tv_pm25_in = (TextView) findViewById(R.id.tv_pm25_in);
		tv_pm10_in = (TextView) findViewById(R.id.tv_pm10_in);
		tv_pm10_out = (TextView) findViewById(R.id.tv_pm10_out);
		tv_co2 = (TextView) findViewById(R.id.tv_co2);
		tv_wendu_out  = (TextView)findViewById(R.id.tv_wendu_out);
		tv_shidu_out = (TextView)findViewById(R.id.tv_shidu_out);
		tv_wendu_in = (TextView) findViewById(R.id.tv_wendu_in);
		tv_shidu_in = (TextView) findViewById(R.id.tv_shidu_in);
		tv_voc = (TextView) findViewById(R.id.tv_voc);
		tv_kongqizhiliang = (TextView)findViewById(R.id.tv_kongqizhiliang);
		tv_air_outdoor = (TextView) findViewById(R.id.tv_air_outdoor);
		/**标题字体*/
		TextView textView = (TextView) findViewById(R.id.tv_title_test);
		//Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/huakang_shaonv_ziti.ttf");
		//textView.setTypeface(typeFace);

		/**显示系统时间，显示天气预报的数据*/
		timer.schedule(clockTask, 1000, 1000*60);
		/**画间隔线*/
		FrameLayout ll = (FrameLayout)findViewById(R.id.test);
		ll.addView(new DrawView(this));
		FrameLayout fl_0 = (FrameLayout)findViewById(R.id.fl_0);
		fl_0.addView(new DrawView(this));

		/** get service*/
		Configuration.mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
				this, Configuration.ACTION_USB_PERMISSION);
		// check USB host function.
		if (!Configuration.mSerial.PL2303USBFeatureSupported()) {
			Toast.makeText(this, "Test:No Support USB host API", Toast.LENGTH_SHORT).show();
			Configuration.mSerial = null;
		}

		bt_next = (Button) findViewById(R.id.bt_next);
		ButtonListener buttonListener = new ButtonListener();
		bt_next.setOnClickListener(buttonListener);

		//openUsbSerial();

		a = new double[][]{{0,15.4, 0, 50}, {15.4,40.4,50,100},{40.4,65.4,100,150},{65.4,150.4,150,200},{150.4,250.4,200,300},
				{250.4,350.4,300,400},{350.4,500.4,400,500}};



	}

	private Timer timer = new Timer();
	/*** 时钟运行*/
	private TimerTask clockTask = new TimerTask(){
		@Override
		public void run() {
			Message message = new Message();
			message.what = 2;
			handler.sendMessage(message);
		}
	};
	private TimerTask task = new TimerTask() {
		public void run() {

			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

	private  Handler handler = new Handler()
	{
		public void handleMessage(Message msg) {

			switch (msg.what) {
				case 1:
					try {
				if (Configuration.mSerial.InitByBaudRate(PL2303Driver.BaudRate.B115200,700))
						{

							int len;
							StringBuffer sbHex=new StringBuffer();
							len = Configuration.mSerial.read(rbuf);
							if(len<0) {
								Log.d(TAG, "Fail to bulkTransfer(read data)");
								return;
							}

							if (len > 0) {
								if (Configuration.SHOW_DEBUG) {
									Log.d(TAG, "read len : " + len);
								}
								for (int j = 0; j < len; j++) {
									sbHex.append((char) (rbuf[j] & 0x000000FF));
									Log.i(TAG, String.valueOf(rbuf[j] & 0x000000FF));
									buf1[j]=(short)(rbuf[j] & 0x000000FF);
								}


							if (buf1[0] == 0x42 && buf1[1] == 0x4D && buf1[2] == 0x00 && buf1[3] == 0x0F) {
								for (int i = 0; i < len - 2; i++)
								{	sum += buf1[i];}
								Log.i("jieshou_sum", String.valueOf(sum));
								Log.i("jieshou_sum", String.valueOf(buf1[19] << 8 + buf1[20]));

								//if (sum == (short)(buf1[17] << 8 + buf1[18]))

								{


									//Log.i("jiehsou_jiaoyan", String.valueOf(buf1[17] << 8 + buf1[18]));
									Configuration.pm25_out = buf1[4];
									Configuration.pm25_out = (short) (Configuration.pm25_out << 8);
									Configuration.pm25_out += buf1[5];
									if (Configuration.pm25_out < 0) {
										Log.i("pm25_out", String.valueOf(Configuration.pm25_out + 65536));
										tv_pm25_out.setText(String.valueOf(Configuration.pm25_out + 65536));
										Log.i("pm25_out", String.valueOf(Configuration.pm25_out));
									} else {
										tv_pm25_out.setText(String.valueOf(Configuration.pm25_out));
									}

									Configuration.pm10_out = buf1[6];
									Configuration.pm10_out = (short) (Configuration.pm10_out << 8);
									Configuration.pm10_out = (short) (Configuration.pm10_out + buf1[7]);
									if (Configuration.pm10_out < 0) {
										tv_pm10_out.setText(String.valueOf(Configuration.pm10_out + 65536));
									} else {
										tv_pm10_out.setText(String.valueOf(Configuration.pm10_out));
									}

									Configuration.wendu_out = buf1[8];
									tv_wendu_out.setText(String.valueOf(Configuration.wendu_out));
									Configuration.shidu_out = buf1[9];
									tv_shidu_out.setText(String.valueOf(Configuration.shidu_out));

									Configuration.pm25_in = buf1[10];
									Configuration.pm25_in = (short) (Configuration.pm25_in << 8);
									Configuration.pm25_in = (short) (Configuration.pm25_in + buf1[11]);
									System.out.println(Configuration.pm25_in);
									if (Configuration.pm25_in < 0) {
										tv_pm25_in.setText(String.valueOf(Configuration.pm25_in + 65536));
									} else {
										tv_pm25_in.setText(String.valueOf(Configuration.pm25_in));
									}


									Configuration.pm10_in = buf1[12];
									Configuration.pm10_in = (short) (Configuration.pm10_in << 8);
									Configuration.pm10_in = (short) (Configuration.pm10_in + buf1[13]);
									if (Configuration.pm10_in < 0) {
										tv_pm10_in.setText(String.valueOf(Configuration.pm10_in + 65536));
									} else {
										tv_pm10_in.setText(String.valueOf(Configuration.pm10_in));
									}

									Configuration.co2_in = buf1[14];
									Configuration.co2_in = (short) (Configuration.co2_in << 8);
									Configuration.co2_in = (short) (Configuration.co2_in + buf1[15]);
									if (Configuration.co2_in < 0) {
										tv_co2.setText(String.valueOf(Configuration.co2_in + 65536));
									} else {
										tv_co2.setText(String.valueOf(Configuration.co2_in));
									}


									Configuration.voc_in = buf1[16];
									tv_voc.setText(String.valueOf(Configuration.voc_in));
									Configuration.wendu_in = (buf1[17]);
									tv_wendu_in.setText(String.valueOf(Configuration.wendu_in));
									Configuration.shidu_in = buf1[18];
									tv_shidu_in.setText(String.valueOf(Configuration.shidu_in));


									try {
										tv_kongqizhiliang.setText(Api(Configuration.pm25_in, 0) + zhiliang[zhiliang_flag]);
										tv_kongqizhiliang.setTextColor(zhiliang_color[zhiliang_flag]);
										tv_air_outdoor.setText(String.valueOf(Api(Configuration.pm25_out,1))+zhiliang[outdoor_flag]);
										tv_air_outdoor.setTextColor(zhiliang_color[outdoor_flag]);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								}
							}
						}//isConnected
					} catch (Exception e) {
						Log.e(TAG, "Test Read DatefromSerial Failed" + e.getMessage());
					}

						break;
				case 2:
					tv_time_now = (TextView) findViewById(R.id.tv_time_now);
					tv_date = (TextView) findViewById(R.id.tv_date);
					tv_city_online = (TextView) findViewById(R.id.tv_city_online);
					tv_weather_online = (TextView) findViewById(R.id.tv_weather_online);
					if(Lunch.wendu_line.equals("")&&flag_weather){
						flag_weather=false;
						Toast.makeText(Test.this,"网络不给力",Toast.LENGTH_SHORT).show();
					}
					tv_city_online.setText(Lunch.normalCity);
					tv_weather_online.setText(
							Lunch.alarmType_line    + Lunch.pm25_line+
									Lunch.wendu_line + Lunch.fengli_line );
					tv_time_now.setText(com.prolific.pl2303hxdsimpletest.Utils.Configuration.getTime());
					tv_date.setText(com.prolific.pl2303hxdsimpletest.Utils.Configuration.getDate());
					Log.i("time", com.prolific.pl2303hxdsimpletest.Utils.Configuration.getDate() +
							com.prolific.pl2303hxdsimpletest.Utils.Configuration.getDate());
					break;

			}
		}
	};


			static char zhiliang_flag=0;
			static char outdoor_flag=0;
			String zhiliang[] = {"（良好）","（中等）","（对敏感人群不健康）","（不健康）","（非常不健康）","（有毒害）",
					"（有毒害）","（爆表了）"};
			int zhiliang_color[]={0xff18c224,0xffe5f436,0xFFFF610F,0xfff41911,0xffad0d9c,0xff55052d,0xff2b061c,0xac09072c};

			private int Api (double api,int flag){
				if(api>500)
				{
					api=500;
					i=7;
				}
				else if(api<0){api=0;i=0;}
				else {
					for(i=0;i<7;i++)
					{
						if(api>a[i][0]&&api<a[i][1])break;
					}
					double api1 = (a[i][3]-a[i][2])/(a[i][1]-a[i][0]);
					api= api1*(api-a[i][0])+a[i][2];
				}

					if (flag ==0){
					zhiliang_flag=(char)i;
					}else if(flag == 1){
					outdoor_flag = (char)i;
					}

			return (int)api;

		}


	/**
	 * 按钮点击事件，值的传递、刷新按钮、下一页
	 */
	public class ButtonListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {

			bt_next.setBackgroundResource(R.drawable.control1);
			Intent intent = new Intent();
			intent.setClass(Test.this, Control.class);
			startActivity(intent);
			finish();
		}
	}





	/**返回当前界面时，显示之前的数据*/
	public void onResume() {
		Log.d(TAG, "Test :Enter onResume");
		super.onResume();
		String action =  getIntent().getAction();
		Log.d(TAG, "Test :onResume:" + action);

	try {
			if(!Configuration.mSerial.isConnected()) {
			if (Configuration.SHOW_DEBUG) {
//				Log.d(TAG, "Test :New instance : " + Configuration.mSerial);
			}

			if( !Configuration.mSerial.enumerate() ) {
				Configuration.mSerial.InitByBaudRate(PL2303Driver.BaudRate.B115200, 700);

				Toast.makeText(this, "Test :please link device", Toast.LENGTH_SHORT).show();
				return;
			} else {
				Log.d(TAG, "Test :onResume:enumerate succeeded!");
				Toast.makeText(this, "Test :attached", Toast.LENGTH_SHORT).show();
			}
		}//if isConnected


			tv_pm25_out.setText(String.valueOf(Configuration.pm25_out));
			tv_pm10_out.setText(String.valueOf(Configuration.pm10_out));
			tv_pm25_in.setText(String.valueOf(Configuration.pm25_in));
			tv_pm10_in.setText(String.valueOf(Configuration.pm10_in));
			tv_voc.setText(String.valueOf(Configuration.voc_in));
			tv_co2.setText(String.valueOf(Configuration.co2_in));
			tv_shidu_in.setText(String.valueOf(Configuration.shidu_in));
			tv_wendu_in.setText(String.valueOf(Configuration.wendu_in));
			tv_shidu_out.setText(String.valueOf(Configuration.shidu_out));
			tv_wendu_out.setText(String.valueOf(Configuration.wendu_out));


		} catch (Exception e) {
			e.printStackTrace();
		}

	}




	/**
	 * 连续按两次返回则退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - nowtime > 2000) {
				Log.i("nowtime", String.valueOf(nowtime));
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT)
						.show();
				nowtime = System.currentTimeMillis();
				return true;
			} else {
				int pid = android.os.Process.myPid(); //获得自己的pid
				android.os.Process.killProcess(pid);//通过pid自杀
				finish();
				System.exit(0);
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	private void openUsbSerial() {
		Log.d(TAG, "Enter  openUsbSerial");


		if(null==Configuration.mSerial) {
			Toast.makeText(this, "connected failed ", Toast.LENGTH_SHORT).show();
			return;
		}
		if (Configuration.mSerial.isConnected()) {
			if (Configuration.SHOW_DEBUG) {
				Log.d(TAG, "openUsbSerial : isConnected ");
			}


			if (!Configuration.mSerial.InitByBaudRate(PL2303Driver.BaudRate.B115200,700)) {
				if(!Configuration.mSerial.PL2303Device_IsHasPermission()) {
					Toast.makeText(this, "cannot open, maybe no permission", Toast.LENGTH_SHORT).show();
				}

				if(Configuration.mSerial.PL2303Device_IsHasPermission() && (!Configuration.mSerial.PL2303Device_IsSupportChip())) {
					Toast.makeText(this, "cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "connect successed", Toast.LENGTH_SHORT).show();
			}
		}//isConnected

		Log.d(TAG, "Leave openUsbSerial");
	}//openUsbSerial
}
