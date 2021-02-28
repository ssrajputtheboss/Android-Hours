package com.example.recyclerview;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.job.JobInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    String[] tspent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.list);
        if(isReadStoragePermissionGranted()) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            String[] packageNames = getPackageNames();
            recyclerView.setAdapter(new MyAdapter(tspent, packageNames, getApplicationContext()));
        }

    }

    public  boolean isReadStoragePermissionGranted() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {

               Toast.makeText(getApplicationContext(),"Permission Not Granted",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else
            return true;

    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  String[] getPackageNames(){
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        String[] appNames = new String[packages.size()];
        tspent = new String[packages.size()];
        int i=0;
        for(ApplicationInfo packageInfo:packages){
            if( pm.getLaunchIntentForPackage(packageInfo.packageName) != null ){

                if(!packageInfo.loadLabel(pm).toString().contains("."))
                {
                    appNames[i] = packageInfo.packageName;
                    tspent[i]=getTimeString(packageInfo.packageName);
                    ++i;
                    //This app is a non-system app
                }
            }
        }


    return  appNames;

    }


    @SuppressLint({"WrongConstant", "NewApi"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getTimeString(String pack){


        UsageStatsManager usageStatsManager =null;
        usageStatsManager=(UsageStatsManager) getSystemService("usagestats");
        long TimeInForeground=500;
        int minutes=500,hour=500;
        //long currenttime = System.currentTimeMillis();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.HOUR_OF_DAY, -2);

        Calendar endCalendar = Calendar.getInstance();
        List<UsageStats> stats = null;
        stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,startCalendar.getTimeInMillis(),endCalendar.getTimeInMillis());
        if(stats == null || stats.isEmpty())
        {startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),1000);return  null;}
        else {

            for(UsageStats usageStats : stats){
                TimeInForeground =usageStats.getTotalTimeInForeground();
                minutes = (int) ((TimeInForeground/(1000*60))%60);
                hour = (int) ((TimeInForeground/1000*60*60)%24);
                if(usageStats.getPackageName().toLowerCase().equals(pack))
                {
                    return ""+hour+"H"+minutes+"M";
                }
            }
            return "0H0M";
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==1000){
            if(resultCode== AppOpsManager.MODE_ALLOWED)
            {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        else{
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
