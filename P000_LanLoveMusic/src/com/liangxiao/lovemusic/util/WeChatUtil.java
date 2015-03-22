package com.liangxiao.lovemusic.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.platformtools.Util;

public class WeChatUtil {
	public static final String APP_ID = "wx494ae1909240bad1";
	private static final int THUMB_SIZE = 150;
	public static IWXAPI mApi;
	private Context mContext;
	private static WeChatUtil mInstance;

	private WeChatUtil(Context context) {
		mContext = context;

		mApi = WXAPIFactory.createWXAPI(context, APP_ID, false);

		mApi.registerApp(APP_ID);
	}

	public static WeChatUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new WeChatUtil(context);
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
}
