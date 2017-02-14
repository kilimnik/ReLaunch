package com.kickflip.myfirstapp.floating;


import java.util.ArrayList;
import java.util.List;

public class CategorieInfo {
    private String name;
    private int image;
    private List<AppInfo> applicationInfos;

    public CategorieInfo(String name, int image) {
        this.name = name;
        this.image = image;

        applicationInfos = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public List<AppInfo> getApplicationInfos() {
        return applicationInfos;
    }
}
