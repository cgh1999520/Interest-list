package cn.cz.qqd;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.cz.data.MyUser;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserXinxiActivity extends Activity implements OnClickListener {
	private EditText et_nickname;
	private EditText gexingqianming;
	private Button bt_tijiao;
	SharedPreferences sp;
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_xinxi);
		et_nickname = (EditText) findViewById(R.id.nickname);
		bt_tijiao = (Button) findViewById(R.id.tj);
		gexingqianming = (EditText) findViewById(R.id.gexingqianming);
		bt_tijiao.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tj:
			sp = getSharedPreferences("admin", MODE_PRIVATE);
			if (sp.getString("phone", "").length() != 0) {
				update(sp.getString("phone", ""));
			}
			
			break;

		default:
			break;
		}
	}
	
	//更新用户信息
	private void update(String myphone){
		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("phone", myphone);
		query.findObjects(this, new FindListener<MyUser>() {
			@Override
			public void onSuccess(List<MyUser> object) {
				MyUser newUser = new MyUser();
				newUser.setNickName(et_nickname.getText().toString());
				newUser.setGexingqianming(gexingqianming.getText().toString());
				newUser.update(UserXinxiActivity.this, object.get(0).getObjectId(), new UpdateListener() {

				    @Override
				    public void onSuccess() {
				    	Toast.makeText(UserXinxiActivity.this, "修改信息成功", 0).show();
				    }

				    @Override
				    public void onFailure(int code, String msg) {
				    	
				    }
				});
			}

			@Override
			public void onError(int code, String msg) {
				Toast.makeText(UserXinxiActivity.this, "没有找到此id", 0).show();
			}
		});
	}
}	
