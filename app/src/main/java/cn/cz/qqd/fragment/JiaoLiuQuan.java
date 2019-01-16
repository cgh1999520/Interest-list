package cn.cz.qqd.fragment;

import android.support.v4.app.*;
import android.view.*;
import android.os.*;
import cn.cz.qqd.*;
import cn.cz.qqd.adapter.MyFragmentAdapter;
import android.support.v4.view.*;
import java.util.*;
import android.view.View.*;
import android.widget.*;


public class JiaoLiuQuan extends Fragment implements OnClickListener{

	RadioButton rb_lianxiren,rb_haoyoubiping;
	JiaoLiuQuan_HaoYouBiPing haoyoubiping;
	JiaoLiuQuan_LianXiRen lianxiren;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.jiaoliuquan, container, false);
		rb_haoyoubiping =  (RadioButton) view.findViewById(R.id.jiaoliuquan_rb_haoyoubiping);
		rb_haoyoubiping.setOnClickListener(this);
		rb_lianxiren =  (RadioButton) view.findViewById(R.id.jiaoliuquan_rb_lianxiren);
		rb_lianxiren.setOnClickListener(this);
		
		lianxiren = new JiaoLiuQuan_LianXiRen();
		haoyoubiping = new JiaoLiuQuan_HaoYouBiPing();
		
		getFragmentManager().beginTransaction().replace(R.id.jiaoliuquan, lianxiren).commit();
		
		return view;
	}
	@Override
	public void onClick(View p1){
		if(p1.getId()==R.id.jiaoliuquan_rb_lianxiren){
			getFragmentManager().beginTransaction().replace(R.id.jiaoliuquan, lianxiren).commit();
		}else{
			getFragmentManager().beginTransaction().replace(R.id.jiaoliuquan, haoyoubiping).commit();
		}	}
}
