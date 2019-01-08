package com.kyawhtut.ucstgovoting.adapter.viewholders;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.Utils;
import com.kyawhtut.ucstgovoting.utils.fonts.FontUtils;
import com.kyawhtut.ucstgovoting.utils.longpress.LongPressPopupCallBack;
import com.kyawhtut.ucstgovoting.utils.longpress.PopupTouchListener;

import java.util.List;

import butterknife.BindView;

public class SelectionViewHolder extends BaseViewHolder<Selection> {

    @BindView(R.id.parent_view)
    RelativeLayout mParentView;

    @BindView(R.id.selection_name)
    TextView mSelectionName;

    @BindView(R.id.refresh_img)
    ImageView mRefreshImg;

    @BindView(R.id.dot_progress_bar)
    DotProgressBar mDotProgressBar;

    @BindView(R.id.selection_fb)
    FloatingActionButton mSelectionFb;

    @BindView(R.id.selection_class)
    TextView mSelectionClass;

    @BindView(R.id.selection_like)
    FloatingActionButton mSelectionLike;

    @BindView(R.id.selection_img)
    ImageView mSelectionImg;

    private String mCurrentPhoto = "";

    public SelectionViewHolder(@NonNull View itemView, DefaultItemClickListener<Selection> mDefaultItemClickListener) {
        super(itemView, mDefaultItemClickListener);
        mParentView.getLayoutParams().width = Utils.getScreenWidth(itemView.getContext()) - Utils.convertDpToPixel(90, itemView.getContext());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void bind(Selection data) {
        super.bind(data);
        mSelectionName.setText(FontUtils.getConvertedString(data.name));
        mSelectionClass.setText(data.class_name);
        List<String> photo = new Gson().fromJson(data.photo_col, new TypeToken<List<String>>() {
        }.getType());
        if (photo.size() > 0) {
            mCurrentPhoto = photo.get(0);
            loadPhoto(photo.get(0));
        }

        mSelectionLike.setEnabled(true);

        if (data.isSelected) {
            mSelectionLike.setImageResource(R.drawable.ic_favorite_active);
            mSelectionLike.setEnabled(false);
        } else {
            mSelectionLike.setImageResource(R.drawable.ic_fav);
        }

        mRefreshImg.setOnClickListener(v -> loadPhoto(photo.get(0)));

        mSelectionImg.setOnClickListener(v -> {
            mDefaultItemClickListener.onClickPhoto(
                    mData,
                    getAdapterPosition()
            );
        });

        mSelectionFb.setOnClickListener(v -> {
            mDefaultItemClickListener.onClickFacebook(
                    mData,
                    getAdapterPosition()
            );
        });

        mSelectionLike.setOnClickListener(v -> {
            mDefaultItemClickListener.onClickFavourite(
                    mData,
                    getAdapterPosition()
            );
            mSelectionLike.setImageResource(R.drawable.ic_favorite_active);
        });

        mSelectionImg.setOnTouchListener(new PopupTouchListener(new LongPressPopupCallBack() {
            @Override
            public void onLongPressStart(MotionEvent motionEvent) {
                mDefaultItemClickListener.onDialogShow(
                        (photo.size() > 0) ? photo.get(0) : ""
                );
            }

            @Override
            public void onLongPressEnd(MotionEvent motionEvent) {
                mDefaultItemClickListener.onDialogDismiss();
            }
        }, 100));
    }

    private void loadPhoto(String url) {
        mRefreshImg.setVisibility(View.GONE);
        mDotProgressBar.setVisibility(View.VISIBLE);
        GlideApp.with(itemView.getContext())
                .asBitmap()
                .load(url.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        mDotProgressBar.setVisibility(View.GONE);
                        mRefreshImg.setVisibility(View.VISIBLE);
                        mSelectionImg.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        mDotProgressBar.setVisibility(View.GONE);
                        mRefreshImg.setVisibility(View.GONE);
                        mSelectionImg.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(mSelectionImg);
    }

    public String getCurrentPhoto() {
        return mCurrentPhoto.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE);
    }
}
