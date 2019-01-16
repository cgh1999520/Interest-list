package cn.cz.qqd;

import java.util.List;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import android.app.*;
import android.os.*;
import android.content.*;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.cz.data.*;
import android.widget.*;
import android.util.Log;
import android.view.View.*;
import android.view.*;

public class DengluActivity extends Activity implements OnClickListener {

	private Button bt_login;
	private TextView tv_zhuce, tv_wangjimiam;
	private EditText et_zhanghao, et_miam;
	private SharedPreferences.Editor sp_ed;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.denglu);
		init();
	}

	private void init() {
		tv_wangjimiam = (TextView) findViewById(R.id.denglu_tv_wangjimima);
		et_zhanghao = (EditText) findViewById(R.id.denglu_et_zhanghao);
		et_miam = (EditText) findViewById(R.id.denglu_ed_mima);
		bt_login = (Button) findViewById(R.id.denglu_bt_login);
		tv_zhuce = (TextView) findViewById(R.id.denglu_tv_zhuce);
		tv_wangjimiam.setOnClickListener(this);
		tv_zhuce.setOnClickListener(this);
		bt_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.denglu_bt_login:
			loginBmob();
			break;
		case R.id.denglu_tv_zhuce:
			startActivity(new Intent(DengluActivity.this, ZhuceActivity.class));
			break;
		case R.id.denglu_tv_wangjimima:
			startActivity(new Intent(DengluActivity.this, FindPwdActivity.class));
			break;
		}

	}

	// 登录bmob
	private void loginBmob() {

		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("phone", et_zhanghao.getText().toString());
		query.findObjects(this, new FindListener<MyUser>() {
			@Override
			public void onSuccess(List<MyUser> object) {
				String password = object.get(0).getPassword();
				final String huanxinPwd = object.get(0).getHuanxinPwd();
				if (password.equals(et_miam.getText().toString())) {
					// bmob的帐号验证成功后登录环信
					EMClient.getInstance().login(
							et_zhanghao.getText().toString(), huanxinPwd,
							new EMCallBack() {// 回调
								@Override
								public void onSuccess() {
									runOnUiThread(new Runnable() {
										public void run() {
											EMClient.getInstance()
													.groupManager()
													.loadAllGroups();
											EMClient.getInstance()
													.chatManager()
													.loadAllConversations();
											Log.d("main", "登录聊天服务器成功！");
											Toast.makeText(DengluActivity.this,
													"登陆聊天服务器成功！", 0).show();

											sp = getSharedPreferences("admin",
													MODE_PRIVATE);
											sp_ed = sp.edit();

											if (sp.getString("phone", "")
													.length() == 0) {
												sp_ed.putString("phone",
														et_zhanghao.getText()
																.toString());
												sp_ed.putString("password",
														et_miam.getText()
																.toString());
												sp_ed.putString("huanxinPwd",
														huanxinPwd);
												sp_ed.commit();
											}

											Intent intent = new Intent(
													DengluActivity.this,
													MainActivity.class);
											startActivity(intent);
											finish();
										}
									});
								}

								@Override
								public void onProgress(int progress,
										String status) {

								}

								@Override
								public void onError(int code, String message) {
									Log.d("main", "登录聊天服务器失败！");
									Toast.makeText(DengluActivity.this,
											"登录聊天服务器失败！", 0).show();
								}
							});

				} else {
					Toast.makeText(DengluActivity.this, "密码或帐号不正确", 0).show();
				}
			}

			@Override
			public void onError(int code, String msg) {
				Toast.makeText(DengluActivity.this, "没有找到此id" + msg, 0).show();
			}
		});

	}

}
