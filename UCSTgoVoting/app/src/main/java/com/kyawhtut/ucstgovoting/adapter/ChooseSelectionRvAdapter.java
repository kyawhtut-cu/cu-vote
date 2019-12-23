package com.kyawhtut.ucstgovoting.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;
import com.kyawhtut.ucstgovoting.adapter.viewholders.ChooseSelectionViewHolder;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;

public class ChooseSelectionRvAdapter extends BaseAdapter<ChooseSelectionViewHolder, Selection> {

    public ChooseSelectionRvAdapter(Context mContext, DefaultItemClickListener<Selection> mDefaultItemClickListener) {
        super(mContext, mDefaultItemClickListener);
    }

    @NonNull
    @Override
    public ChooseSelectionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.choose_selection_card, viewGroup, false);
        return new ChooseSelectionViewHolder(view, mDefaultItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseSelectionViewHolder selectionViewHolder, int i) {
        selectionViewHolder.bind(mData.get(i));
    }
}
