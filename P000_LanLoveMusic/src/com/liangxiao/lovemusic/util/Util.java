package com.liangxiao.lovemusic.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import com.liangxiao.lovemusic.R;
import com.liangxiao.lovemusic.data.Const;
import com.liangxiao.lovemusic.model.IAlertDialogButtonListener;
import com.liangxiao.lovemusic.model.Song;
import com.liangxiao.lovemusic.myui.MyGridView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class Util {
	private static AlertDialog mAlertDialog;

	// 加载gridview中的item的xml方法
	public static View getView(Context context, int layoutId) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(layoutId, null);
		return layout;
	}

	/**
	 * 界面跳转
	 * 
	 * @param context
	 * @param desti
	 */
	public static void startActivity(Context context, Class desti) {
		Intent intent = new Intent();
		intent.setClass(context, desti);
		context.startActivity(intent);

		// 关闭当前的Activity
		((Activity) context).finish();
	}

	/**
	 * 显示自定义对话框
	 * 
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog(final Context context, String message,
			final IAlertDialogButtonListener listener) {
		View dialogView = null;
		AlertDialog.Builder builder = new Builder(context,
				R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view);
		ImageButton btn_ok = (ImageButton) dialogView.findViewById(R.id.btn_ok);
		ImageButton btn_cancel = (ImageButton) dialogView
				.findViewById(R.id.btn_cancel);
		TextView txt_dailog_message = (TextView) dialogView
				.findViewById(R.id.txt_dailog_message);
		txt_dailog_message.setText(message);
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 关闭dialog
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 事件回调
				if (listener != null) {
					listener.onClick();
				}

				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_ENTER);
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 关闭dialog
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_CANCEL);
			}
		});
		// 为dialog设置View
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		mAlertDialog.show();
	}

	/**
	 * 显示自定义对话框 share
	 * 
	 * @param context
	 * @param message
	 * @param listener
	 */
	public static void showDialog_share(final Context context, String message,
			final IAlertDialogButtonListener listener) {
		View dialogView = null;
		AlertDialog.Builder builder = new Builder(context,
				R.style.Theme_Transparent);
		dialogView = getView(context, R.layout.dialog_view_share);
		ImageButton btn_ok = (ImageButton) dialogView.findViewById(R.id.btn_ok);
		ImageButton btn_cancel = (ImageButton) dialogView
				.findViewById(R.id.btn_cancel);
		EditText txt_dailog_message = (EditText) dialogView
				.findViewById(R.id.txt_dailog_message);
		txt_dailog_message.setText(message);
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 关闭dialog
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 事件回调
				if (listener != null) {
					listener.onClick();
				}

				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_ENTER);
			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 关闭dialog
				if (mAlertDialog != null) {
					mAlertDialog.cancel();
				}
				// 播放音效
				MyPlayer.playTone(context, MyPlayer.INDEX_STONE_CANCEL);
			}
		});
		// 为dialog设置View
		builder.setView(dialogView);
		mAlertDialog = builder.create();
		mAlertDialog.show();
	}

	/**
	 * 游戏数据的save写入
	 * 
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static void saveData(Context context, int stageIndex, int coins) {
		FileOutputStream fis = null;

		try {
			fis = context.openFileOutput(Const.FILE_NAME_SAVE_DATA,
					Context.MODE_PRIVATE);
			DataOutputStream dos = new DataOutputStream(fis);
			dos.writeInt(stageIndex);
			dos.writeInt(coins);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 游戏数据的读取
	 * 
	 * @param context
	 * @param stageIndex
	 * @param coins
	 */
	public static int[] ReadData(Context context) {
		FileInputStream fis = null;
		int[] datas = { -1, Const.TOTAL_COINS };
		try {
			fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);
			DataInputStream dis = new DataInputStream(fis);

			datas[Const.INDEX_READ_DATA_STAGE] = dis.readInt();
			datas[Const.INDEX_READ_DATA_COINS] = dis.readInt();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return datas;

	}

	/**
	 * 随机生成汉字部分
	 */
	public static char getRandomChar() {
		String str = "";
		int hightPos;
		int lowPos;

		Random random = new Random();

		hightPos = (176 + Math.abs(random.nextInt(39)));// 176+01~87
		lowPos = (161 + Math.abs(random.nextInt(93)));//

		byte[] b = new byte[2];
		b[0] = (Integer.valueOf(hightPos)).byteValue();
		b[1] = (Integer.valueOf(lowPos)).byteValue();

		try {
			str = new String(b, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str.charAt(0);
	}

}
