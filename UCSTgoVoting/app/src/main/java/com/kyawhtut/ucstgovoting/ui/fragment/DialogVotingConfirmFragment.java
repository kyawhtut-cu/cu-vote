package com.kyawhtut.ucstgovoting.ui.fragment;

import android.content.DialogInterface;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

public class DialogVotingConfirmFragment extends DialogFragmentBlur {

    private String mDialogMessage;
    private String mSelectionPhoto;

    @BindView(R.id.selection_img)
    ImageView mSelectionImg;

    @BindView(R.id.tv_message)
    TextView mTvMessage;

    private DialogButtonClickListener mListener;

    public DialogFragmentBlur newInstance(int radius, float downScaleFactor, boolean dimming, boolean debug, String dialogMessage, String selectionPhoto) {
        this.mDialogMessage = dialogMessage;
        this.mSelectionPhoto = selectionPhoto;
        return super.newInstance(radius, downScaleFactor, dimming, debug);
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_confirm;
    }

    @Override
    protected void prepareView() {
        GlideApp.with(getActivity())
                .load(mSelectionPhoto.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .centerCrop()
                .into(mSelectionImg);

        mTvMessage.setText(Html.fromHtml(mDialogMessage));
    }

    public DialogVotingConfirmFragment setListener(DialogButtonClickListener mListener) {
        this.mListener = mListener;
        return this;
    }

    @OnClick(R.id.btn_ok)
    public void onClickOk() {
        if (mListener != null)
            mListener.onClickOk(getDialog());
    }

    @OnClick(R.id.btn_cancel)
    public void onClickCancel() {
        if (mListener != null)
            mListener.onClickCancel();
        onDismiss(getDialog());
    }

    public interface DialogButtonClickListener {
        void onClickOk(DialogInterface dialog);

        void onClickCancel();
    }
}
