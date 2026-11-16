package iq.mazen.basmati;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.provider.Settings;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Schedule {
 static final String CHANNEL="fingerprints";
 static final String[] NAMES={"البصمة الصباحية","البصمة المسائية","البصمة الليلية","بصمة الخروج"};
 static final int[] HOURS={6,16,21,9}, MINUTES={0,0,0,30};
 static final LocalDate START=LocalDate.of(2026,11,19);
 static int dayType(LocalDate d){return (int)Math.floorMod(ChronoUnit.DAYS.between(START,d),7);}
 static boolean active(int alarm, LocalDate d){int day=dayType(d);return alarm==3?day==2:day==0||day==1;}
 static android.content.SharedPreferences prefs(Context c){return c.getSharedPreferences("basmati",Context.MODE_PRIVATE);}
 static int hour(Context c,int i){return prefs(c).getInt("h"+i,HOURS[i]);}
 static int minute(Context c,int i){return prefs(c).getInt("m"+i,MINUTES[i]);}
 static boolean enabled(Context c,int i){return prefs(c).getBoolean("on"+i,true);}
 static int offset(Context c,int i){return prefs(c).getInt("offset"+i,0);}
 static void channel(Context c){if(Build.VERSION.SDK_INT>=26){NotificationManager n=c.getSystemService(NotificationManager.class);NotificationChannel ch=new NotificationChannel(CHANNEL,"تنبيهات البصمة",NotificationManager.IMPORTANCE_HIGH);ch.enableVibration(true);ch.setDescription("مواعيد بصمات مناوبة مازن");n.createNotificationChannel(ch);}}
 static PendingIntent intent(Context c,int i,int request){Intent in=new Intent(c,AlarmReceiver.class).putExtra("index",i);return PendingIntent.getBroadcast(c,request,in,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);}
 static void schedule(Context c){channel(c);AlarmManager manager=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);ZonedDateTime now=ZonedDateTime.now();for(int i=0;i<4;i++){
  PendingIntent pi=intent(c,i,100+i);manager.cancel(pi);if(!enabled(c,i))continue;
  ZonedDateTime next=null;
  for(int delta=0;delta<16;delta++){LocalDate date=now.toLocalDate().plusDays(delta);if(!active(i,date))continue;
   ZonedDateTime candidate=ZonedDateTime.of(date,LocalTime.of(hour(c,i),minute(c,i)),ZoneId.systemDefault()).minusMinutes(offset(c,i));
   if(candidate.isAfter(now)){next=candidate;break;}}
  if(next==null)continue;
  long when=next.toInstant().toEpochMilli();
  try {if(Build.VERSION.SDK_INT>=31&&!manager.canScheduleExactAlarms())manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi);else manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi);}catch(SecurityException e){manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when,pi);}
 }}
}
