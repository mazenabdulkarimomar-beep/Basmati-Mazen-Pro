package iq.mazen.basmati;
import android.app.*;
import android.content.*;
import android.media.RingtoneManager;
import android.os.*;

public class AlarmReceiver extends android.content.BroadcastReceiver {
 @Override public void onReceive(Context c,Intent in){int i=in.getIntExtra("index",0);if(i<0||i>3)return;
  Schedule.channel(c);
  Intent launch=new Intent(c,MainActivity.class);PendingIntent click=PendingIntent.getActivity(c,200+i,launch,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
  Notification.Builder b=new Notification.Builder(c,Schedule.CHANNEL).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("بصمتي - موعد البصمة").setContentText(Schedule.NAMES[i]+" - حان موعد التنبيه").setAutoCancel(true).setContentIntent(click).setCategory(Notification.CATEGORY_ALARM).setVisibility(Notification.VISIBILITY_PUBLIC);
  if(Build.VERSION.SDK_INT<33||c.checkSelfPermission("android.permission.POST_NOTIFICATIONS")==android.content.pm.PackageManager.PERMISSION_GRANTED)c.getSystemService(NotificationManager.class).notify(100+i,b.build());
  Schedule.schedule(c);
 }
}
