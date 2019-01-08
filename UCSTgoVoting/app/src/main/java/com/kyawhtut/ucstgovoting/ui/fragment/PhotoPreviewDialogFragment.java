package com.kyawhtut.ucstgovoting.ui.fragment;

import android.annotation.TargetApi;
import android.os.Build;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.ui.view.TouchImageView;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.Utils;

import butterknife.BindView;

/**
 * Simple fragment with blur effect behind.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class PhotoPreviewDialogFragment extends DialogFragmentBlur {


    @BindView(R.id.preview_image)
    TouchImageView mPreviewImage;

    private String mPhotoUrl;

    public DialogFragmentBlur newInstance(int radius, float downScaleFactor, boolean dimming, boolean debug, String photoUrl) {
        this.mPhotoUrl = photoUrl;
        return super.newInstance(radius, downScaleFactor, dimming, debug);
    }

    @Override
    protected int getLayout() {
        return R.layout.photo_view_dialog;
    }

    @Override
    protected void prepareView() {
        GlideApp.with(getActivity())
                .load(mPhotoUrl.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .placeholder(R.drawable.ic_place_holder)
                .error(R.drawable.ic_place_holder)
                .fitCenter()
                .into(mPreviewImage);
    }
}