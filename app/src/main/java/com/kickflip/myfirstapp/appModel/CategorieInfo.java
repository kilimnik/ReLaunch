package com.kickflip.myfirstapp.appModel;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kickflip.myfirstapp.appModel.AppInfo;
import com.kickflip.myfirstapp.settings.MyActivity;

import java.util.ArrayList;
import java.util.List;

public class CategorieInfo {

    private String name;

    private int image;

    private List<String> packages;

    public CategorieInfo(String name, int image) {
        this.name = name;
        this.image = image;

        packages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public List<String> getPackages() {
        return packages;
    }

    public List<AppInfo> getAppList(){
        List<AppInfo> appInfos = new ArrayList<>();

        for (String p: packages){
            for (AppInfo info: MyActivity.getInfo().getApplist()){
                if (p.equals(info.getPname())){
                    appInfos.add(info);
                    break;
                }
            }
        }

        return appInfos;
    }
}
