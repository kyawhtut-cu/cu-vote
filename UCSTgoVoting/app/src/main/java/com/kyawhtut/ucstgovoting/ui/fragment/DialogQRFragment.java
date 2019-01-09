package com.kyawhtut.ucstgovoting.ui.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.ui.activity.HomeActivity;
import com.kyawhtut.ucstgovoting.utils.DateUtils;
import com.kyawhtut.ucstgovoting.utils.SelectionInjection;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;
import com.kyawhtut.ucstgovoting.utils.Utils;
import com.kyawhtut.ucstgovoting.utils.permission.PermissionListener;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Simple fragment with blur effect behind.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DialogQRFragment extends DialogFragmentBlur {

    private SelectionUtil mSelectionUtil;

    @BindView(R.id.qr_image)
    ImageView mQRImage;

    @BindView(R.id.label_current_time)
    TextView mLabelCurrentTime;

    @BindView(R.id.qr_layout)
    RelativeLayout mQRLayout;

    public DialogFragmentBlur newInstance(int radius,
                                          float downScaleFactor,
                                          boolean dimming,
                                          boolean debug) {
        return super.newInstance(radius, downScaleFactor, dimming, debug);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSelectionUtil = SelectionInjection.provideSelection(activity);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_qr;
    }

    @Override
    protected void prepareView() {
        mLabelCurrentTime.setText(DateUtils.convertMilSecTo("dd/MM/yyyy h:mm a"));
        String qrData = mSelectionUtil.getString(SelectionUtil.KING_ID) + "#" +
                mSelectionUtil.getString(SelectionUtil.QUEEN_ID) + "#" +
                mSelectionUtil.getString(SelectionUtil.ATTRACTIVE_BOY_ID) + "#" +
                mSelectionUtil.getString(SelectionUtil.ATTRACTIVE_GIRL_ID) + "#" +
                mSelectionUtil.getString(SelectionUtil.INNOCENCE_BOY_ID) + "#" +
                mSelectionUtil.getString(SelectionUtil.INNOCENCE_GIRL_ID);
        mQRImage.setImageBitmap(getQrImage(qrData));
    }

    public Bitmap getBitmap() {
        getActivity().getWindow().getDecorView().setDrawingCacheEnabled(true);
        getActivity().getWindow().getDecorView().buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(getActivity().getWindow().getDecorView().getDrawingCache(), 0, Utils.convertDpToPixel(100, getActivity()), Utils.getScreenWidth(getActivity()), mQRLayout.getHeight());
        getActivity().getWindow().getDecorView().setDrawingCacheEnabled(false);

        Canvas canvas = new Canvas(bmp);
        mQRLayout.setDrawingCacheEnabled(true);
        mQRLayout.buildDrawingCache();
        canvas.drawBitmap(mQRLayout.getDrawingCache(), Utils.convertDpToPixel(16, getActivity()), 0, null);
        mQRLayout.setDrawingCacheEnabled(false);
        return bmp;
    }

    @OnClick(R.id.share)
    public void onClickShare(View view) {
        ((HomeActivity) getActivity()).shareQR(getBitmap(), new PermissionListener() {
            @Override
            public void onError(String error) {
                Snackbar.make(view, error, Snackbar.LENGTH_LONG)
                        .setAction("Try Again", v -> onClickShare(view)).show();
            }
        });
    }

    @OnClick(R.id.save)
    public void onClickSave(View view) {
        ((HomeActivity) getActivity()).saveQR(getBitmap(), new PermissionListener() {
            @Override
            public void onError(String error) {
                Snackbar.make(view, error, Snackbar.LENGTH_LONG)
                        .setAction("Try Again", v -> onClickShare(view)).show();
            }

            @Override
            public void onSuccess(String message) {
                Snackbar.make(view, "Saved successfully to " + message, Snackbar.LENGTH_LONG)
                        .setAction("View", v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(message), "image/*");
                            startActivity(Intent.createChooser(intent, "View using"));
                        }).show();
            }
        });
    }

    private Bitmap getQrImage(String qrData) {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 250, 250, null);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? 0xff000000 : 0xffffffff;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        } catch (WriterException e) {
            Timber.e(e);
        }
        return null;
    }
}