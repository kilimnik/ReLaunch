package com.kickflip.myfirstapp.settings;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.kickflip.myfirstapp.R;
import com.kickflip.myfirstapp.floating.AppInfo;
import com.kickflip.myfirstapp.settings.look.LookFeelFragment;
import com.kickflip.myfirstapp.settings.organize.OrganizeFragment;
import com.kickflip.myfirstapp.settings.properties.PropertiesFragment;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    private static GridView gridView;

    private PackageManager packageManager;
    private static List<AppInfo> applist;

    private Toolbar toolbar;

    private IconSizeReciever receiver;

    private NavigationView navigationView;

    public static String STARTFOREGROUND_ACTION = "com.kickflip.float.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.kickflip.float.action.stopforeground";

    private static ActionBarDrawerToggle toggle;

    private static boolean backPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PropertiesFragment()).commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);

        toggle.setDrawerIndicatorEnabled(true);

        toggle.syncState();

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!backPressed){
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                }else {
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrganizeFragment()).commit();
                    backPressed = false;
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.setDrawerIndicatorEnabled(true);
                }
            }
        });



        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_properties);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)){
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                }
            }
        }

        gridView = new GridView(this);
        gridView.setNumColumns(GridView.AUTO_FIT);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setColumnWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));

        IntentFilter filter = new IntentFilter(PropertiesFragment.PROPERTIES_ACTION + ".icon_size");

        receiver = new IconSizeReciever();
        registerReceiver(receiver, filter);

        packageManager = getPackageManager();
        List<PackageInfo> apps = getPackageManager().getInstalledPackages(0);

        applist = new ArrayList<AppInfo>();

        for(int i=0;i<apps.size();i++) {
            PackageInfo p = apps.get(i);

            AppInfo newInfo = new AppInfo();
            newInfo.setAppname(p.applicationInfo.loadLabel(getPackageManager()).toString());
            newInfo.setPname(p.packageName);
            newInfo.setVersionName(p.versionName);
            newInfo.setVersionCode(p.versionCode);
            newInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
            applist.add(newInfo);
        }
    }

    public static void setBackPressed(boolean backPressed) {
        MyActivity.backPressed = backPressed;
        toggle.setDrawerIndicatorEnabled(!backPressed);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (backPressed){
            backPressed = false;
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_properties) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new PropertiesFragment()).commit();
        } else if (id == R.id.nav_look_feel) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new LookFeelFragment()).commit();
        } else if (id == R.id.nav_organize) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrganizeFragment()).commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static List<AppInfo> getApplist() {
        return applist;
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {

        ArrayList<ApplicationInfo> appList = new ArrayList<ApplicationInfo>();

        for(ApplicationInfo info : list) {
            try{
                if(packageManager.getLaunchIntentForPackage(info.packageName) != null) {
                    appList.add(info);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return appList;
    }

    private class IconSizeReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PropertiesFragment.PROPERTIES_ACTION + ".icon_size")){
                gridView = new GridView(context);
                gridView.setNumColumns(GridView.AUTO_FIT);
                gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                gridView.setColumnWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, intent.getIntExtra("value", 40), getResources().getDisplayMetrics()));

                //new LoadApplications(intent.getIntExtra("value", 40)).execute();
            }
        }
    }
}
