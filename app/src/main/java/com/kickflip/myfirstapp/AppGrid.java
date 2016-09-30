package com.kickflip.myfirstapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class AppGrid extends GridView{
    private List<ApplicationInfo> applicationInfos;

    public AppGrid(Context context, List<ApplicationInfo> applicationInfos) {
        super(context);

        this.applicationInfos = applicationInfos;

        setNumColumns(GridView.AUTO_FIT);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setColumnWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()));


        Adapter adapter = new Adapter();
        setAdapter(adapter);
    }

    private class Adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return applicationInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return applicationInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(applicationInfos.get(position).loadIcon(getContext().getPackageManager()));

            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));

            return imageView;
        }
    }
}
