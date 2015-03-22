package com.liangxiao.lovemusic.ui;

import com.liangxiao.lovemusic.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class AllPassView extends Activity {
	FrameLayout layout_bar_icon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_pass_layout);
		layout_bar_icon = (FrameLayout) findViewById(R.id.layout_bar_icon);
		layout_bar_icon.setVisibility(View.INVISIBLE);
	}
}
