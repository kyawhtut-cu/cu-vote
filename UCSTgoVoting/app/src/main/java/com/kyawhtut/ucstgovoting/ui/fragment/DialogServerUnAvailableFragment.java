package com.kyawhtut.ucstgovoting.ui.fragment;

import android.content.DialogInterface;
import android.widget.TextView;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.utils.fonts.FontUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class DialogServerUnAvailableFragment extends DialogFragmentBlur {

    @BindView(R.id.tv_message)
    TextView mTvMessage;

    private String message;
    private DialogServerUnAvailableClickListener mListener;

    public DialogFragmentBlur newInstance(int radius, float downScaleFactor, boolean dimming, boolean debug, String message) {
        this.message = message;
        setCancelable(false);
        return super.newInstance(radius, downScaleFactor, dimming, debug);
    }

    public DialogServerUnAvailableFragment setListener(DialogServerUnAvailableClickListener listener) {
        this.mListener = listener;
        return this;
    }

    @OnClick(R.id.btn_ok)
    public void onClickBtnOk() {
        if (mListener != null)
            mListener.onClickOk(getDialog());
    }

    @Override
    protected int getLayout() {
        return R.layout.dialog_server_close;
    }

    @Override
    protected void prepareView() {
        mTvMessage.setText(FontUtils.getConvertedString(message));
    }

    public interface DialogServerUnAvailableClickListener {
        void onClickOk(DialogInterface dialog);
    }
}
