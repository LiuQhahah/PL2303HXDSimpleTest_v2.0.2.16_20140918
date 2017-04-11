package com.prolific.pl2303hxdsimpletest.Utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.prolific.driver.pl2303.PL2303Driver;

/**
 * 描述：配置信息 包括基本的静态常量信息和指令集 
 */
public class Configuration {
	//定义数据
	public static short pm25_in;
	public static short pm10_in;
	public static short wendu_in;
	public static short shidu_in;
	public static short co2_in;
	public static short voc_in;
	public static short pm25_out;
	public static short pm10_out;
	public static short wendu_out;
	public static short shidu_out;
	public static PL2303Driver mSerial;
	public static final String ACTION_USB_PERMISSION = "com.prolific.pl2303hxdsimpletest.USB_PERMISSION";
	public static final boolean SHOW_DEBUG = true;

	//public static boolean sw_fengshan=true;
	//public static boolean sw_jiashi=true;
//	public static boolean sw_fulizi=true;
	//public static boolean sw_qing=true;
	/**开启学习模式 flag为0 时 可以开启学习模式，学习结束后flag 置为1 （点击退出学习模式）**/
	public static int flag  = 0;

	/**判断是否保存设定的数据，保存后设置为真，回到Control界面时，
	 * 判断是否开启自动模式并且是否保存设定值，只有&&才进行自动模式操作*/
	public static boolean setting_flag =false;
	/**false  为关，->可以打开设备，开启后，flag为true 表明已开启，不能再次开启设备*/
	public static boolean flag_fulizi =false;
	public static boolean flag_fengshan =false;
	public static boolean flag_qingxiang = false;
	public static boolean flag_jiashi =false;

	/**真的状态，设置为自动模式，真的情况，点击了设置按钮，并且在点击手动模式下，把真的状态置为假，重新判断
	 * 否则会出现第一次点击手动模式，弹出提示框，这时点击进入设置界面，next 一直为真，当第二次点击手动转自动时，则不会出现提示框*/

	public static boolean next = false;
	public static int Control_pm25_in,Control_co2_in,Control_shidu_in,Control_voc_in;
	public static boolean shoudong = true; /**真->手动*/
	/**
	 * 控制界面下，开关按钮的监听，0/1
	 */


	public static String date;
	public static String time;

	/**
	 * 描述：获得系统当前时间
	 *
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getTime() {
		/**以时分的形式输出时间*/
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		Date curDate = new Date(System.currentTimeMillis());
		time = formatter.format(curDate);
		return time;
	}

	public static String getDate() {
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy年MM月dd日");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		date = formatter1.format(curDate);
		return date;
	}


	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}



}