package cn.cz.qqd;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.cz.data.MyUser;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FindPwdActivity extends Activity implements OnClickListener {
	private EditText phoneNumber;
	private EditText code;
	private EditText password;
	private Button getCode;
	private Button findPwd;
	private int i = 30;
	private SharedPreferences.Editor sp_ed;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.find_password);
		init();
	}

	private void init() {
		phoneNumber = (EditText) findViewById(R.id.find_password_phone_et);
		code = (EditText) findViewById(R.id.find_password_code_et);
		password = (EditText) findViewById(R.id.find_password_password_et);
		getCode = (Button) findViewById(R.id.login_request_code_btn);
		findPwd = (Button) findViewById(R.id.login_commit_btn);
		getCode.setOnClickListener(this);
		findPwd.setOnClickListener(this);

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
		// TODO Auto-generated method stub
		String phoneNums = phoneNumber.getText().toString();
		switch (v.getId()) {
		case R.id.login_request_code_btn:
			// 1. 通过规则判断手机号
			if (!judgePhoneNums(phoneNums)) {
				return;
			} // 2. 通过sdk发送短信验证
			SMSSDK.getVerificationCode("86", phoneNums);

			// 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
			getCode.setClickable(false);
			getCode.setText("重新发送(" + i + ")");
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

		case R.id.login_commit_btn:
			SMSSDK.submitVerificationCode("86", phoneNums, code.getText()
					.toString());

			break;
		default:

			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == -9) {
				getCode.setText("重新发送(" + i + ")");
			} else if (msg.what == -8) {
				getCode.setText("获取验证码");
				getCode.setClickable(true);
				i = 30;
			} else {
				int event = msg.arg1;
				int result = msg.arg2;
				Object data = msg.obj;
				Log.e("event", "event=" + event);
				if (result == SMSSDK.RESULT_COMPLETE) {
					// 短信注册成功后，返回MainActivity,然后提示
					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
						Toast.makeText(FindPwdActivity.this, "提交验证码成功",
								Toast.LENGTH_SHORT).show();
						// 更新bmob帐号
						updatePwd();

					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						Toast.makeText(FindPwdActivity.this, "验证码已经发送",
								Toast.LENGTH_SHORT).show();
					} else {
						((Throwable) data).printStackTrace();
						Toast.makeText(FindPwdActivity.this, "验证码输入错误",
								Toast.LENGTH_SHORT).show();
					}
				}

			}
		}
	};

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

	public void updatePwd() {
		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("phone", phoneNumber.getText().toString());
		query.findObjects(this, new FindListener<MyUser>() {
			@Override
			public void onSuccess(List<MyUser> object) {
				Toast.makeText(FindPwdActivity.this,
						"我查询到id了" + object.get(0).getObjectId(), 0).show();
				// 更新用户密码
				MyUser newUser = new MyUser();
				newUser.setValue("password", password.getText().toString());

				newUser.update(FindPwdActivity.this, object.get(0)
						.getObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Log.i("bmob", "更新成功：");
						Toast.makeText(FindPwdActivity.this, "我修改密码成功了", 0)
								.show();
						System.out.println("bmob修改密码成功");
						sp = getSharedPreferences("admin", MODE_PRIVATE);
						sp_ed = sp.edit();
						if (sp.getString("phone", "").length() != 0) {
							sp_ed.putString("phone", "");
							sp_ed.putString("password", "");
							sp_ed.putString("huanxinPwd", "");
							sp_ed.commit();
						}
						startActivity(new Intent(FindPwdActivity.this,
								DengluActivity.class));
					}

					@Override
					public void onFailure(int code, String msg) {
						// TODO Auto-generated method stub
						Log.i("bmob", "更新失败：" + msg);
						Toast.makeText(FindPwdActivity.this, "我修改密码失败了" + msg,
								0).show();
						System.out.println("bmob修改密码失败");
					}
				});

			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				System.out.println("没有查询到相关id");
			}
		});
	}
}
