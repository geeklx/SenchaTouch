package com.ctone;

import java.io.IOException;

import org.apache.cordova.*;

import com.ctone.R;



import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

public class MainActivity extends DroidGap {

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	UpdateManager manager = new UpdateManager(MainActivity.this);
 		try {
			manager.checkUpdate();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        super.onCreate(savedInstanceState);
        super.setIntegerProperty("splashscreen", R.drawable.splash);
       
        super.loadUrl("file:///android_asset/www/index.html",3000);
    }
    public boolean checkNetWorkStatus() {
		boolean result;
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo != null && netinfo.isConnected()) { // 当前网络可用
			result = true;
		} else { // 不可用
			new AlertDialog.Builder(MainActivity.this).setMessage(
					"检查到没有可用的网络连接,请打开网络连接").setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							ComponentName cn = new ComponentName(
									"com.android.settings",
									"com.android.settings.Settings");
							Intent intent = new Intent();
							intent.setComponent(cn);
							intent.setAction("android.intent.action.VIEW");
							startActivity(intent);
							finish();
						}
					}).show();
			result = false;
		}
		return result;
	}



	@Override
	protected void onPause() {
		super.onPause();
		 //关闭对话框  	     
	}
	
	 

}