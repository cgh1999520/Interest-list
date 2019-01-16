package cn.cz.qqd;

import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ChatActivity extends FragmentActivity {

	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_activity);
	
		intent = getIntent();
		
		EaseChatFragment chatFragment = new EaseChatFragment(); 
		 //传入参数
		 Bundle args = new Bundle();
		 args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
		 args.putString(EaseConstant.EXTRA_USER_ID,intent.getStringExtra("toname"));
		 chatFragment.setArguments(args);

	   getSupportFragmentManager().beginTransaction().add(R.id.chat_activity,chatFragment).commit();
	      

	}
}
