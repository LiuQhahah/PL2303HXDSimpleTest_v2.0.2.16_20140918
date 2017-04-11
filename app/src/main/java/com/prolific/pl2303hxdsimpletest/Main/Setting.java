package com.prolific.pl2303hxdsimpletest.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.prolific.pl2303hxdsimpletest.Utils.Configuration;
import com.prolific.pl2303hxdsimpletest.Widget.AbstractSpinerAdapter;
import com.prolific.pl2303hxdsimpletest.Widget.DrawView;
import com.prolific.pl2303hxdsimpletest.Widget.SpinerPopWindow;

import java.util.ArrayList;
import java.util.List;

import edu.njupt_tongda.liu.R;


/**
 * 
 * @author geniuseoe2012
 *  more brilliant��please pay attention to my csdn blog --> http://blog.csdn.net/geniuseoe2012 
 *  android  develop group ��298044305
 */
public class Setting extends Activity implements OnClickListener, AbstractSpinerAdapter.IOnItemSelectListener
{
    /** Called when the activity is first created. */
	

	private TextView mTView_pm25,mTView_co2,mTView_voc,mTView_shidu;
	private ImageButton mBtnDropDown_pm25 ,mBtnDropDown_co2,mBtnDropDown_voc ,mBtnDropDown_shidu;
	private List<String> nameList_pm25 = new ArrayList<String>();
	private List<String> nameList_co2 = new ArrayList<String>();
	private List<String> nameList_voc = new ArrayList<String>();
	private List<String> nameList_shidu = new ArrayList<String>();

	private int a;
	public static String pm25 = "10";
	public static String co2="350";
	public static String voc = "10";
	public static String shidu="20";

	private boolean flag_pm25 = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

		/**标题字体*/
		TextView textView = (TextView) findViewById(R.id.title);
		//Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/huakang_shaonv_ziti.ttf");
		//textView.setTypeface(typeFace);
		/**画线*/
		FrameLayout ll = (FrameLayout)findViewById(R.id.fl_setting);
		ll.addView(new DrawView(this));


		Thread setting =new Thread(new Runnable() {
			@Override
			public void run() {
				setupViews_co2();
				setupViews_pm();
				setupViews_voc();
				setupViews_shidu();

			}
		});

