package com.kickflip.myfirstapp;

public class DrawerItem {
    private String title;
    private int icon;

    public DrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}
