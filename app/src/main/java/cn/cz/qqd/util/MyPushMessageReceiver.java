package cn.cz.qqd.util;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.cz.qqd.R;
import android.app.Notification;
import android.app.NotificationManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class MyPushMessageReceiver extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			String tuisong = intent
					.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
			
			try {
				JSONObject jsonObject = new JSONObject(tuisong);
				String title = jsonObject.getString("title" + "");
				String neirong = jsonObject.getString("neirong" + "");
				String xiaoxi = jsonObject.getString("xiaoxi" + "");

				Builder builder = new NotificationCompat.Builder(context);
				builder.setSmallIcon(R.drawable.ic);
				builder.setContentTitle(title);
				builder.setContentText(neirong);
				builder.setTicker(xiaoxi);
				builder.setWhen(System.currentTimeMillis());
				builder.setDefaults(Notification.DEFAULT_VIBRATE);
				Notification notification = builder.build();
				NotificationManager manager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				manager.notify(0, notification);
			} catch (JSONException e) {
			}

		}
	}
}
