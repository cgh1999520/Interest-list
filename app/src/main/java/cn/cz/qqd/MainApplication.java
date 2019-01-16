package cn.cz.qqd;

import java.util.Iterator;
import java.util.List;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.smssdk.SMSSDK;

public class MainApplication extends Application {

	public Context appContext;

	@Override
	public void onCreate() {
		super.onCreate();
		// 启动bmobSdk
		Bmob.initialize(getApplicationContext(),
				"eb2555f2240bbfb5c54d85b61dc91995");
		// 使用推送服务时的初始化操作
		BmobInstallation.getCurrentInstallation(getApplicationContext()).save();
		// 启动推送服务
		BmobPush.startWork(getApplicationContext(),
				"eb2555f2240bbfb5c54d85b61dc91995");
		// 启动短信验证sdk
		SMSSDK.initSDK(getApplicationContext(), "11b97b42d31fc",
				"0fce6f02a261fded299265f624903439");

		// 环信的东东
		EMOptions options = new EMOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证	
		options.setAcceptInvitationAlways(false);
		//环信 自带聊天界面 
		EaseUI.getInstance().init(getApplicationContext(), options);
		
		
		appContext = this;
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果APP启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process
		// name就立即返回

		if (processAppName == null
				|| !processAppName
						.equalsIgnoreCase(appContext.getPackageName())) {
			Log.e("huanxin", "enter the service process!");

			// 则此application::onCreate 是被service 调用的，直接返回
			return;
		}

		// 初始化
		EMClient.getInstance().init(getApplicationContext(), options);
		// 在做打包混淆时，关闭debug模式，避免消耗不必要的资源
		EMClient.getInstance().setDebugMode(true);
	
	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this
				.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
					.next());
			try {
				if (info.pid == pID) {
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
			}
		}
		return processName;
	}
}
