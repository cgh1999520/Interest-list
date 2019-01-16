package cn.cz.qqd.fragment;

import java.util.ArrayList;
import java.util.List;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.exceptions.HyphenateException;

import android.R.string;
import android.support.v4.app.*;
import android.widget.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.os.*;
import cn.cz.qqd.*;
import cn.cz.qqd.adapter.MyFriendsListAdapter;

public class JiaoLiuQuan_LianXiRen extends Fragment implements OnClickListener {

	private ListView lv_haoyou;
	private Button bt_sousuo;
	private EditText et_zhanghao;
	private SharedPreferences sp;
	ArrayList<String> usernames;
	MyFriendsListAdapter myFriendsListAdapter = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.jiaoliuquan_lianxiren, container,
				false);
		init(view);

		getFriendsList();
		return view;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			ArrayList<String> friends_list = msg.getData().getStringArrayList(
					"friends");
			System.out.println(friends_list.get(0));
			myFriendsListAdapter = new MyFriendsListAdapter(friends_list,
					getContext());
			lv_haoyou.setAdapter(myFriendsListAdapter);
		};
	};

	private void getFriendsList() {
		sp = getContext().getSharedPreferences("admin",
				getContext().MODE_PRIVATE);
		if (sp.getString("phone", "").length() != 0) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						usernames = (ArrayList<String>) EMClient.getInstance()
								.contactManager().getAllContactsFromServer();
						if (!usernames.isEmpty()) {
							Message msg = new Message();
							Bundle bundle = new Bundle();
							bundle.putStringArrayList("friends", usernames);
							msg.setData(bundle);
							handler.sendMessage(msg);
						}
					} catch (HyphenateException e) {
						e.printStackTrace();
					}

				}
			}).start();
		}
	}

	// 获取所有
	private void init(View view) {
		lv_haoyou = (ListView) view.findViewById(R.id.jiaoliuquan_ls_lianxiren);
		bt_sousuo = (Button) view
				.findViewById(R.id.jiaoliuquan_lianxiren_bt_tianjiahaoyou);
		et_zhanghao = (EditText) view
				.findViewById(R.id.jiaoliuquan_lianxiren_et_tianjiahaoyou);
		bt_sousuo.setOnClickListener(this);

	}

	// 给listview添加数据
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.jiaoliuquan_lianxiren_bt_tianjiahaoyou:
			try {
				EMClient.getInstance().contactManager()
						.addContact(et_zhanghao.getText().toString(), "快加我");
				Toast.makeText(getContext(), "发m送请求", 0).show();
			} catch (HyphenateException e) {
				e.printStackTrace();
			}
			if (myFriendsListAdapter!=null) {
				myFriendsListAdapter.notifyDataSetChanged();
			}else{
				getFriendsList();
			}
			break;

		default:
			break;
		}

	}

}
