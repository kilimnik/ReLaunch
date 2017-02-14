package com.kickflip.myfirstapp.floating;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kickflip.myfirstapp.floating.CategorieInfo;

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