package cn.cz.qqd;

import android.support.v4.app.*;
import android.support.v4.view.*;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.cz.data.MyUser;
import cn.cz.qqd.adapter.MyFragmentAdapter;
import cn.cz.qqd.fragment.*;

import java.util.*;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;

import android.view.View.*;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private JiaoLiuQuan jiaoliuquan;
	private QingDanGuanLi qingdanguanli;
	private Zhuye zhuye;
	private GeRenZhongXin geren;
	private ViewPager view_pager;
	private List<Fragment> list_fragent;
	private MyFragmentAdapter myadapter;
	private SharedPreferences.Editor sp_ed;
	private SharedPreferences sp;
	int text_yes;
	int text_no;
	public static Handler myHandler;

	boolean zidongdenglu = true;

	private LinearLayout lin_zhuye, lin_jiaoliuquan, lin_gerenzhongxin,
			lin_qingdanguanli;
	private ImageView iv_zhuye, iv_jiaoliuquan, iv_gerenzhongxin,
			iv_qingdanguanli;
	private TextView tv_zhuye, tv_jiaoliuquan, tv_gerenzhongxin,
			tv_qingdanguanli;

	private String myphone;
	private String mypassword;
	
	private Bundle bundle;
	private Message msg;
	private Bundle huanxin_bundle;
	private Message huanxin_msg;
	//用于环信加好友的延时处理
	private int i = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		getWindow()
				.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.main);

		view_pager = (ViewPager) findViewById(R.id.main_view_pager);
		view_pager.setOnPageChangeListener(new ViewPagerJianting());
		Mydata();
		init();

		// 关于加好友状态的监听
		EMClient.getInstance().contactManager().setContactListener(
				new MyContactListener());
		// 注册一个监听连接状态的listener
		EMClient.getInstance()
				.addConnectionListener(new MyConnectionListener());

		// 用于检测环信连接状态
		myHandler = new Handler() {
			public void handleMessage(Message msg) {
				String str = msg.getData().getString("buff");
				Toast.makeText(MainActivity.this, str, 0).show();
			}
		};
	}

	// 实现ConnectionListener接口
	// 对环信连接状态的监听
	private class MyConnectionListener implements EMConnectionListener {
		@Override
		public void onConnected() {
			// 已连接到服务器
		}

		@Override
		public void onDisconnected(final int error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (error == EMError.USER_REMOVED) {
						// 显示帐号已经被移除
						bundle.putString("buff", "帐号已经被移除");
					} else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
						// 显示帐号在其他设备登录
						bundle.putString("buff", "帐号在其他设备登陆");
						sp = getSharedPreferences("admin", MODE_PRIVATE);
						if (sp.getString("phone", "").length() != 0) {
							BmobQuery<MyUser> query = new BmobQuery<MyUser>();
							query.addWhereEqualTo("phone",
									sp.getString("phone", ""));
							query.findObjects(MainActivity.this,
									new FindListener<MyUser>() {
										@Override
										public void onSuccess(
												List<MyUser> object) {
											String huanxinPwd = object.get(0)
													.getHuanxinPwd();
											loginHuanxin(
													sp.getString("phone", ""),
													huanxinPwd);
										}

										@Override
										public void onError(int code, String msg) {

										}
									});
						}

					} else {
						if (NetUtils.hasNetwork(MainActivity.this))
							// 连接不到聊天服务器
							bundle.putString("buff", "连接不到聊天服务器");
						else
							// 当前网络不可用，请检查网络设置
							bundle.putString("buff", "检查网络设置");
					}
					msg.setData(bundle);
					myHandler.sendMessage(msg);
				}

			});
		}

	}

	// 获取所有控件
	private void init() {
		lin_zhuye = (LinearLayout) findViewById(R.id.main_dibu_menu_zhuye);
		lin_zhuye.setOnClickListener(new JianTing());
		iv_zhuye = (ImageView) findViewById(R.id.main_dibu_menu_zhuye_ig);
		tv_zhuye = (TextView) findViewById(R.id.main_dibu_menu_zhuye_tx);
		lin_jiaoliuquan = (LinearLayout) findViewById(R.id.main_dibu_menu_jiaoliuquan);
		lin_jiaoliuquan.setOnClickListener(new JianTing());
		iv_jiaoliuquan = (ImageView) findViewById(R.id.main_dibu_menu_jiaoliuquan_ig);
		tv_jiaoliuquan = (TextView) findViewById(R.id.main_dibu_menu_jiaoliuquan_tx);
		lin_gerenzhongxin = (LinearLayout) findViewById(R.id.main_dibu_menu_gerenzhongxin);
		lin_gerenzhongxin.setOnClickListener(new JianTing());
		iv_gerenzhongxin = (ImageView) findViewById(R.id.main_dibu_menu_gerenzhongxin_ig);
		tv_gerenzhongxin = (TextView) findViewById(R.id.main_dibu_menu_gerenzhongxin_tx);
		lin_qingdanguanli = (LinearLayout) findViewById(R.id.main_dibu_menu_qingdanguanli);
		lin_qingdanguanli.setOnClickListener(new JianTing());
		iv_qingdanguanli = (ImageView) findViewById(R.id.main_dibu_menu_qingdanguanli_ig);
		tv_qingdanguanli = (TextView) findViewById(R.id.main_dibu_menu_qingdanguanli_tx);

		msg = new Message();
		bundle = new Bundle();
		huanxin_bundle = new Bundle();
		huanxin_msg = new Message();
	}

	// 给Viewpager填充数据
	private void Mydata() {
		zhuye = new Zhuye();
		geren = new GeRenZhongXin();
		jiaoliuquan = new JiaoLiuQuan();
		qingdanguanli = new QingDanGuanLi();
		list_fragent = new ArrayList<Fragment>();
		myadapter = new MyFragmentAdapter(getSupportFragmentManager(),
				list_fragent);
		list_fragent.add(zhuye);
		list_fragent.add(qingdanguanli);
		list_fragent.add(jiaoliuquan);
		list_fragent.add(geren);
		view_pager.setAdapter(myadapter);
		text_yes = getResources().getColor(R.color.yes);
		text_no = getResources().getColor(R.color.no);
	}

	// 初始化显示图标
	private void Chushihua() {
		iv_zhuye.setImageResource(R.drawable.main_home_no);
		tv_zhuye.setTextColor(text_no);
		iv_jiaoliuquan.setImageResource(R.drawable.main_jiaoliuquan_no);
		tv_jiaoliuquan.setTextColor(text_no);
		iv_gerenzhongxin.setImageResource(R.drawable.main_gerenzhongxin_no);
		tv_gerenzhongxin.setTextColor(text_no);
		iv_qingdanguanli.setImageResource(R.drawable.main_qingdanguanli_no);
		tv_qingdanguanli.setTextColor(text_no);
	}

	// 监听按键 来显示相应的fragment
	class JianTing implements OnClickListener {

		@Override
		public void onClick(View v) {
			Chushihua();
			switch (v.getId()) {

			case R.id.main_dibu_menu_zhuye:
				view_pager.setCurrentItem(0, false);
				iv_zhuye.setImageResource(R.drawable.main_home_yes);
				tv_zhuye.setTextColor(text_yes);
				break;

			case R.id.main_dibu_menu_qingdanguanli:
				view_pager.setCurrentItem(1, false);
				iv_qingdanguanli
						.setImageResource(R.drawable.main_qingdanguanli_yes);
				tv_qingdanguanli.setTextColor(text_yes);
				break;

			case R.id.main_dibu_menu_jiaoliuquan:
				if (zidongdenglu == true) {
					loginpanduan();
				}
				view_pager.setCurrentItem(2, false);
				iv_jiaoliuquan
						.setImageResource(R.drawable.main_jiaoliuquan_yes);
				tv_jiaoliuquan.setTextColor(text_yes);
				break;

			case R.id.main_dibu_menu_gerenzhongxin:
				if (zidongdenglu == true) {
					loginpanduan();
				}
				view_pager.setCurrentItem(3, false);
				iv_gerenzhongxin
						.setImageResource(R.drawable.main_gerenzhongxin_yes);
				tv_gerenzhongxin.setTextColor(text_yes);
				break;

			}
		}

	}

	// 检测当前滑动到那个fragment 并显示出来
	class ViewPagerJianting implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			Chushihua();
			switch (arg0) {
			case 0:
				iv_zhuye.setImageResource(R.drawable.main_home_yes);
				tv_zhuye.setTextColor(text_yes);
				break;

			case 1:
				iv_qingdanguanli
						.setImageResource(R.drawable.main_qingdanguanli_yes);
				tv_qingdanguanli.setTextColor(text_yes);
				break;

			case 2:
				if (zidongdenglu == true) {
					loginpanduan();
				}
				iv_jiaoliuquan
						.setImageResource(R.drawable.main_jiaoliuquan_yes);
				tv_jiaoliuquan.setTextColor(text_yes);
				break;

			case 3:
				if (zidongdenglu == true) {
					loginpanduan();
				}
				iv_gerenzhongxin
						.setImageResource(R.drawable.main_gerenzhongxin_yes);
				tv_gerenzhongxin.setTextColor(text_yes);
				break;
			}
		}
	}

	// 登录判断
	public void loginpanduan() {
		// 若有缓存文件则自动登录bmob
		sp = getSharedPreferences("admin", MODE_PRIVATE);
		if (sp.getString("phone", "").length() != 0) {
			myphone = sp.getString("phone", "");
			mypassword = sp.getString("password", "");
			loginBmob();
		} else {
			tishikuang();
		}
	}

	AlertDialog aler;

	// 登录提示框
	private void tishikuang() {

		zidongdenglu = false;
		aler = new AlertDialog.Builder(this).create();
		aler.show();
		Window wid = aler.getWindow();
		aler.setCanceledOnTouchOutside(false);
		wid.setContentView(R.layout.tanchuang);
		wid.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置显示标题
		TextView tx_title = (TextView) wid.findViewById(R.id.tuichu_title);
		tx_title.setText(getResources().getString(R.string.main_tishidenglu));
		aler.findViewById(R.id.tuichu_yes).setOnClickListener(this);
		aler.findViewById(R.id.tuichu_no).setOnClickListener(this);

	}

	// 监听按钮
	@Override
	public void onClick(View p1) {
		switch (p1.getId()) {
		case R.id.tuichu_yes:
			startActivity(new Intent(this, DengluActivity.class));
			aler.dismiss();
			aler.cancel();
			break;
		case R.id.tuichu_no:
			aler.dismiss();
			aler.cancel();
			break;
		}
	}

	// 登录bmob
	private void loginBmob() {

		BmobQuery<MyUser> query = new BmobQuery<MyUser>();
		query.addWhereEqualTo("phone", myphone);
		query.findObjects(this, new FindListener<MyUser>() {
			@Override
			public void onSuccess(List<MyUser> object) {
				String password = object.get(0).getPassword();
				if (password.equals(mypassword)) {
					Toast.makeText(MainActivity.this, "自动登录成功", 0).show();
				} else {
					Toast.makeText(MainActivity.this, "密码或帐号不正确", 0).show();
				}
				zidongdenglu = false;
			}

			@Override
			public void onError(int code, String msg) {
				Toast.makeText(MainActivity.this, "没有找到此id", 0).show();
			}
		});

	}

	// 被清理缓存后重登环信
	private void loginHuanxin(String phone, String huanxinPwd) {
		EMClient.getInstance().login(phone, huanxinPwd, new EMCallBack() {// 回调
					@Override
					public void onSuccess() {
						runOnUiThread(new Runnable() {
							public void run() {
								EMClient.getInstance().groupManager()
										.loadAllGroups();
								EMClient.getInstance().chatManager()
										.loadAllConversations();
								Log.d("main", "登录聊天服务器成功！");
								Toast.makeText(MainActivity.this, "重登聊天服务器成功",
										0).show();
							}
						});
					}

					@Override
					public void onProgress(int progress, String status) {

					}

					@Override
					public void onError(int code, String message) {
						Log.d("main", "登录聊天服务器失败！");
						Toast.makeText(MainActivity.this, "重登聊天服务器失败！", 0)
								.show();
					}
				});

	}

	// 对加好友的监听
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAgreed(final String username) {
			// 好友请求被同意

			sp = getSharedPreferences("admin", MODE_PRIVATE);
			if (sp.getString("phone", "").length() != 0) {
				BmobQuery<MyUser> query = new BmobQuery<MyUser>();
				query.addWhereEqualTo("phone", sp.getString("phone", ""));
				query.findObjects(MainActivity.this,
						new FindListener<MyUser>() {
							@Override
							public void onSuccess(List<MyUser> object) {

								MyUser newUser = new MyUser();
								newUser.setFriendID(username);
								newUser.update(MainActivity.this, object.get(0)
										.getObjectId(), new UpdateListener() {
									@Override
									public void onSuccess() {
										huanxin_bundle.putString("buff", "好友请求被同意");
										huanxin_msg.setData(huanxin_bundle);
										myHandler.sendMessage(huanxin_msg);
									}

									@Override
									public void onFailure(int code, String msg) {

									}
								});

							}

							@Override
							public void onError(int code, String msg) {

							}
						});

			}

		}

		@Override
		public void onContactRefused(String username) {
			// 好友请求被拒绝
		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 收到好友邀请

			if(i==0){
				i++;
				Log.i("asd", "收到好友邀请"+username+reason);
				Bundle bundle = new Bundle();
				bundle.putString("name", username);
				bundle.putString("reason", reason);
				Intent intent = new Intent(MainActivity.this,
						addFriendActivity.class);
				intent.putExtra("object", bundle);
				startActivity(intent);			
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i=0;
			}
		}

		@Override
		public void onContactDeleted(String username) {
			// 被删除时回调此方法
		}

		@Override
		public void onContactAdded(String username) {
			// 增加了联系人时回调此方法
		}
	}

}
