package cn.cz.qqd;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.cz.data.MyUser;
import cn.cz.qqd.fragment.GeRenZhongXin;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdatePwdActivity extends Activity implements OnClickListener {
	private EditText password_old;
	private EditText password_new;
	private Button update;

	private SharedPreferences.Editor sp_ed;
	private SharedPreferences sp;
	private String phone = null;
	private String password = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.update_pwd);
		init();

	}

	private void init() {
		password_old = (EditText) findViewById(R.id.update_password_password_et);
		password_new = (EditText) findViewById(R.id.update_password_password2_et);
		update = (Button) findViewById(R.id.update_commit_btn);
		update.setOnClickListener(this);

		sp = getSharedPreferences("admin", MODE_PRIVATE);

		if (sp.getString("phone", "").length() != 0) {
			phone = sp.getString("phone", "");
			password = sp.getString("password", "");
		} else {
			Toast.makeText(this, "您还没有登录呢", 0).show();
			startActivity(new Intent(UpdatePwdActivity.this,
					DengluActivity.class));
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.update_commit_btn:

			BmobQuery<MyUser> query = new BmobQuery<MyUser>();
			query.addWhereEqualTo("phone", phone);
			query.findObjects(this, new FindListener<MyUser>() {
				@Override
				public void onSuccess(List<MyUser> object) {
					Toast.makeText(UpdatePwdActivity.this,
							"我查询到id了" + object.get(0).getObjectId(), 0).show();
					if (password_old.getText().toString()
							.equals(object.get(0).getPassword())) {
						// 更新用户密码
						MyUser newUser = new MyUser();
						newUser.setValue("password", password_new.getText()
								.toString());

						newUser.update(UpdatePwdActivity.this, object.get(0)
								.getObjectId(), new UpdateListener() {

							@Override
							public void onSuccess() {
								sp = getSharedPreferences("admin", MODE_PRIVATE);
								sp_ed = sp.edit();
								if (sp.getString("phone", "").length() != 0) {
									sp_ed.putString("phone", "");
									sp_ed.putString("password", "");
									sp_ed.putString("huanxinPwd", "");
									sp_ed.commit();
								}
								Toast.makeText(UpdatePwdActivity.this,
										"我修改密码成功了", 0).show();
								startActivity(new Intent(
										UpdatePwdActivity.this,
										DengluActivity.class));
								finish();
							}

							@Override
							public void onFailure(int code, String msg) {
								// TODO Auto-generated method stub
								Log.i("bmob", "更新失败：" + msg);
								Toast.makeText(UpdatePwdActivity.this,
										"我修改密码失败了" + msg, 0).show();
								System.out.println("bmob修改密码失败");
							}
						});
					} else {
						Toast.makeText(UpdatePwdActivity.this, "原密码输入错误!", 0)
								.show();
					}

				}

				@Override
				public void onError(int code, String msg) {
					// TODO Auto-generated method stub
					System.out.println("没有查询到相关id");
				}
			});

			break;
		default:
			break;
		}
	}

}
