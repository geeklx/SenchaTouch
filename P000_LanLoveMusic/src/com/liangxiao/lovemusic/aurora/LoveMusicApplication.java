package com.liangxiao.lovemusic.aurora;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

public class LoveMusicApplication extends Application {
	private static final String TAG = "LoveMusic";

	@Override
	public void onCreate() {
		Log.i(TAG, "[LoveMusicApplication] onCreate");
		super.onCreate();
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		// 设置别名Alias
		JPushInterface.setAlias(this, "test1", new TagAliasCallback() {

			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				Log.i("Alias", "set alias result is " + arg0 + "");
			}
		});
		// 设置TAG
		Set<String> sets = new HashSet<String>();
		sets.add("lan1");
		sets.add("lan2");
		JPushInterface.setTags(getApplicationContext(), sets,
				new TagAliasCallback() {

					@Override
					public void gotResult(int arg0, String arg1,
							Set<String> arg2) {
						Log.i("TAG", "set tag result is " + arg0 + "");
					}
				});
	}
}
