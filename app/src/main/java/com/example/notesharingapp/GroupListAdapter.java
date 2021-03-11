package com.example.notesharingapp;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupListAdapter implements ListAdapter {
    ArrayList<Group> arrayList;
    Context context;

    public GroupListAdapter(ArrayList<Group> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Group subjectData=arrayList.get(i);
        if(view==null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view=layoutInflater.inflate(R.layout.group, null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            TextView tittle=view.findViewById(R.id.tv_group_title);
            TextView description = view.findViewById(R.id.tv_group_description);
            LinearLayout linearLayout = view.findViewById(R.id.ll_each_group);
            tittle.setText(subjectData.getName());
            description.setText(subjectData.getDescription());
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, subjectData.getKey()+" is clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,GroupActivity.class);
                    intent.putExtra("group_key",subjectData.getKey());
                    intent.putExtra("group_name",subjectData.getName());
                    context.startActivity(intent);
                }
            });

        }
        return view;
    }

    @Override
    public int getItemViewType(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
