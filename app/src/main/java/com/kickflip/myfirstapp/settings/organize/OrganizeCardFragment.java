package com.kickflip.myfirstapp.settings.organize;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.kickflip.myfirstapp.R;
import com.kickflip.myfirstapp.floating.AppInfo;
import com.kickflip.myfirstapp.settings.MyActivity;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OrganizeCardFragment extends Fragment {
    private List<MyApplicationInfo> allApps;
    private String title;
    private Drawable icon;
    private String fileName;

    public OrganizeCardFragment(List<AppInfo> apps, String title, Drawable icon, String fileName) {
        allApps = new ArrayList<>();

        for (AppInfo info:apps){
            this.allApps.add(new MyApplicationInfo(info));
        }
        this.title = title;
        this.icon = icon;
        this.fileName = fileName;
    }

    public OrganizeCardFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MyActivity.setBackPressed(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.organize_card_fragment,container, false);

        EditText editText = (EditText) view.findViewById(R.id.organize_card_title);
        editText.setText(title);

        ImageView imageView = (ImageView) view.findViewById(R.id.organize_card_icon);
        imageView.setImageDrawable(icon);

        GridView gridView = (GridView) view.findViewById(R.id.organize_card_grid);
        gridView.setAdapter(new Adapter());

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        String string = "";
        for (MyApplicationInfo app:allApps){
            string = string + app.getInfo().getAppname() + ";";
        }

        FileOutputStream outputStream;

        try {
            outputStream = getActivity().openFileOutput(fileName, Context.MODE_APPEND);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyApplicationInfo{
        private AppInfo info;
        private boolean enabled;

        public MyApplicationInfo(AppInfo info) {
            this.info = info;
            this.enabled = false;
        }

        public AppInfo getInfo() {
            return info;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return allApps.size();
        }

        @Override
        public Object getItem(int position) {
            return allApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Button button = new Button(getActivity());
            button.setBackground(allApps.get(position).getInfo().getIcon());

            button.setLayoutParams(new ViewGroup.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));

            return button;
        }
    }
}
