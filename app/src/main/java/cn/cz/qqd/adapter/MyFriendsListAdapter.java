package cn.cz.qqd.adapter;

import java.util.List;

import cn.cz.qqd.R;
import cn.cz.qqd.ui.MyImageView;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyFriendsListAdapter extends BaseAdapter {

	List<String> usernames;
	Context context;
	public MyFriendsListAdapter(List<String> usernames, Context context){
		this.usernames = usernames;
		this.context = context;
	}
	@Override
	public int getCount() {
		return usernames.size();
	}

	@Override
	public Object getItem(int position) {
		return usernames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = View.inflate(context,R.layout.friends_list,null);
		ImageView ig = (ImageView) convertView.findViewById(R.id.friends_touxiang);
		TextView tv_nickname = (TextView) convertView.findViewById(R.id.friends_nickname);
		TextView tv_gexinqianming = (TextView) convertView.findViewById(R.id.friends_gexinqianming);
		tv_nickname.setText((String)getItem(position));
		tv_gexinqianming.setText("啊撒旦法撒旦法是的ad所发生的点点滴滴滴滴sad撒旦发射点发来就看见");
		return convertView;
	}

}
