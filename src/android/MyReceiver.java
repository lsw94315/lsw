package cn.jiguang.cordova.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jiguang.cordova.plugin.badge.BadgeImpl;

public class MyReceiver extends BroadcastReceiver {

    private static final List<String> IGNORED_EXTRAS_KEYS =
            Arrays.asList(
                    "cn.jpush.android.TITLE",
                    "cn.jpush.android.MESSAGE",
                    "cn.jpush.android.APPKEY",
                    "cn.jpush.android.NOTIFICATION_CONTENT_TITLE"
            );

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(JPushInterface.ACTION_REGISTRATION_ID)) {
            String rId = intent.getStringExtra(JPushInterface.EXTRA_REGISTRATION_ID);
            JPushPlugin.transmitReceiveRegistrationId(rId);
        } else if (action.equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            handlingMessageReceive(intent);
        } else if (action.equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            handlingNotificationReceive(context, intent);
        } else if (action.equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            handlingNotificationOpen(context, intent);
        }
    }

    private void handlingMessageReceive(Intent intent) {
        String msg = intent.getStringExtra(JPushInterface.EXTRA_MESSAGE);
        Map<String, Object> extras = getNotificationExtras(intent);
        JPushPlugin.transmitMessageReceive(msg, extras);
    }

    private void handlingNotificationOpen(Context context, Intent intent) {
        String title = intent.getStringExtra(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        JPushPlugin.openNotificationTitle = title;

        String alert = intent.getStringExtra(JPushInterface.EXTRA_ALERT);
        JPushPlugin.openNotificationAlert = alert;

        Map<String, Object> extras = getNotificationExtras(intent);
        JPushPlugin.openNotificationExtras = extras;

        JPushPlugin.transmitNotificationOpen(title, alert, extras);

        Intent launch = context.getPackageManager().getLaunchIntentForPackage(
                context.getPackageName());
        launch.addCategory(Intent.CATEGORY_LAUNCHER);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(launch);
    }

    private void handlingNotificationReceive(Context context, Intent intent) {
        Intent launch = context.getPackageManager().getLaunchIntentForPackage(
                context.getPackageName());
        launch.addCategory(Intent.CATEGORY_LAUNCHER);
        launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String title = intent.getStringExtra(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        JPushPlugin.notificationTitle = title;

        String alert = intent.getStringExtra(JPushInterface.EXTRA_ALERT);
        JPushPlugin.notificationAlert = alert;

        Map<String, Object> extras = getNotificationExtras(intent);
        JPushPlugin.notificationExtras = extras;

        JPushPlugin.transmitNotificationReceive(title, alert, extras);

        if (extras.containsKey(JPushInterface.EXTRA_EXTRA)) {
            try {
                JSONObject json = new JSONObject(extras.get(JPushInterface.EXTRA_EXTRA) + "");
                BadgeImpl impl = new BadgeImpl(context);
                if (json.has("extra_badge")) {
                    String num = json.get("extra_badge") + "";
                    if (num.isEmpty()) {
                        impl.clearBadge();
                    } else if (num.equals("+1")) {
                        impl.autoBadge();
                    } else {
                        impl.setBadge(Integer.parseInt(num));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<String, Object> getNotificationExtras(Intent intent) {
        Map<String, Object> extrasMap = new HashMap<String, Object>();
        for (String key : intent.getExtras().keySet()) {
            if (!IGNORED_EXTRAS_KEYS.contains(key)) {
                if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                    extrasMap.put(key, intent.getIntExtra(key, 0));
                } else {
                    extrasMap.put(key, intent.getStringExtra(key));
                }
            }
        }
        return extrasMap;
    }
}
