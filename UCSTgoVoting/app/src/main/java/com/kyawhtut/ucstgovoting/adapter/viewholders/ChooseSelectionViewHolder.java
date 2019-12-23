package com.kyawhtut.ucstgovoting.adapter.viewholders;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ChooseSelectionViewHolder extends BaseViewHolder<Selection> {

    @BindView(R.id.selection_name)
    TextView mSelectionName;

    @BindView(R.id.selection_img)
    ImageView mSelectionImg;

    private List<String> photo;

    public ChooseSelectionViewHolder(@NonNull View itemView, DefaultItemClickListener<Selection> mDefaultItemClickListener) {
        super(itemView, mDefaultItemClickListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void bind(Selection data) {
        super.bind(data);
        mSelectionName.setText(FontUtils.getConvertedString(data.name));
        photo = new Gson().fromJson(data.photo_col, new TypeToken<List<String>>() {
        }.getType());
        if (photo != null && photo.size() > 0)
            loadPhoto(photo.get(0));
        else
            GlideApp.with(itemView.getContext())
                    .load(R.drawable.ic_place_holder)
                    .thumbnail(0.1f)
                    .centerCrop()
                    .into(mSelectionImg);

        mSelectionImg.setOnClickListener(v -> {
            if (photo != null)
                mDefaultItemClickListener.onClickPhoto(
                        mData,
                        getAdapterPosition()
                );
            else
                mDefaultItemClickListener.onClickItem(mData, getAdapterPosition());
        });

        mSelectionImg.setOnTouchListener(new PopupTouchListener(new LongPressPopupCallBack() {
            @Override
            public void onLongPressStart(MotionEvent motionEvent) {
                if (photo != null)
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
        GlideApp.with(itemView.getContext())
                .load(url.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .fitCenter()
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .into(mSelectionImg);
    }
}
