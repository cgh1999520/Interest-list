package cn.cz.data;

import android.os.*;
import android.widget.*;
import java.util.*;

public class GenXinUi implements Runnable{
	
	TextView tx_timer;
	boolean start=false;
	int timer=1;

	public GenXinUi(TextView tx_timer, boolean start){
		this.tx_timer = tx_timer;
		this.start = start;
	}
	
	@Override
	public void run(){
		while(start){
			try{
			Thread.sleep(1000);
			timer++;
				han.sendEmptyMessage(0);
			} catch(Exception o){}
		}
	}
	
	Handler han = new Handler(){

		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			tx_timer.setText(timer+"S");
		}
	};
}
