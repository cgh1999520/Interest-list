package cn.cz.qqd;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.cz.data.MyUser;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class addFriendActivity extends Activity implements OnClickListener {

	private TextView tv_name;
	private TextView tv_reason;
	private Button bt_yes;
	private Button bt_no;
	private String name;
	private String reason;
	private SharedPreferences sp;

	private Message msg;
	private Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friend);
		Bundle bundle = getIntent().getBundleExtra("object");
		name = bundle.getString("name");
		reason = bundle.getString("reason");
		init();

	}

	private void init() {
		// TODO Auto-generated method stub
		tv_name = (TextView) findViewById(R.id.name);
		tv_reason = (TextView) findViewById(R.id.reason);
		bt_yes = (Button) findViewById(R.id.yes);
		bt_no = (Button) findViewById(R.id.no);
		tv_name.setText(name);
		tv_reason.setText(reason);
		bt_yes.setOnClickListener(this);
		bt_no.setOnClickListener(this);
		msg = new Message();
		bundle = new Bundle();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Toast.makeText(addFriendActivity.this,
					msg.getData().getString("text"), 0).toString();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yes:
			bundle.putString("text",
					name+reason);
			sp = getSharedPreferences("admin", MODE_PRIVATE);
			if (sp.getString("phone", "").length() != 0) {
				BmobQuery<MyUser> query = new BmobQuery<MyUser>();
				query.addWhereEqualTo("phone", sp.getString("phone", ""));
				query.findObjects(addFriendActivity.this,
						new FindListener<MyUser>() {
							@Override
							public void onSuccess(List<MyUser> object) {
								MyUser newUser = new MyUser();
								newUser.setFriendID(name);

								newUser.update(addFriendActivity.this, object
										.get(0).getObjectId(),
										new UpdateListener() {
											@Override
											public void onSuccess() {
												try {
													EMClient.getInstance()
															.contactManager()
															.acceptInvitation(
																	name);
													bundle.putString("text",
															"添加好友成功");
												} catch (HyphenateException e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}
											@Override
											public void onFailure(int code,
													String s) {
												bundle.putString("text",
														"添加好友失败");
											}
										});
							}

							@Override
							public void onError(int code, String s) {
								bundle.putString("text", "没有找到此帐号");
							}
						});
				msg.setData(bundle);
				handler.sendMessage(msg);
				finish();
			}

			break;
		case R.id.no:
			try {
				EMClient.getInstance().contactManager().declineInvitation(name);
				bundle.putString("text", "已拒绝");
				msg.setData(bundle);
				handler.sendMessage(msg);
			} catch (HyphenateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finish();
			break;
		default:
			break;
		}
	}
}
