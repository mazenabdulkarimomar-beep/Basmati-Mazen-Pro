package iq.mazen.basmati;
import android.content.*;
public class BootReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c, Intent i) {
  if (i != null) Schedule.schedule(c);
 }
}
