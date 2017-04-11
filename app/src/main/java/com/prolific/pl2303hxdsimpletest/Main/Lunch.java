package com.prolific.pl2303hxdsimpletest.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.prolific.pl2303hxdsimpletest.Utils.MyHandler;
import com.prolific.pl2303hxdsimpletest.Widget.GradientTextView;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import edu.njupt_tongda.liu.R;

/**
 * Created by asus on 2015/7/20.
 */
public class Lunch extends Activity {

    public static String city_line ="北京";
    public static String wendu_line ="";
    public static String shidu_line ="";
    public static String fengli_line ="";
    public static String pm25_line ="";
    public static String quality_line="" ;
    public static String alarmType_line="" ;
    private int GO_MAIN=1000;
    private static boolean flag_weather =true;
    public static String normalCity = "";


    private  LocationManagerProxy aMapManager;
    private Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lunch);

        aMapManager = LocationManagerProxy.getInstance(this);
        /**设置标题字体*/
        GradientTextView textView = (GradientTextView) findViewById(R.id.tv_loading);
       // Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/huakang_shaonv_ziti.ttf");
        //textView.setTypeface(typeFace);

        /**开启初始化子线程*/
        LunchThread lunchThread = new LunchThread();
        lunchThread.start();
        try {
            lunchThread.join(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lunchThread.interrupt();



  }

    public class LunchThread extends Thread {
        @Override
        public void run() {
            super.run();
            /**定位，获取天气预报数据*/
            aMapManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, mAMapLocationListener);
            /**进入主界面*/
            mHandler.sendEmptyMessageDelayed(GO_MAIN, 2000);
        }
    }
    protected void NetworkOperation()  {

         t = new Thread() {

            public void run() {
                    if(flag_weather) {
                        Log.i("www", ("http://wthrcdn.etouch.cn/WeatherApi?citykey=" + city(normalCity)));
                        Log.i("www", String.valueOf(city(normalCity)));
                        getRss("http://wthrcdn.etouch.cn/WeatherApi?citykey=" + city(normalCity));
                    }
                flag_weather=false;
            }
        };
        t.start();
      t.interrupt();


    }


    /*** 获取天气预报数据* @param path*/
    private void getRss(String path) {

        URL url;

        try {
            url = new URL(path);
            Log.i("yao", "open path");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            Log.i("yao", "set SAXParseFactory");

            XMLReader xr = sp.getXMLReader();
            Log.i("yao", "set XMLReader");
            MyHandler myExampleHandler = new MyHandler();
            xr.setContentHandler(myExampleHandler);
            xr.parse(new InputSource(url.openStream()));
            Log.i("yao", "jiexi");
            city_line = myExampleHandler.getCityData();
            Log.i("city", city_line);
            wendu_line =  "   温度: "+myExampleHandler.getWenduData()+"℃";
            Log.i("wendu", wendu_line);
            shidu_line = myExampleHandler.getShiduData();
            Log.i("shidu", shidu_line);
            fengli_line = "   风力: "+myExampleHandler.getFengliDate();
            Log.i("fengli", fengli_line);
            pm25_line = "   PM2.5: "+myExampleHandler.getPm25Data();
            Log.i("pm25", pm25_line);
            quality_line = myExampleHandler.getQualityData();
            Log.i("quality", quality_line);
            alarmType_line = myExampleHandler.getTypeData() ;
            Log.i("alarmType", alarmType_line);

        }catch (Exception e){
            Log.e("error", ">>解析天气数据失败" + e.getMessage());
        }
    }

    /*** 进入检测界面*/

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            Intent intent=new Intent();
            intent.setClass(Lunch.this, Test.class);

            startActivity(intent);
            finish();
            super.handleMessage(msg);
        }
    };


    public AMapLocationListener mAMapLocationListener = new AMapLocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(android.location.Location location) {

        }

        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location != null) {
                normalCity=location.getCity();
                NetworkOperation();
            }else {
                Toast.makeText(Lunch.this, "定位失败，请检查网络", Toast.LENGTH_SHORT).show();
            }
        }
    };


    public static int city(String c)

    {
        int a=0;
        if(c.equals("北京市"))
            a= 101010100;
        /**
         * 江苏
         */
        else if(c.equals("南京市"))
            a= 101190101;
        else if(c.equals("扬州市"))
            a=101191001;
        else if(c.equals("无锡市"))
            a= 101190204;
        else if(c.equals("镇江市"))
            a=101190301;
        else if(c.equals("苏州市"))
            a= 101190401;
        else if(c.equals("南通市"))
            a=101190501;
        else if(c.equals("盐城市"))
            a= 101190701;
        else if(c.equals("徐州市"))
            a=101190801;
        else if(c.equals("淮安市"))
            a= 101190901;
        else if(c.equals("连云港市"))
            a=101191001;
        else if(c.equals("常州市"))
            a= 101191101;
        else if(c.equals("泰州市"))
            a=101191201;
        else if(c.equals("宿迁市"))
            a= 101191301;

        /**
         * 浙江
         */
        else if(c.equals("杭州市"))
            a=101210101;
        else if(c.equals("湖州市"))
            a= 101210201;
        else if(c.equals("嘉兴市"))
            a=101210301;
        else if(c.equals("宁波市"))
            a=101210401;
        else if(c.equals("绍兴市"))
            a= 101210501;
        else if(c.equals("台州市"))
            a=101210601;
        else if(c.equals("温州市"))
            a=101210701;
        else if(c.equals("丽水市"))
            a= 101210801;
        else if(c.equals("金华市"))
            a=101210901;
        else if(c.equals("衢州市"))
            a=101211001;
        else if(c.equals("舟山市"))
            a= 101211101;
        /**
         * 安徽
         */
        else if(c.equals("合肥市"))
            a=101220101;
        else if(c.equals("蚌埠市"))
            a=101220201;
        else if(c.equals("芜湖市"))
            a= 101220301;
        else if(c.equals("淮南市"))
            a=101220401;
        else if(c.equals("马鞍山市"))
            a=101220501;
        else if(c.equals("安庆市"))
            a= 101220601;
        else if(c.equals("宿州市"))
            a=101220701;
        else if(c.equals("阜阳市"))
            a=101220801;
        else if(c.equals("亳州市"))
            a= 101220901;
        else if(c.equals("黄山市"))
            a=101221001;
        else if(c.equals("衢州市"))
            a=101211001;
        else if(c.equals("滁州市"))
            a= 101221101;
        else if(c.equals("淮北市"))
            a=101221201;
        else if(c.equals("铜陵市"))
            a=101221301;
        else if(c.equals("宣城市"))
            a= 101221401;
        else if(c.equals("六安市"))
            a=101221501;
        else if(c.equals("巢湖市"))
            a=101221601;
        else if(c.equals("池州市"))
            a= 101221701;

        /**
         * 福建
         */
        else if(c.equals("福州市"))
            a= 101230101;
        else if(c.equals("厦门市"))
            a=101230201;
        else if(c.equals("宁德市"))
            a= 101230301;
        else if(c.equals("莆田市"))
            a=101230401;
        else if(c.equals("泉州市"))
            a= 101230501;
        else if(c.equals("漳州市"))
            a=101230601;
        else if(c.equals("龙岩市"))
            a= 101230701;
        else if(c.equals("三明市"))
            a=101230801;
        else if(c.equals("南平市"))
            a= 101230901;
        else if(c.equals("钓鱼岛"))
            a=101231001;

        /**
         * 江西
         */
        else if(c.equals("南昌市"))
            a= 101240101;
        else if(c.equals("九江市"))
            a=101240201;
        else if(c.equals("上饶市"))
            a= 101240301;
        else if(c.equals("抚州市"))
            a=101240401;
        else if(c.equals("宜春市"))
            a= 101240501;
        else if(c.equals("吉安市"))
            a=101240601;
        else if(c.equals("赣州市"))
            a= 101240701;
        else if(c.equals("景德镇市"))
            a=101240801;
        else if(c.equals("萍乡市"))
            a= 101240901;
        else if(c.equals("新余市"))
            a=101241001;
        else if(c.equals("鹰潭市"))
            a=101241101;
        return a;
    }
}

