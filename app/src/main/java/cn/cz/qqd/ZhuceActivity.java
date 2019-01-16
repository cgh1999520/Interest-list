package cn.cz.qqd;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import cn.bmob.v3.listener.SaveListener;
import cn.cz.data.MyUser;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

public class ZhuceActivity extends Activity implements OnClickListener {
	// 手机号输入框
	private EditText et_phone;

	// 验证码输入框
	private EditText et_code;

	// 获取验证码按钮
	private Button et_getCode;

	// 注册按钮
	private Button commitBtn;
	// 密码框
	private EditText et_password;

	int i = 30;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 透明通知栏 更为美观
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.activity_zhuce);
		init();
	}

	// 获取所有控件
	private void init() {
		et_phone = (EditText) findViewById(R.id.login_input_phone_et);
		et_code = (EditText) findViewById(R.id.login_input_code_et);
		et_getCode = (Button) findViewById(R.id.login_request_code_btn);
		commitBtn = (Button) findViewById(R.id.login_commit_btn);
		et_password = (EditText) findViewById(R.id.login_input_password_et);

		et_getCode.setOnClickListener(this);
		commitBtn.setOnClickListener(this);

		EventHandler eventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);

			}
		};
		// 注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	}

	@Override
	public void onClick(View v) {
		String phoneNums = et_phone.getText().toString();
		switch (v.getId()) {
		case R.id.login_request_code_btn:
			// 1. 通过规则判断手机号
			if (!judgePhoneNums(phoneNums)) {
				return;
			} // 2. 通过sdk发送短信验证
			SMSSDK.getVerificationCode("86", phoneNums);

			// 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
			et_getCode.setClickable(false);
			et_getCode.setText("重新发送(" + i + ")");
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (; i > 0; i--) {
						handler.sendEmptyMessage(-9);
						if (i <= 0) {
							break;
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendEmptyMessage(-8);
				}
			}).start();
			break;
		// 注册按钮
		case R.id.login_commit_btn:
			SMSSDK.submitVerificationCode("86", phoneNums, et_code
					.getText().toString());
			createProgressBar();
			break;

		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				et_getCode.setText("重新发送(" + i + ")");
			} else if (msg.what == -8) {
				et_getCode.setText("获取验证码");
				et_getCode.setClickable(true);
				i = 30;
			} else {
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				Log.e("event", "event=" + event);
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
						Toast.makeText(ZhuceActivity.this, "提交验证码成功",
								Toast.LENGTH_SHORT).show();
						register();

					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(ZhuceActivity.this, "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					} else {
						((Throwable) data).printStackTrace();
						Toast.makeText(ZhuceActivity.this, "验证码输入错误",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		}
	};

	// 注册环信聊天账号 注册是个耗时操作 需要放到 线程未完成
	private void huanXinZhangHao(final String name,final String password) {
		// 注册失败会抛出HyphenateException
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					EMClient.getInstance().createAccount(name, password);
					startActivity(new Intent(ZhuceActivity.this,MainActivity.class));
				} catch (HyphenateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	/**
	 * 判断手机号码是否合理
	 * 
	 * @param phoneNums
	 */
	private boolean judgePhoneNums(String phoneNums) {
		if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
			return true;
		}
		Toast.makeText(this, "手机号码输入有误！", Toast.LENGTH_SHORT).show();
		return false;
	}

	/**
	 * 判断一个字符串的位数
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean isMatchLength(String str, int length) {
		if (str.isEmpty()) {
			return false;
		} else {
			return str.length() == length ? true : false;
		}
	}

	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobileNums))
			return false;
		else
			return mobileNums.matches(telRegex);
	}

	// 显示注册元圆形进度条
	private void createProgressBar() {
		FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		ProgressBar mProBar = new ProgressBar(this);
		mProBar.setLayoutParams(layoutParams);
		mProBar.setVisibility(View.VISIBLE);
		layout.addView(mProBar);
	}

	// 注册方法
	private void register() {

		MyUser myUser = new MyUser();
		myUser.setPhone(et_phone.getText().toString());
		myUser.setPassword(et_password.getText().toString());
		myUser.setHuanxinPwd(et_password.getText().toString());
		
		myUser.save(this, new SaveListener() {

		    @Override
		    public void onSuccess() {
		    	// 注册 环信 聊天接口
		    	Toast.makeText(ZhuceActivity.this, "bmob注册成功", 0).show();
				huanXinZhangHao(et_phone.getText().toString(),
						et_password.getText().toString());
		    }

		    @Override
		    public void onFailure(int code, String arg0) {
		        // 添加失败
		    	Toast.makeText(ZhuceActivity.this, "bmob注册失败", 0).show();
		    }
		});
		

	}

}
