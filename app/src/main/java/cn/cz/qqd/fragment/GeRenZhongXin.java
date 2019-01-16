package cn.cz.qqd.fragment;

import android.support.v4.app.*;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hyphenate.chat.EMClient;


import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import cn.bmob.v3.BmobQuery;

import cn.bmob.v3.listener.FindListener;

import cn.cz.data.MyUser;
import cn.cz.qqd.*;
import cn.cz.qqd.ui.MyImageView;

public class GeRenZhongXin extends Fragment implements OnClickListener {

	TextView tx_yonghuming, tx_dengjitishi, tx_dengji, tx_qiandaotianshu,
			tx_qiandaojifen, tx_jifen, tx_ruhehuodejifen, tx_chakanjifen;
	MyImageView ig_touxiang;
	Button bt_qiandao;
	ImageView ig_shezhi;
	ProgressBar pb_dengjijindu;
	ListView lv_liebiao;
	SimpleAdapter sm_liebiao;
	List<Map<String, Object>> liebiao_list;
	View view;

	SharedPreferences.Editor sp_ed;
	SharedPreferences sp;
	// 资源
	int liebiao_imageid[] = { R.drawable.haoyoudongtai_ig,
			R.drawable.haoyoupaiming_ig, R.drawable.yiwancheng_ig,
			R.drawable.weiwancheng_ig, R.drawable.xiugaimima_ig,
			R.drawable.tuichu_ig };
	String liebiao_text[] = { "好友动态", "好友排行", "已完成任务", "未完成任务", "修改密码", "退出" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.gerenzhongxin, container, false);
		init();
		Data();
		lv_liebiao
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						switch (position) {
						case 0:
							break;
						case 1:
							break;
						case 2:
							break;
						case 3:
							break;
						case 4:
							updatePwd();
							break;
						case 5:
							tishikuang();
							break;

						default:
							break;
						}

					}
				});
		return view;
	}

	// 获取控件
	private void init() {
		tx_yonghuming = (TextView) view
				.findViewById(R.id.gerenzhongxin_tx_yonghuming);
		tx_dengji = (TextView) view.findViewById(R.id.gerenzhongxin_tx_dengji);
		tx_dengjitishi = (TextView) view
				.findViewById(R.id.gerenzhongxin_tx_dengjitishi);
		tx_qiandaojifen = (TextView) view
				.findViewById(R.id.gerenzhongxin_tx_qiandaojifen);
		tx_qiandaotianshu = (TextView) view
				.findViewById(R.id.gerenzhongxin_tx_qiandaotianshu);
		tx_jifen = (TextView) view.findViewById(R.id.gerenzhongxin_tx_jifen);
		tx_ruhehuodejifen = (TextView) view
				.findViewById(R.id.gerenzhongxin_tx_ruhehuodejifen);
		tx_chakanjifen = (TextView) view
				.findViewById(R.id.gerenzhongxin_tx_chakanjifen);
		ig_touxiang = (MyImageView) view
				.findViewById(R.id.gerenzhongxin_ig_touxiang);
		ig_touxiang.setOnClickListener(this);
		bt_qiandao = (Button) view.findViewById(R.id.gerenzhongxin_bt_qiandao);
		ig_shezhi = (ImageView) view.findViewById(R.id.gerenzhongxin_ig_shezhi);
		pb_dengjijindu = (ProgressBar) view
				.findViewById(R.id.gerenzhongxin_pb_dengjijindu);
		lv_liebiao = (ListView) view
				.findViewById(R.id.gerenzhongxin_lv_liebiao);
	}

	// 给控件添加值
	private void Data() {
		sm_liebiao = new SimpleAdapter(getActivity(), getData(),
				R.layout.gerenzhongxin_liebiao, new String[] { "ig", "tx" },
				new int[] { R.id.gerenzhongxin_liebiao_ig,
						R.id.gerenzhongxin_liebiao_tx });
		lv_liebiao.setAdapter(sm_liebiao);

		// 判断是否已经登录帐号，并且把数据取出
		sp = getContext().getSharedPreferences("admin",
				getContext().MODE_PRIVATE);
		if (sp.getString("phone", "").length() != 0) {
			BmobQuery<MyUser> query = new BmobQuery<MyUser>();
			query.addWhereEqualTo("phone", sp.getString("phone", ""));
			query.findObjects(getActivity(), new FindListener<MyUser>() {
				@Override
				public void onSuccess(List<MyUser> object) {
					// TODO Auto-generated method stub
					String nikeName = object.get(0).getNickName();
					// Integer rank = object.get(0).getRank();
					// Integer rankfen = object.get(0).getRankfen();
					// Integer integral = object.get(0).getIntegral();
					tx_yonghuming.setText(nikeName);
					// tx_jifen.setText("等级:"+integral);
					// tx_dengji.setText("LV "+rank);
					// pb_dengjijindu.setProgress(rankfen);
				}

				@Override
				public void onError(int code, String msg) {
					// TODO Auto-generated method stub

				}
			});
		} else {
			tx_yonghuming.setOnClickListener(this);
		}
	}

	private List<Map<String, Object>> getData() {
		liebiao_list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < liebiao_imageid.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ig", liebiao_imageid[i]);
			map.put("tx", liebiao_text[i]);
			liebiao_list.add(map);
		}
		return liebiao_list;
	}

	android.app.Dialog tuichu;

	// 退出提示框
	private void tishikuang() {
		AlertDialog.Builder aler = new AlertDialog.Builder(getContext());
		tuichu = aler.show();
		Window wid = tuichu.getWindow();
		wid.setContentView(R.layout.tanchuang);
		wid.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		// 设置显示标题
		TextView tx_title = (TextView) wid.findViewById(R.id.tuichu_title);
		tx_title.setText(getResources().getString(R.string.main_tishituichu));
		tuichu.findViewById(R.id.tuichu_yes).setOnClickListener(this);
		tuichu.findViewById(R.id.tuichu_no).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tuichu_yes:
			sp = getContext().getSharedPreferences("admin",
					getContext().MODE_PRIVATE);
			sp_ed = sp.edit();
			if (sp.getString("phone", "").length() != 0) {
				sp_ed.putString("phone", "");
				sp_ed.putString("password", "");
				sp_ed.putString("huanxinPwd", "");
				sp_ed.commit();
				EMClient.getInstance().logout(true);
			}
			startActivity(new Intent(getContext(), DengluActivity.class));
			tuichu.dismiss();
			getActivity().finish();
			break;
		case R.id.tuichu_no:
			tuichu.dismiss();
			break;
		case R.id.gerenzhongxin_tx_yonghuming:
			login();
			break;
		case R.id.gerenzhongxin_ig_touxiang:
			updateUserXinxi();
			break;
		}
	}

	// 更新用户信息
	private void updateUserXinxi() {
		// TODO Auto-generated method stub
		startActivity(new Intent(getContext(), UserXinxiActivity.class));
	}

	// 登录或者显示用户名
	private void login() {
		// TODO Auto-generated method stub
		startActivity(new Intent(getContext(), DengluActivity.class));
	}

	// 修改密码
	public void updatePwd() {
		startActivity(new Intent(getContext(), UpdatePwdActivity.class));
	}
}
