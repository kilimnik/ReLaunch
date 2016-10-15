package com.kickflip.myfirstapp;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class OrganizeCardFragment extends Fragment {
    private List<MyApplicationInfo> allApps;
    private String title;
    private Drawable icon;

    public OrganizeCardFragment(List<ApplicationInfo> apps, String title, Drawable icon) {
        allApps = new ArrayList<>();

        for (ApplicationInfo info:apps){
            this.allApps.add(new MyApplicationInfo(info));
        }
        this.title = title;
        this.icon = icon;
    }

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

    private class MyApplicationInfo{
        private ApplicationInfo info;
        private boolean enabled;

        public MyApplicationInfo(ApplicationInfo info) {
            this.info = info;
            this.enabled = false;
        }

        public ApplicationInfo getInfo() {
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
            button.setBackground(allApps.get(position).getInfo().loadIcon(getActivity().getPackageManager()));

            button.setLayoutParams(new ViewGroup.LayoutParams(
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())));

            return button;
        }
    }
}
