package iq.mazen.basmati;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.time.*;
import java.time.format.*;

public class MainActivity extends Activity {
 LinearLayout root; final int NAVY=Color.rgb(19,43,70), GREEN=Color.rgb(13,128,91);
 TextView text(String s,int sp){TextView t=new TextView(this);t.setText(s);t.setTextSize(sp);t.setTextColor(NAVY);t.setPadding(12,12,12,12);return t;}
 void add(String s,int size){root.addView(text(s,size));}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(NAVY);show();Schedule.schedule(this);if(Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},55);}
 @Override public void onResume(){super.onResume();Schedule.schedule(this);}
 void show(){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(18,20,18,30);root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);root.setBackgroundColor(Color.rgb(246,249,252));sc.addView(root);setContentView(sc);
 TextView heading=text("بصمتي  ⏰",32);heading.setTextColor(GREEN);root.addView(heading);add("مازن | المعدات الدوارة – مصافي الشمال",17);
 LocalDate today=LocalDate.now();String[] statuses={"دوام – اليوم الأول","دوام – اليوم الثاني","بصمة خروج 09:30","استراحة","استراحة","استراحة","استراحة"};
 add("اليوم: "+today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))+"  |  "+statuses[Schedule.dayType(today)],20);
 add("جدول الأيام الأربعة عشر القادمة",21);
 DateTimeFormatter fmt=DateTimeFormatter.ofPattern("EEEE dd/MM",new java.util.Locale("ar"));for(int n=0;n<14;n++){LocalDate d=today.plusDays(n);TextView t=text(d.format(fmt)+"  •  "+statuses[Schedule.dayType(d)],15);if(Schedule.dayType(d)<3)t.setTextColor(GREEN);root.addView(t);}
 add("إعدادات المنبّهات (قابلة للتعديل)",22);
 for(int i=0;i<4;i++)addAlarm(i);
 if(Build.VERSION.SDK_INT>=31){AlarmManager am=getSystemService(AlarmManager.class);if(!am.canScheduleExactAlarms()){Button exact=new Button(this);exact.setText("السماح بالمنبّهات الدقيقة (مهم)");root.addView(exact);exact.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,android.net.Uri.parse("package:"+getPackageName())));}catch(Exception e){Toast.makeText(this,"افتح إعدادات المنبّهات للتطبيق",Toast.LENGTH_LONG).show();}});}}
 add("ملاحظة: امنح التطبيق إذن الإشعارات والمنبّهات الدقيقة، وأوقف تقييد البطارية عند الحاجة. هذه التنبيهات ليست بديلاً عن التأكد من وقت البصمة.",13);
 }
 void addAlarm(int i){LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(10,10,10,10);card.setBackgroundColor(Color.WHITE);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,8,0,8);root.addView(card,lp);
 Switch sw=new Switch(this);sw.setText(Schedule.NAMES[i]);sw.setTextSize(18);sw.setChecked(Schedule.enabled(this,i));card.addView(sw);
 Button time=new Button(this);time.setText(String.format(java.util.Locale.US,"موعد البصمة: %02d:%02d",Schedule.hour(this,i),Schedule.minute(this,i)));card.addView(time);
 time.setOnClickListener(v->{TimePickerDialog dialog=new TimePickerDialog(this,(picker,h,m)->{Schedule.prefs(this).edit().putInt("h"+i,h).putInt("m"+i,m).apply();Schedule.schedule(this);show();},Schedule.hour(this,i),Schedule.minute(this,i),true);dialog.show();});
 TextView early=text("التنبيه قبل البصمة بـ "+Schedule.offset(this,i)+" دقيقة",15);card.addView(early);
 SeekBar seek=new SeekBar(this);seek.setMax(120);seek.setProgress(Schedule.offset(this,i));card.addView(seek);seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean user){early.setText("التنبيه قبل البصمة بـ "+p+" دقيقة");}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){Schedule.prefs(MainActivity.this).edit().putInt("offset"+i,s.getProgress()).apply();Schedule.schedule(MainActivity.this);}});
 sw.setOnCheckedChangeListener((button,checked)->{Schedule.prefs(this).edit().putBoolean("on"+i,checked).apply();Schedule.schedule(this);});
 }
}
