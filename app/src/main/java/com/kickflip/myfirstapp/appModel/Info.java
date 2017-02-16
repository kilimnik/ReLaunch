package com.kickflip.myfirstapp.appModel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kickflip.myfirstapp.R;
import com.kickflip.myfirstapp.settings.MyActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Info {
    private List<AppInfo> applist;

    private int iconSize;

    private List<CategorieInfo> categorieInfos;

    public Info(PackageManager packageManager, Activity activity){
        this.applist = new ArrayList<>();

        List<PackageInfo> apps = packageManager.getInstalledPackages(0);

        for(int i=0;i<apps.size();i++) {
            PackageInfo p = apps.get(i);

            AppInfo newInfo = new AppInfo();
            newInfo.setAppname(p.applicationInfo.loadLabel(packageManager).toString());
            newInfo.setPname(p.packageName);
            newInfo.setVersionName(p.versionName);
            newInfo.setVersionCode(p.versionCode);
            newInfo.setIcon(p.applicationInfo.loadIcon(packageManager));
            applist.add(newInfo);
        }


        this.iconSize = 40;


        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("categories", "");
        Type type = new TypeToken<List<CategorieInfo>>() {}.getType();

        categorieInfos = gson.fromJson(json, type);

        if (categorieInfos == null){
            categorieInfos = new ArrayList<>();
        }

    }

    public void newCategorie(String name, int image){
        CategorieInfo categorieInfo = new CategorieInfo(name, image);

        List<String> packages = categorieInfo.getPackages();

        List<AppInfo> applicationInfosAll = applist;

        Random random = new Random();

        for (int i = 0; i < 10; i++){
            packages.add(applicationInfosAll.get(random.nextInt(applicationInfosAll.size())).getPname());
        }

        categorieInfos.add(categorieInfo);
    }

    public CategorieInfo getCategorieInfo(String title){
        for (CategorieInfo info: categorieInfos){
            if (info.getName().equals(title)){
                return info;
            }
        }

        return null;
    }

    public List<AppInfo> getApplist() {
        return applist;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public int getIconSize() {
        return iconSize;
    }

    public List<CategorieInfo> getCategorieInfos() {
        return categorieInfos;
    }
}
