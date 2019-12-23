package com.kyawhtut.ucstgovoting.adapter.viewholders;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListener;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.Utils;
import com.kyawhtut.ucstgovoting.utils.longpress.LongPressPopupCallBack;
import com.kyawhtut.ucstgovoting.utils.longpress.PopupTouchListener;

import butterknife.BindView;
import timber.log.Timber;

public class PhotoViewHolder extends BaseViewHolder<String> {

    @BindView(R.id.selection_image)
    ImageView selectionImage;

    @SuppressLint("ClickableViewAccessibility")
    public PhotoViewHolder(@NonNull View itemView, DefaultItemClickListener<String> mDefaultItemClickListener) {
        super(itemView, mDefaultItemClickListener);

        selectionImage.setOnClickListener(v -> mDefaultItemClickListener.onClickPhoto(mData, getAdapterPosition()));

        selectionImage.setOnTouchListener(new PopupTouchListener(new LongPressPopupCallBack() {
            @Override
            public void onLongPressStart(MotionEvent motionEvent) {
                mDefaultItemClickListener.onDialogShow(mData);
            }

            @Override
            public void onLongPressEnd(MotionEvent motionEvent) {
                mDefaultItemClickListener.onDialogDismiss();
            }
        }, 100));
    }

    @Override
    public void bind(String data) {
        super.bind(data);
        Timber.i("Data : %s", data);
        GlideApp.with(itemView.getContext())
                .load(data.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .centerCrop()
                .into(selectionImage);
    }
}
