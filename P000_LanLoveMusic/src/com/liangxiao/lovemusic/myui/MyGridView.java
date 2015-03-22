package com.liangxiao.lovemusic.myui;

import java.util.ArrayList;
import java.util.List;

import com.liangxiao.lovemusic.R;
import com.liangxiao.lovemusic.model.IWordButtonClickListener;
import com.liangxiao.lovemusic.model.WordButton;
import com.liangxiao.lovemusic.util.Util;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class MyGridView extends GridView {
	public final static int COUNTS_WORDS = 24;
	private ArrayList<WordButton> mArrayList = new ArrayList<WordButton>();
	private MyGridAdapter mAdapter;
	private Context mContext;
	private IWordButtonClickListener mWordButtonClickListener;
	private Animation mScaleAnimation;

	public MyGridView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		mAdapter = new MyGridAdapter();
		this.setAdapter(mAdapter);
	}

	public void updateData(ArrayList<WordButton> list) {
		mArrayList = list;
		// 重新设置数据源
		setAdapter(mAdapter);
	}

	class MyGridAdapter extends BaseAdapter {
		public int getCount() {
			return mArrayList.size();
		}

		public Object getItem(int pos) {
			return mArrayList.get(pos);
		}

		public long getItemId(int pos) {
			return pos;
		}

		public View getView(int pos, View v, ViewGroup p) {
			final WordButton holder;
			if (v == null) {
				v = Util.getView(mContext, R.layout.name_selector_item);
				holder = mArrayList.get(pos);
				// 加载动画
				mScaleAnimation = AnimationUtils.loadAnimation(mContext,
						R.anim.scale);
				// 设置动画的延迟时间
				mScaleAnimation.setStartOffset(pos * 100);
				holder.mIndex = pos;
				if (holder.mViewButton == null) {

					holder.mViewButton = (Button) v.findViewById(R.id.item_btn);
					holder.mViewButton
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View arg0) {
									mWordButtonClickListener
											.onWordButtonClick(holder);
								}
							});
				}
				v.setTag(holder);
			} else {
				holder = (WordButton) v.getTag();
			}
			holder.mViewButton.setText(holder.mWordString);
			// 播放动画
			v.startAnimation(mScaleAnimation);
			return v;
		}
	}

	/**
	 * 注册监听事件
	 */
	public void registOnWordButtonClick(IWordButtonClickListener listener) {
		mWordButtonClickListener = listener;

	}
}
