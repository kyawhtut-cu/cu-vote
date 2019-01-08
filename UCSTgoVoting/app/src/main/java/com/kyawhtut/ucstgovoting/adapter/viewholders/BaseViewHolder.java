package com.kyawhtut.ucstgovoting.adapter.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;

import butterknife.ButterKnife;

public abstract class BaseViewHolder<D> extends RecyclerView.ViewHolder {

    protected D mData;
    protected DefaultItemClickListener<D> mDefaultItemClickListener;

    public BaseViewHolder(@NonNull View itemView, DefaultItemClickListener<D> mDefaultItemClickListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mDefaultItemClickListener = mDefaultItemClickListener;
    }

    public void bind(D data) {
        this.mData = data;
    }
}
