package com.kickflip.myfirstapp;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Categories extends LinearLayout {

    public Categories(Context context, CategorieInfo[] categorieInfos) {
        super(context);

        setOrientation(VERTICAL);

        for (CategorieInfo info:categorieInfos){
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(info.getImage());

            addView(imageView);
        }
    }
}