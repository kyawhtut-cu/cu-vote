package com.kyawhtut.ucstgovoting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;
import com.kyawhtut.ucstgovoting.adapter.viewholders.BaseViewHolder;

import java.util.List;

public abstract class BaseAdapter<V extends BaseViewHolder, D> extends RecyclerView.Adapter<V> {

    LayoutInflater mInflater;
    List<D> mData;
    DefaultItemClickListener<D> mDefaultItemClickListener;
    private Context mContext;

    BaseAdapter(Context mContext, DefaultItemClickListener<D> mDefaultItemClickListener) {
        this.mContext = mContext;
        this.mDefaultItemClickListener = mDefaultItemClickListener;

        mInflater = LayoutInflater.from(mContext);
    }

    public void swipeData(List<D> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setData(int pos, D data) {
        this.mData.remove(pos);
        this.mData.add(pos, data);
        notifyItemChanged(pos);
    }

    public void changeData(D data, int pos) {
        this.mData.remove(pos);
        this.mData.add(pos, data);
    }

    public List<D> getAllData() {
        return mData;
    }

    public D getData(int pos) {
        return mData.get(pos);
    }


    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return (mData != null) ? mData.size() : 0;
    }
}
