package com.kickflip.myfirstapp;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Map<String, String> fileNames;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fileNames = new HashMap<>();

        View view = inflater.inflate(R.layout.activity_card_view,container, false);

        view.findViewById(R.id.floating_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.addItem(new DataObject("Some Primary Text " + mAdapter.getItemCount(), R.drawable.ic_menu_build, MyActivity.getApplist()), mAdapter.getItemCount());

                String filename = "categorie" + mAdapter.getItemCount();
                fileNames.put("Some Primary Text " + mAdapter.getItemCount(), filename);
                String string = "Some Primary Text " + mAdapter.getItemCount() + ";" + R.drawable.ic_menu_build + ";";
                FileOutputStream outputStream;

                try {
                    outputStream = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(string.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        FileInputStream inputStream;

        try {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                inputStream = getActivity().openFileInput("categorie" + i);

                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = inputStream.read()) != -1) {
                    builder.append((char) ch);
                }

                String[] data = builder.toString().split(";");

                List<AppInfo> all = MyActivity.getApplist();
                List<AppInfo> card = new ArrayList<>();
                for (int j = 2; j < data.length; j++) {
                    for (AppInfo a : all) {
                        if (data[j].equals(a.getAppname())) {
                            card.add(a);
                            break;
                        }
                    }
                }

                mAdapter.addItem(new DataObject(data[0], Integer.parseInt(data[1]), card), mAdapter.getItemCount());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
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

            getFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrganizeCardFragment(appInfos, title, icon, fileNames.get(title))).addToBackStack("Fragment").commit();
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
