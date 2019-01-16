package cn.cz.qqd.adapter;

import android.content.Context;
import android.support.v4.app.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cz.qqd.R;

import java.util.*;

public class MyFragmentAdapter extends FragmentPagerAdapter{

	List<Fragment> list_fragment;

	public MyFragmentAdapter(FragmentManager fm,List<Fragment> list_fragment){
		super(fm);
		this.list_fragment = list_fragment;
	}
	
	@Override
	public Fragment getItem(int p1){
		return list_fragment.get(p1);
	}

	@Override
	public int getCount(){
		return list_fragment.size();
	}
}
