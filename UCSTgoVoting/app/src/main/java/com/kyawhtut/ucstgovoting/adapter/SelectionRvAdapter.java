package com.kyawhtut.ucstgovoting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;
import com.kyawhtut.ucstgovoting.adapter.viewholders.SelectionViewHolder;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;

public class SelectionRvAdapter extends BaseAdapter<SelectionViewHolder, Selection> {

    public SelectionRvAdapter(Context mContext, DefaultItemClickListener<Selection> mDefaultItemClickListener) {
        super(mContext, mDefaultItemClickListener);
    }

    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.selection_info_card, viewGroup, false);
        return new SelectionViewHolder(view, mDefaultItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder selectionViewHolder, int i) {
        selectionViewHolder.bind(mData.get(i));
    }
}
