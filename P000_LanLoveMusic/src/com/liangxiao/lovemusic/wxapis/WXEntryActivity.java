package com.liangxiao.lovemusic.wxapis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.liangxiao.lovemusic.R;
import com.liangxiao.lovemusic.ui.MainActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.platformtools.Util;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	public static final String APP_ID = "wx494ae1909240bad1";
	private static final int THUMB_SIZE = 150;
	public static IWXAPI mApi;
	private Context mContext;
	private static WXEntryActivity mInstance;
	public int result = 0;
	FrameLayout layout_bar_icon;
	ImageButton btn_bar_back;

	public WXEntryActivity() {
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_success);
		Log.i("result", result + "");
		layout_bar_icon = (FrameLayout) findViewById(R.id.layout_bar_icon);
		layout_bar_icon.setVisibility(View.INVISIBLE);
		btn_bar_back = (ImageButton) findViewById(R.id.btn_bar_back);
		btn_bar_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(WXEntryActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		Intent intent = new Intent();
		intent.putExtra("result", "分享成功快去看看吧");
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private WXEntryActivity(Context context) {
		mContext = context;

		mApi = WXAPIFactory.createWXAPI(context, APP_ID, false);

		mApi.registerApp(APP_ID);
		mApi.handleIntent(getIntent(), this);
	}

	public static WXEntryActivity getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new WXEntryActivity(context);
		}
		return mInstance;
	}

	/**
	 * 发送文本信息到微信
	 * 
	 * @param text
	 */
	public static void sentRequest(String text) {
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		msg.description = text;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = "txt" + String.valueOf(System.currentTimeMillis());
		req.message = msg;

		// SendMessageToWX.Req.WXSceneSession 发送至微信的会话内
		// SendMessageToWX.Req.WXSceneTimeline 发送至朋友圈
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		mApi.sendReq(req);
	}

	/**
	 * 发送图片到微信
	 * 
	 * @param bitmap
	 */
	public static void sentBitmap(Bitmap bitmap) {
		WXImageObject imgObj = new WXImageObject(bitmap);

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE,
				THUMB_SIZE, true);
		bitmap.recycle();
		msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = "img" + String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;

		mApi.sendReq(req);
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResp(BaseResp resp) {

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}

		// Toast.makeText(this, result, Toast.LENGTH_LONG).show();

	}
}
