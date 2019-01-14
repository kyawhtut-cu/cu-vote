package com.kyawhtut.ucstgovoting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;
import com.kyawhtut.ucstgovoting.adapter.viewholders.PhotoViewHolder;

public class PhotoRvAdapter extends BaseAdapter<PhotoViewHolder, String> {

    public PhotoRvAdapter(Context mContext, DefaultItemClickListener<String> mDefaultItemClickListener) {
        super(mContext, mDefaultItemClickListener);
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PhotoViewHolder(mInflater.inflate(R.layout.item_selection_image, viewGroup, false), mDefaultItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder selectionViewHolder, int i) {
        selectionViewHolder.bind(mData.get(i));
    }
}
