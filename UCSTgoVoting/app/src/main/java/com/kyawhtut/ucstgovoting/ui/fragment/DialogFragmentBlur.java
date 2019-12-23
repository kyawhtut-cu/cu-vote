package com.kyawhtut.ucstgovoting.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import butterknife.ButterKnife;
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;

/**
 * Simple fragment with blur effect behind.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class DialogFragmentBlur extends BlurDialogFragment {

    /**
     * Bundle key used to start the blur dialog with a given scale factor (float).
     */
    private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

    /**
     * Bundle key used to start the blur dialog with a given blur radius (int).
     */
    private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

    /**
     * Bundle key used to start the blur dialog with a given dimming effect policy.
     */
    private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

    /**
     * Bundle key used to start the blur dialog with a given debug policy.
     */
    private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;

    /**
     * Dialog Dismiss Listener
     */
    protected DialogDismissListener mListener;

    /**
     * Retrieve a new instance of the sample fragment.
     *
     * @param radius          blur radius.
     * @param downScaleFactor down scale factor.
     * @param dimming         dimming effect.
     * @param debug           debug policy.
     * @return well instantiated fragment.
     */

    public DialogFragmentBlur newInstance(int radius,
                                          float downScaleFactor,
                                          boolean dimming,
                                          boolean debug) {
        Bundle args = new Bundle();
        args.putInt(
                BUNDLE_KEY_BLUR_RADIUS,
                radius
        );
        args.putFloat(
                BUNDLE_KEY_DOWN_SCALE_FACTOR,
                downScaleFactor
        );
        args.putBoolean(
                BUNDLE_KEY_DIMMING,
                dimming
        );
        args.putBoolean(
                BUNDLE_KEY_DEBUG,
                debug
        );

        this.setArguments(args);
        return this;
    }

    public DialogFragmentBlur setListener(DialogDismissListener mListener) {
        this.mListener = mListener;
        return this;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(getLayout(), null);
        ButterKnife.bind(this, view);

        prepareView();

        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mListener != null)
            mListener.onDismiss(dialog);
        else
            super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mListener != null)
            mListener.onDismiss(dialog);
        else
            super.onDismiss(dialog);
    }

    protected abstract int getLayout();

    protected abstract void prepareView();

    protected String getResString(int res) {
        return getActivity().getResources().getString(res);
    }

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return true;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }

    public abstract static class DialogDismissListener {
        public abstract void onDismiss(DialogInterface dialog);
    }
}