		setting.start();
		try {
			setting.join(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Button bt_save =(Button)findViewById(R.id.bt_save);
		bt_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				Toast.makeText(Setting.this, "保存成功", Toast.LENGTH_SHORT).show();
				Configuration.next=true;
				Configuration.setting_flag = true;
				intent.setClass(Setting.this, Control.class);
				startActivity(intent);
			}
		});


		mTView_pm25 = (TextView) findViewById(R.id.tv_value_pm);

    }

	@Override
	protected void onResume() {
		super.onResume();
		mTView_pm25.setText(pm25);
		mTView_voc.setText(voc);
		mTView_co2.setText(co2);
		mTView_shidu.setText(shidu);
	}

	private void setupViews_pm()
    {


		mBtnDropDown_pm25 = (ImageButton) findViewById(R.id.bt_dropdown_pm);
		mBtnDropDown_pm25.setOnClickListener(this);
		String[] names = getResources().getStringArray(R.array.pm25);
		for(int i = 0; i < names.length; i++)
		{
			nameList_pm25.add(names[i]);
		}
		mSpinerPopWindow_pm25 = new SpinerPopWindow(this);
		mSpinerPopWindow_pm25.refreshData(nameList_pm25, 0);
		mSpinerPopWindow_pm25.setItemListener(this);

    }

	private void setpm(int pos){
		if (pos >= 0 && pos <= nameList_pm25.size()){
			pm25 = nameList_pm25.get(pos);

				mTView_pm25.setText(pm25);

		//	Toast.makeText(this, pm25, Toast.LENGTH_SHORT).show();
		}
	}
	private SpinerPopWindow mSpinerPopWindow_pm25;
	private void showSpinWindow(){

		mSpinerPopWindow_pm25.setWidth(mTView_pm25.getWidth());
		mSpinerPopWindow_pm25.showAsDropDown(mTView_pm25);
	}



    private void setupViews_co2()
    {

    	mTView_co2 = (TextView) findViewById(R.id.tv_value_co2);

		mBtnDropDown_co2 = (ImageButton) findViewById(R.id.bt_dropdown_co2);
		mBtnDropDown_co2.setOnClickListener(this);
		String[] names1 = getResources().getStringArray(R.array.co2);
		for(int i = 0; i < names1.length; i++)
		{
			nameList_co2.add(names1[i]);
		}
		mSpinerPopWindow_co2 = new SpinerPopWindow(this);
		mSpinerPopWindow_co2.refreshData(nameList_co2, 0);

		mSpinerPopWindow_co2.setItemListener(this);
		
    }
    
    private void setupViews_voc()
    {

    	mTView_voc = (TextView) findViewById(R.id.tv_value_voc);
		mBtnDropDown_voc = (ImageButton) findViewById(R.id.bt_dropdown_voc);
		mBtnDropDown_voc.setOnClickListener(this);
		String[] names1 = getResources().getStringArray(R.array.voc);
		for(int i = 0; i < names1.length; i++)
		{
			nameList_voc.add(names1[i]);
		}
		mSpinerPopWindow_voc = new SpinerPopWindow(this);
		mSpinerPopWindow_voc.refreshData(nameList_voc, 0);
		mSpinerPopWindow_voc.setItemListener(this);
		
    }
    
    private void setupViews_shidu()
    {

    	mTView_shidu = (TextView) findViewById(R.id.tv_value_shidu);
		mBtnDropDown_shidu = (ImageButton) findViewById(R.id.bt_dropdown_shidu);
		mBtnDropDown_shidu.setOnClickListener(this);
		String[] names1 = getResources().getStringArray(R.array.shidu);
		for(int i = 0; i < names1.length; i++)
		{
			nameList_shidu.add(names1[i]);
		}
		mSpinerPopWindow_shidu = new SpinerPopWindow(this);
		mSpinerPopWindow_shidu.refreshData(nameList_shidu, 0);
		mSpinerPopWindow_shidu.setItemListener(this);
		
    }

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.bt_dropdown_pm:
			showSpinWindow();
			flag_pm25=true;
			Log.i("setting","点击");
			a=0;
			break;
		case R.id.bt_dropdown_co2:
			showSpinWindow1();
			a=1;
			break;
		case R.id.bt_dropdown_voc:
			showSpinWindow2();
			a=2;
			break;
		case R.id.bt_dropdown_shidu:
			showSpinWindow3();
			a=3;
			break;
		}
	}
	



	private void setVOC(int pos){
		if (pos>= 0 && pos <= nameList_voc.size()){
			voc = nameList_voc.get(pos);
			mTView_voc.setText(voc);
			//Toast.makeText(this, voc, Toast.LENGTH_SHORT).show();
		}
	}
	private void setCO2(int pos){
		if (pos>= 0 && pos <= nameList_co2.size()){
			co2 = nameList_co2.get(pos);
			mTView_co2.setText(co2);
			co2=co2.substring(0,3);
			//Toast.makeText(this, co2.substring(0, 3), Toast.LENGTH_SHORT).show();
		}
	}
	private void setshidu(int pos){
		if (pos>= 0 && pos <= nameList_shidu.size()){
			shidu = nameList_shidu.get(pos);
			mTView_shidu.setText(shidu);
			shidu=shidu.substring(0,2);
			//Toast.makeText(this, shidu.substring(0, 2), Toast.LENGTH_SHORT).show();
		}
	}
	

	
	private SpinerPopWindow mSpinerPopWindow_co2;
	private void showSpinWindow1(){

		mSpinerPopWindow_co2.setWidth(mTView_co2.getWidth());
		mSpinerPopWindow_co2.showAsDropDown(mTView_co2);
	}
	
	private SpinerPopWindow mSpinerPopWindow_voc;
	private void showSpinWindow2(){

		mSpinerPopWindow_voc.setWidth(mTView_voc.getWidth());
		mSpinerPopWindow_voc.showAsDropDown(mTView_voc);

	}

	private SpinerPopWindow mSpinerPopWindow_shidu;
	private void showSpinWindow3(){

		mSpinerPopWindow_shidu.setWidth(mTView_shidu.getWidth());
		mSpinerPopWindow_shidu.showAsDropDown(mTView_shidu);
	}
	



	@Override
	public void onItemClick(int pos) {
		if (a==0)

			if (flag_pm25) {
				setpm(pos);
			}
		if (a==1)
			setCO2(pos);
		if (a==2)
			setVOC(pos);
		if (a==3)
			setshidu(pos);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

//		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("系统提示！").
//				setMessage("是否保存设定的阀值?");
//
//		setPositiveButton(builder);
//		setNegativeButton(builder);
//
//		AlertDialog al =builder.create();
//		al.show();
//		return super.onKeyDown(keyCode, event);
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				// 设置对话框标题
				.setTitle("系统提示！")
						// 设置图标

				.setMessage("是否保存参数信息?");
		// 为AlertDialog.Builder添加“确定”按钮
		setPositiveButton(builder);
		// 为AlertDialog.Builder添加“取消”按钮
		setNegativeButton(builder)
				.create()
				.show();
		return super.onKeyDown(keyCode, event);
	}



	private AlertDialog.Builder setPositiveButton(
			AlertDialog.Builder builder)
	{
		// 调用setPositiveButton方法添加“确定”按钮
		return builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Configuration.setting_flag = true;
				Intent in = new Intent();
				in.setClass(Setting.this,Control.class);
				startActivity(in);
			}
		});

	}
	private AlertDialog.Builder setNegativeButton(
			AlertDialog.Builder builder)
	{
		// 调用setNegativeButton方法添加“取消”按钮
		return builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Configuration.setting_flag = false;
				Intent in = new Intent();
				in.setClass(Setting.this,Control.class);
				startActivity(in);
			}
		});
	}
}