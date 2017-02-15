package com.kickflip.myfirstapp.floating;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kickflip.myfirstapp.appModel.CategorieInfo;

import java.util.List;

public class Categories extends LinearLayout {

    private Context context;

    public Categories(Context context, List<CategorieInfo> categorieInfos) {
        super(context);

        this.context = context;

        setOrientation(VERTICAL);

        setCategories(categorieInfos);
    }

    public void setCategories(List<CategorieInfo> categorieInfos){
        removeAllViews();

        for (CategorieInfo info:categorieInfos){
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(info.getImage());

            addView(imageView);
        }
    }
}