package com.kickflip.myfirstapp.settings.organize;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.kickflip.myfirstapp.R;
import com.kickflip.myfirstapp.appModel.AppInfo;
import com.kickflip.myfirstapp.appModel.CategorieInfo;
import com.kickflip.myfirstapp.settings.MyActivity;
import com.kickflip.myfirstapp.settings.properties.PropertiesFragment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganizeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper touchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_card_view,container, false);

        view.findViewById(R.id.floating_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.addItem(new DataObject("Some Primary Text " + mAdapter.getItemCount(), R.drawable.ic_menu_build, MyActivity.getInfo().getApplist()), mAdapter.getItemCount());

                MyActivity.getInfo().newCategorie("Some Primary Text " + (mAdapter.getItemCount()-1) ,R.drawable.ic_menu_build);

                Intent intent = new Intent(PropertiesFragment.PROPERTIES_ACTION + ".organize");
                getActivity().sendBroadcast(intent);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MyRecyclerViewAdapter(new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                touchHelper.startDrag(viewHolder);
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);


        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);


        for (CategorieInfo info: MyActivity.getInfo().getCategorieInfos()){
            mAdapter.addItem(new DataObject(info.getName(), info.getImage(), MyActivity.getInfo().getApplist()), mAdapter.getItemCount());
        }

        return view;
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        List<CategorieInfo> categorieInfos = MyActivity.getInfo().getCategorieInfos();

        Gson gson = new Gson();
        Type type = new TypeToken<List<CategorieInfo>>() {}.getType();

        String json = gson.toJson(categorieInfos, type);

        editor.putString("categories", json);
        editor.commit();

        super.onPause();
    }

    private class MyRecyclerViewAdapter extends RecyclerView.Adapter<DataObjectHolder> implements ItemTouchHelperAdapter {
        private List<DataObject> mDataset;
        private OnStartDragListener mDragStartListener;

        public MyRecyclerViewAdapter(OnStartDragListener dragStartListener) {
            mDataset = new ArrayList<>();
            mDragStartListener = dragStartListener;
        }

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_row, parent, false);

            DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
            return dataObjectHolder;
        }

        @Override
        public void onBindViewHolder(final DataObjectHolder holder, int position) {
            holder.getTitle().setText(mDataset.get(position).getTitle());
            holder.getIcon().setImageResource(mDataset.get(position).getIcon());
            holder.setAppInfos(mDataset.get(position).getAppInfos());

            holder.getHandleView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }

        public void addItem(DataObject dataObj, int index) {
            mDataset.add(index, dataObj);
            notifyItemInserted(index);
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDataset, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDataset, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            mDataset.remove(position);
            notifyItemRemoved(position);
        }
    }

    private class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        private TextView title;
        private ImageView icon;
        private List<AppInfo> appInfos;
        private ImageView handleView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_view_title);
            icon = (ImageView) itemView.findViewById(R.id.card_view_icon);
            handleView = (ImageView) itemView.findViewById(R.id.handle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String title = ((TextView) v.findViewById(R.id.card_view_title)).getText().toString();
            Drawable icon = ((ImageView) v.findViewById(R.id.card_view_icon)).getDrawable();

            OrganizeCardFragment organizeCardFragment = new OrganizeCardFragment(MyActivity.getInfo().getCategorieInfo(title).getPackages(), title, icon);

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, organizeCardFragment).addToBackStack("Fragment").commit();
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

        public TextView getTitle() {
            return title;
        }

        public ImageView getIcon() {
            return icon;
        }

        public void setAppInfos(List<AppInfo> appInfos) {
            this.appInfos = appInfos;
        }

        public ImageView getHandleView() {
            return handleView;
        }
    }

    private class DataObject {
        private String title;
        private int icon;
        private List<AppInfo> appInfos;

        public DataObject(String title, int icon, List<AppInfo> appInfos) {
            this.title = title;
            this.icon = icon;
            this.appInfos = appInfos;
        }

        public String getTitle() {
            return title;
        }

        public int getIcon() {
            return icon;
        }

        public List<AppInfo> getAppInfos() {
            return appInfos;
        }
    }
}
