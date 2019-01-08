package com.kyawhtut.ucstgovoting.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.ui.view.TouchImageView;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class PhotoViewFragment extends BaseFragment {

    @BindView(R.id.photo_view)
    TouchImageView mPhotoView;

    @BindView(R.id.item_refresh)
    ImageView mItemRefresh;

    @BindView(R.id.dot_progress_bar)
    DotProgressBar mDotProgressBar;

    private String mPhotoUrl;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPhotoUrl = getArguments().getString("photo_url");

        loadPhoto();

    }

    @OnClick(R.id.item_refresh)
    public void onClickItemRefresh(View view) {
        loadPhoto();
    }

    private void loadPhoto() {
        mItemRefresh.setVisibility(View.GONE);
        mDotProgressBar.setVisibility(View.VISIBLE);
        GlideApp.with(getContext())
                .asBitmap()
                .load(mPhotoUrl.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        mDotProgressBar.setVisibility(View.GONE);
                        mItemRefresh.setVisibility(View.VISIBLE);
                        mPhotoView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        mDotProgressBar.setVisibility(View.GONE);
                        mItemRefresh.setVisibility(View.GONE);
                        mPhotoView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(mPhotoView);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_photo_view;
    }
}
