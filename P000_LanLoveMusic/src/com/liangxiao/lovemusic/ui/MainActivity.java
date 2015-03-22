package com.liangxiao.lovemusic.ui;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;

import com.liangxiao.lovemusic.R;
import com.liangxiao.lovemusic.data.Const;
import com.liangxiao.lovemusic.model.IAlertDialogButtonListener;
import com.liangxiao.lovemusic.model.IWordButtonClickListener;
import com.liangxiao.lovemusic.model.Song;
import com.liangxiao.lovemusic.model.WordButton;
import com.liangxiao.lovemusic.myui.MyGridView;
import com.liangxiao.lovemusic.util.MyPlayer;
import com.liangxiao.lovemusic.util.Util;
import com.liangxiao.lovemusic.util.WeChatUtil;
import com.liangxiao.lovemusic.wxapis.WXEntryActivity;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class MainActivity extends Activity implements IWordButtonClickListener {

	public static final String KEY_EXTRAS = "extras";
	public final static int STATUS_ANSWER_RIGHT = 1;// 答案正确
	public final static int STATUS_ANSWER_WRONG = 2;// 答案错误
	public final static int STATUS_ANSWER_PART = 3;// 答案不完整
	public final static int SPASH_TIMES = 6; // 闪烁次数
	public final static int ID_DIALOG_DELETE = 1; // 删除金币30分
	public final static int ID_DIALOG_TIPS = 2; // 提示金币90分
	public final static int ID_DIALOG_LACK = 3; // 金币不够
	public final static int ID_DIALOG_SHARE = 4; // 分享到微信
	// 唱片相关动画
	Animation mPanAnimation;
	LinearInterpolator mPanLinearInterpolator;
	Animation mBarInAnimation;
	LinearInterpolator mBarInLinearInterpolator;
	Animation mBarOutAnimation;
	LinearInterpolator mBarOutLinearInterpolator;
	// 唱片控件
	ImageView img1;
	// 拨杆控件
	ImageView img2;
	// 当前关索引
	TextView mCurrentStagePassView;
	TextView mCurrentStageView;
	TextView mCurrentSongNamePassView;
	// play
	ImageButton btn_play_start;
	// WeChat
	ImageButton btn_Wechat;
	// 当前动画是否运行
	Boolean mIsRunning = false;
	// 文字框容器
	ArrayList<WordButton> mAddWords;
	ArrayList<WordButton> mBtnSelectWords;
	MyGridView mMyGridView;
	// 已选择文字框UI容器
	LinearLayout mViewWordsContainer;
	// 当前的歌曲
	Song mCurrentSong;
	// 当前的第几关的索引
	int mCurrentStageIndex = -1;
	// 过关加载xml
	View mPassView;
	// 通关界面加载xml
	View mAllPassView;
	// 当前金币的数量
	int mCurrentCoins = Const.TOTAL_COINS;
	// 金币的TextView
	TextView mTextViewCurrentCoins;

	// 返回的结果WeChat result
	String result;
	EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 读取游戏的数据
		int[] datas = Util.ReadData(MainActivity.this);
		mCurrentStageIndex = datas[Const.INDEX_READ_DATA_STAGE];
		mCurrentCoins = datas[Const.INDEX_READ_DATA_COINS];

		mTextViewCurrentCoins = (TextView) findViewById(R.id.txt_bar_coins);
		mTextViewCurrentCoins.setText(mCurrentCoins + "");
		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		mMyGridView = (MyGridView) findViewById(R.id.gridview);
		mMyGridView.registOnWordButtonClick(this);
		mViewWordsContainer = (LinearLayout) findViewById(R.id.word_select_container);
		btn_play_start = (ImageButton) findViewById(R.id.btn_play_start);
		btn_Wechat = (ImageButton) findViewById(R.id.btn_Wechat);
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dialog_view = layoutInflater.inflate(R.layout.dialog_view_share,
				null);
		editText = (EditText) dialog_view.findViewById(R.id.txt_dailog_message);
		editText.setText("爱音乐猜歌名，大家都来猜猜我是谁，地址：http://zhushou.360.cn/detail/index/soft_id/1767844?recrefer=SE_D_梁小三");
		// result = editText.getText().toString();
		// 加载动画
		init_animation();
		// 注册微信
		WeChatUtil.getInstance(MainActivity.this);

		// 初始化游戏文字的数据部分
		initCurrentStageData();
		// 处理删除待选文字扣30分的部分
		handleDeleteWord();
		// 处理提示文字框中文字事件部分
		handleTipAnswer();
		btn_play_start.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				handlePlayButton();
			}
		});
		// WeChat
		btn_Wechat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 显示对话框
				// showConfirmDialog(ID_DIALOG_SHARE);
				// WeChat
				String a = "爱音乐猜歌名，大家都来猜猜我是谁，地址：http://zhushou.360.cn/detail/index/soft_id/1767844?recrefer=SE_D_梁小三";
				WeChatUtil.sentRequest(a);
				// Handler handler = new Handler();
				// handler.postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// Toast.makeText(MainActivity.this, "分享成功", 2).show();
				// }
				// }, 5000);
			}
		});
		// Intent intent = getIntent();
		// result = intent.getStringExtra("result");
		// Toast.makeText(this, result, Toast.LENGTH_LONG);
	}

	/**
	 * 播放按钮
	 */
	protected void handlePlayButton() {
		if (img2 != null) {
			if (!mIsRunning) {
				mIsRunning = true;
				// 开始拨杆进入的动画部分
				img2.startAnimation(mBarInAnimation);
				btn_play_start.setVisibility(View.INVISIBLE);

				// 播放音乐
				MyPlayer.playSong(MainActivity.this,
						mCurrentSong.getSongFileName());

			}
		}

	}

	@Override
	protected void onResume() {
		JPushInterface.onResume(this);
		super.onResume();
	}

	@Override
	public void onPause() {
		JPushInterface.onPause(this);
		// 保存游戏数据
		Util.saveData(MainActivity.this, mCurrentStageIndex - 1, mCurrentCoins);
		img1.clearAnimation();
		// 暂停音乐
		MyPlayer.stopSong(MainActivity.this);
		super.onPause();
	}

	/**
	 * 处理提示文字框中文字事件部分
	 */
	private void handleTipAnswer() {
		ImageButton btn_tip_answer = (ImageButton) findViewById(R.id.btn_tip_answer);
		btn_tip_answer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 显示对话框
				showConfirmDialog(ID_DIALOG_TIPS);

			}
		});
	}

	/**
	 * 提示一个字扣90分
	 */
	private void tipAnswer() {
		boolean tipWord = false;
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {

				tipWord = true;
				// 减少金币数量
				if (!handleCoins(-getTipWordCoins())) {
					// 金币不足
					showConfirmDialog(ID_DIALOG_LACK);
					return;
				}
				// 根据当前的答案框条件选择对应的文字并填入
				onWordButtonClick(findIsAnswerWord(i));
				break;
			}
		}
		// 没有找到可以填充的答案
		if (!tipWord) {
			// 闪烁文字提示用户
			sparkTheWords();
		}
	}

	/**
	 * 找到一个答案文字
	 * 
	 * @param i
	 *            当前需要填入答案框的索引
	 * @return
	 */
	private WordButton findIsAnswerWord(int index) {
		WordButton buf = null;
		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			buf = mAddWords.get(i);
			if (buf.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[index])) {
				return buf;
			}
		}
		return null;
	}

	/**
	 * 处理删除待选文字扣30分的部分
	 */
	private void handleDeleteWord() {
		ImageButton btn_delete_word = (ImageButton) findViewById(R.id.btn_delete_word);
		btn_delete_word.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 显示对话框
				showConfirmDialog(ID_DIALOG_DELETE);

			}
		});
	}

	/**
	 * 自定义的AlertDialog事件响应 删除30分操作
	 */
	/**
	 * 扣30分
	 */
	private IAlertDialogButtonListener mBtnOkDeleteWordListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 删除一个字扣30分
			deleteoneword();
		}
	};
	/**
	 * 扣90分
	 */
	private IAlertDialogButtonListener mBtnOkTipAnswerListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 提示一个字扣90分
			tipAnswer();
		}
	};
	/**
	 * 分享到微信
	 */
	private IAlertDialogButtonListener shareListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			// WXEntryActivity.sentRequest(editText.getText().toString());
			String text = editText.getText().toString();
			// 初始化一个WXTextObject对象
			WXTextObject textObj = new WXTextObject();
			textObj.text = text;

			// 用WXTextObject对象初始化一个WXMediaMessage对象
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;
			// 发送文本类型的消息时，title字段不起作用
			// msg.title = "Will be ignored";
			msg.description = text;

			// 构造一个Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneTimeline;
			// req.scene = isTimelineCb.isChecked() ?
			// SendMessageToWX.Req.WXSceneTimeline
			// : SendMessageToWX.Req.WXSceneSession;

			// 调用api接口发送数据到微信
			WXEntryActivity.mApi.sendReq(req);
			// finish();
		}
	};

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	/**
	 * 金币不足
	 */
	private IAlertDialogButtonListener mBtnLackCoinsListener = new IAlertDialogButtonListener() {

		@Override
		public void onClick() {
			// 执行事件
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://liangxiao.blog.51cto.com/");
			intent.setData(content_url);
			startActivity(intent);
			MainActivity.this.finish();
		}
	};

	/**
	 * 显示dialog
	 * 
	 * @param id
	 */
	private void showConfirmDialog(int id) {
		switch (id) {
		case ID_DIALOG_DELETE:
			Util.showDialog(MainActivity.this, "确定花费" + getDeleteWordCoins()
					+ "金币删除一个错误答案？", mBtnOkDeleteWordListener);
			break;
		case ID_DIALOG_TIPS:
			Util.showDialog(MainActivity.this, "确定花费" + getTipWordCoins()
					+ "金币提示一个正确答案？", mBtnOkTipAnswerListener);
			break;
		case ID_DIALOG_LACK:
			Util.showDialog(MainActivity.this, "金币不够，请充值",
					mBtnLackCoinsListener);
			break;

		case ID_DIALOG_SHARE:
			Util.showDialog_share(MainActivity.this, editText.getText()
					.toString(), shareListener);
			break;
		default:
			break;
		}
	}

	/**
	 * 删除一个字扣30分
	 */
	private void deleteoneword() {
		// 减少金币
		if (!handleCoins(-getDeleteWordCoins())) {
			// 金币不够，dailog
			showConfirmDialog(ID_DIALOG_LACK);
			return;
		}
		// 将这个索引的文字设置不可见，减少30分
		setButtonVisiable(findNotAnswerWord(), View.INVISIBLE);

	}

	/**
	 * 从配置文件里读取删除操作所要用的金币
	 */
	private int getDeleteWordCoins() {
		return this.getResources().getInteger(R.integer.pay_delete_word);
	}

	/**
	 * 从配置文件里读取提示操作所要用的金币
	 */
	private int getTipWordCoins() {
		return this.getResources().getInteger(R.integer.pay_tip_answer);
	}

	/**
	 * 从配置文件里读取过关操作所要用的金币
	 */
	private int getAddWordCoins() {
		return this.getResources().getInteger(R.integer.pay_add_word);
	}

	/**
	 * 寻找一个随机文字
	 * 
	 * @return 随机除了正确答案外的22个文字中的一个
	 */
	private WordButton findNotAnswerWord() {
		Random random = new Random();
		WordButton buf = null;
		while (true) {
			int index = random.nextInt(MyGridView.COUNTS_WORDS);
			buf = mAddWords.get(index);
			if (buf.mIsVisiable && !isTheAnswerWord(buf)) {
				return buf;
			}
		}

	}

	/**
	 * 判断24个随机文字中是否有正确答案文字部分
	 */
	private boolean isTheAnswerWord(WordButton word) {
		boolean result = false;
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			if (word.mWordString.equals(""
					+ mCurrentSong.getNameCharacters()[i])) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 增加/减少金币的数量部分传值操作
	 */
	private boolean handleCoins(int data) {
		// 判断当前总的金币数量是否可被减少
		if (mCurrentCoins + data >= 0) {
			mCurrentCoins += data;
			mTextViewCurrentCoins.setText(mCurrentCoins + "");
			return true;
		} else {
			return false;
		}
	}

	public void onWordButtonClick(WordButton wordButton) {
		// Toast.makeText(this, wordButton.mViewButton+"",
		// Toast.LENGTH_SHORT).show();
		setSelectWord(wordButton);

		// 获取答案的状态
		int check = checkTheAnswer();

		if (check == STATUS_ANSWER_RIGHT) {
			// 弹出正确的xml提醒正确
			handlePassEvent();
		} else if (check == STATUS_ANSWER_WRONG) {
			// 闪烁文字提醒
			sparkTheWords();
		} else if (check == STATUS_ANSWER_PART) {
			for (int i = 0; i < mBtnSelectWords.size(); i++) {
				// 设置答案框中文字为白色
				mBtnSelectWords.get(i).mViewButton.setTextColor(Color.WHITE);

			}
		}
	}

	/**
	 * 弹出正确的xml提醒正确
	 */
	private void handlePassEvent() {
		// 显示过关界面
		mPassView = (LinearLayout) this.findViewById(R.id.pass_layout);
		mPassView.setVisibility(View.VISIBLE);
		// 闭关动画
		img1.clearAnimation();
		// 停止播放音乐
		MyPlayer.stopSong(MainActivity.this);
		// 加金币
		handleCoins(+getAddWordCoins());
		// 播放金币音乐
		MyPlayer.playTone(MainActivity.this, MyPlayer.INDEX_STONE_COIN);
		// 当前关的索引
		mCurrentStagePassView = (TextView) findViewById(R.id.guanka);
		if (mCurrentStagePassView != null) {
			mCurrentStagePassView.setText((mCurrentStageIndex + 1) + "");
		}
		// 显示歌曲的名称
		mCurrentSongNamePassView = (TextView) findViewById(R.id.txt_song_name);
		if (mCurrentSongNamePassView != null) {
			mCurrentSongNamePassView.setText(mCurrentSong.getSongName());
		}
		// 下一关按钮
		ImageButton btn_next = (ImageButton) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (judegAppPassed()) {
					// 进入到通关界面
					Util.startActivity(MainActivity.this, AllPassView.class);

				} else {
					// 开始新的一关
					mPassView.setVisibility(View.GONE);

					// 加载关卡数据
					initCurrentStageData();

				}
			}
		});
		// share按钮
		ImageButton btn_share = (ImageButton) findViewById(R.id.btn_share);
		btn_share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mPassView.setVisibility(View.GONE);
				String a = "爱音乐猜歌名，大家都来猜猜我是谁，地址：http://zhushou.360.cn/detail/index/soft_id/1767844?recrefer=SE_D_梁小三";
				WeChatUtil.sentRequest(a);

			}
		});
	}

	/**
	 * 判断是否通关
	 */
	private boolean judegAppPassed() {
		return (mCurrentStageIndex == Const.SONG_INFO.length - 1);
	}

	/**
	 * 闪烁文字提醒部分
	 */
	private void sparkTheWords() {
		final Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			boolean mChange = false;
			int mSpardTimes = 0;

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						if (++mSpardTimes > SPASH_TIMES) {
							// 超过6次就不闪烁
							timer.cancel();
							return;
						}

						// 执行闪烁逻辑：交替显示红色和白色文字
						for (int i = 0; i < mBtnSelectWords.size(); i++) {
							mBtnSelectWords.get(i).mViewButton
									.setTextColor(mChange ? Color.RED
											: Color.WHITE);
						}
						mChange = !mChange;
					}
				});

			}
		};

		timer.schedule(task, 1, 150);
	}

	/**
	 * 获取答案的状态
	 * 
	 * @return
	 */
	private int checkTheAnswer() {
		// 先检查长度
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				return STATUS_ANSWER_PART;
			}
		}
		// 答案完整后继续检查
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			sb.append(mBtnSelectWords.get(i).mWordString);
		}

		int checkID = sb.toString().equals(mCurrentSong.getSongName()) ? STATUS_ANSWER_RIGHT
				: STATUS_ANSWER_WRONG;
		return checkID;
	}

	/**
	 * 设置答案区域文字部分
	 * 
	 * @param wordButton
	 */
	private void setSelectWord(WordButton wordButton) {
		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			if (mBtnSelectWords.get(i).mWordString.length() == 0) {
				// 设置答案文字框的内容及可见性
				mBtnSelectWords.get(i).mWordString = wordButton.mWordString;
				mBtnSelectWords.get(i).mViewButton
						.setText(wordButton.mWordString);
				mBtnSelectWords.get(i).mIsVisiable = true;
				// 记录索引
				mBtnSelectWords.get(i).mIndex = wordButton.mIndex;
				// 设置GridView里面的待选文字可见性
				setButtonVisiable(wordButton, View.INVISIBLE);
				break;
			} else {
				// wucaozuo
			}
		}
	}

	/**
	 * 设置GridView里面的待选文字可见性
	 * 
	 * @param wordButton
	 * @param invisible
	 */
	private void setButtonVisiable(WordButton wordButton, int invisible) {
		wordButton.mViewButton.setVisibility(invisible);
		wordButton.mIsVisiable = (invisible == View.VISIBLE) ? true : false;

	}

	private void init_animation() {
		// 停止播放的时候拨杆的动画效果部分
		mPanAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
		mPanLinearInterpolator = new LinearInterpolator();
		mPanAnimation.setInterpolator(mPanLinearInterpolator);
		mPanAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// 开始拨杆退出的时候动画效果
				img2.startAnimation(mBarOutAnimation);
			}
		});
		// 开始唱片动画部分
		mBarInAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_45);
		mBarInLinearInterpolator = new LinearInterpolator();
		mBarInAnimation.setFillAfter(true);
		mBarInAnimation.setInterpolator(mBarInLinearInterpolator);
		mBarInAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				// 开始img1唱片动画
				img1.startAnimation(mPanAnimation);
			}
		});

		// 整套动画结束部分
		mBarOutAnimation = AnimationUtils.loadAnimation(this,
				R.anim.rotate_d_45);
		mBarOutLinearInterpolator = new LinearInterpolator();
		mBarOutAnimation.setFillAfter(true);
		mBarOutAnimation.setInterpolator(mBarOutLinearInterpolator);
		mBarOutAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				Log.i("tag", "动画结束");
				mIsRunning = false;
				btn_play_start.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * 当前关卡的数据信息部分
	 */
	private void initCurrentStageData() {
		// 读取当前关卡的歌曲信息
		mCurrentSong = loadStageSongInfo(++mCurrentStageIndex);// 从0开始的索引，得到一个歌曲对象,ru{"__00000.m4a","征服"}

		// 初始化已选择框
		mBtnSelectWords = initWordSelect();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// 清空上一个答案
		mViewWordsContainer.removeAllViews();

		for (int i = 0; i < mBtnSelectWords.size(); i++) {
			mViewWordsContainer.addView(mBtnSelectWords.get(i).mViewButton,
					params);
		}
		// 当前的关索引
		mCurrentStageView = (TextView) findViewById(R.id.txt_passview_mainactivity);
		if (mCurrentStageView != null) {
			mCurrentStageView.setText((mCurrentStageIndex + 1) + "");
		}

		// 获取数据
		mAddWords = initAllWord();
		// 更新数据 - MyGridView
		mMyGridView.updateData(mAddWords);
		// 一开始就播放
		handlePlayButton();
	}

	// 读取当前关卡的歌曲信息,如Song对象 征服
	private Song loadStageSongInfo(int stageIndex) {
		Song song = new Song();
		String[] stage = Const.SONG_INFO[stageIndex];
		song.setSongFileName(stage[Const.INDEX_SONG_FILENAME]);
		song.setSongName(stage[Const.INDEX_SONG_NAME]);

		return song;
	}

	/**
	 * 生成所有的文字，动态生成的文字+歌曲名字=24
	 */
	private String[] generateWord() {
		Random random = new Random();
		String[] words = new String[MyGridView.COUNTS_WORDS];
		// 存入歌曲名字部分
		// String a = mCurrentSong.getNameLength() + "";
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			words[i] = mCurrentSong.getNameCharacters()[i] + "";
		}
		// 获取随机文字部分
		for (int i = mCurrentSong.getNameLength(); i < MyGridView.COUNTS_WORDS; i++) {
			words[i] = Util.getRandomChar() + "";
		}

		// 打乱文字顺序：首先从所有元素中随机选取一个与第一个元素进行交换，
		// 然后在第二个之后选择一个元素与第二个交换，知道最后一个元素。
		// 这样能够确保每个元素在每个位置的概率都是1/n。

		for (int i = MyGridView.COUNTS_WORDS - 1; i >= 0; i--) {
			int index = random.nextInt(i + 1);// 23中随意挑一个，往后就是22个文字中挑一个。。。

			String buf = words[index];
			words[index] = words[i];
			words[i] = buf;

		}

		return words;
	}

	/**
	 * 初始化待选文字框24
	 */
	private ArrayList<WordButton> initAllWord() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();

		// 获得所有待选文字
		String[] words = generateWord();
		// .........

		for (int i = 0; i < MyGridView.COUNTS_WORDS; i++) {
			WordButton button = new WordButton();

			button.mWordString = words[i];

			data.add(button);
		}

		return data;
	}

	/**
	 * 初始化已选择的文字框
	 * 
	 * @return
	 */
	private ArrayList<WordButton> initWordSelect() {
		ArrayList<WordButton> data = new ArrayList<WordButton>();
		for (int i = 0; i < mCurrentSong.getNameLength(); i++) {
			View view = Util.getView(MainActivity.this,
					R.layout.name_selector_item);
			final WordButton holder = new WordButton();
			holder.mViewButton = (Button) view.findViewById(R.id.item_btn);
			holder.mViewButton.setTextColor(Color.WHITE);
			holder.mViewButton.setText("");
			holder.mIsVisiable = false;
			holder.mViewButton.setBackgroundResource(R.drawable.game_wordblank);
			holder.mViewButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 清空当前的一个字，然后还原gridview中mcurrentindex的可见性
					clearSelfVisiableGirdView(holder);
				}
			});
			data.add(holder);
		}
		return data;
	}

	private void clearSelfVisiableGirdView(WordButton wordButton) {
		wordButton.mViewButton.setText("");
		wordButton.mWordString = "";
		wordButton.mIsVisiable = false;

		// 设置gridview的可见性
		setButtonVisiable(mAddWords.get(wordButton.mIndex), View.VISIBLE);
	}

}
