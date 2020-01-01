package com.kyawhtut.ucstgovotingadmin.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;
import com.kyawhtut.ucstgovotingadmin.BuildConfig;
import com.kyawhtut.ucstgovotingadmin.R;
import com.kyawhtut.ucstgovotingadmin.model.AdminModel;
import com.kyawhtut.ucstgovotingadmin.utils.NetworkUtils;
import com.kyawhtut.ucstgovotingadmin.utils.SharedPreferenceInjection;
import com.kyawhtut.ucstgovotingadmin.utils.SharedPreferenceUtil;
import com.kyawhtut.ucstgovotingadmin.utils.Utils;
import com.kyawhtut.ucstgovotingadmin.utils.fonts.FontUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import timber.log.Timber;

public class HomeActivity extends BaseActivity implements ZXingScannerView.ResultHandler {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.qr_scanner)
    ZXingScannerView mQrScanner;

    @BindView(R.id.version_label)
    TextView mVersionLabel;

    @BindView(R.id.scanner_layout)
    RelativeLayout mScannerLayout;

    @BindView(R.id.permission_error)
    LinearLayout mPermissionError;

    @BindView(R.id.loading_progress)
    ProgressBar mLoadingProgress;

    private AdminModel mAdminModel;
    private CompositeDisposable mDisposable;
    private SharedPreferenceUtil mPreferenceUtil;

    public static Intent getIntent(Context ctx) {
        return new Intent(ctx, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(mToolbar);
        getSupportActionBar().hide();

        mPreferenceUtil = SharedPreferenceInjection.provideSelection(this);

        if (!mPreferenceUtil.getBoolean(Utils.USER_LOGIN, false)) {
            finish();
            startActivity(LoginActivity.getIntent(this));
        }

        setVersion();

        mAdminModel = AdminModel.getINSTANCE();
        mDisposable = new CompositeDisposable();

        mQrScanner.setResultHandler(this);
        mQrScanner.setAutoFocus(true);
        openScannerView();
    }

    private void loginUserCheck() {
        mDisposable.add(
                mAdminModel.loginUser(
                        mPreferenceUtil.getString(Utils.USER_EMAIL, ""),
                        mPreferenceUtil.getString(Utils.USER_PASSWORD, "")
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(loginResponse -> {
                            if (!loginResponse.message.equals(BuildConfig.API_KEY)) {
                                mPreferenceUtil.clearValue();
                                showDialog(loginResponse.message, true);
                            }
                        }, Timber::e)
        );
    }

    private void openScannerView() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    101);
        } else {
            mPermissionError.setVisibility(View.GONE);
            mScannerLayout.setVisibility(View.VISIBLE);
            mQrScanner.startCamera();
        }
    }

    @OnClick(R.id.try_again)
    public void onClickTryAgain() {
        openScannerView();
    }

    @OnClick(R.id.about_us)
    public void onClickAboutUs() {
        start(this, AboutActivity.class);
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_home;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginUserCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mQrScanner != null) {
            mQrScanner.startCamera();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
        if (mQrScanner != null) {
            mQrScanner.stopCamera();
        }
    }

    @Override
    public void handleResult(Result result) {
        if (NetworkUtils.isOnline(this)) {
            try {
                List<String> selectionId = Arrays.asList(result.getText().split("#"));
                if (selectionId.size() >= 6) {
                    mLoadingProgress.setVisibility(View.VISIBLE);
                    mDisposable.add(
                            mAdminModel.votedSelection(selectionId)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(votingResponse -> {
                                        mLoadingProgress.setVisibility(View.GONE);
                                        showDialog(votingResponse.message, false);
                                    }, throwable -> {
                                        mLoadingProgress.setVisibility(View.GONE);
                                        showDialog(throwable.getMessage(), false);
                                        Timber.e(throwable);
                                    })
                    );
                } else
                    mQrScanner.resumeCameraPreview(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            showDialog(getResources().getString(R.string.connection_error), false);
        }
    }

    private void showDialog(String message, boolean isFinish) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_information, null);
        TextView mTvMessage = view.findViewById(R.id.tv_message);
        TextView mTvOk = view.findViewById(R.id.btn_ok);
        alert.setView(view);
        AlertDialog dialog = alert.create();

        mTvMessage.setText(FontUtils.getConvertedString(message));
        mTvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(dialogInterface -> {
            dialogInterface.dismiss();
            mQrScanner.resumeCameraPreview(this);
            if (isFinish) {
                finish();
                startActivity(LoginActivity.getIntent(this));
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("Access");
                    openScannerView();
                } else {
                    Timber.i("Disable ");
                    mPermissionError.setVisibility(View.VISIBLE);
                    mScannerLayout.setVisibility(View.GONE);
                }
        }
    }

    private void setVersion() {
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;

            String regex = "^([\\d]+).([\\d]+).([\\d]+).([\\d]{4,})([\\d]{2,})([\\d]{2,})$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(versionName);
            if (matcher.matches()) {
                String summary = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3);
                summary += " (Date: " + matcher.group(6) + "/" + matcher.group(5) + "/" + matcher.group(4) + ")";
                mVersionLabel.setText(summary);
            } else {
                SpannableString s = new SpannableString("Version - " + versionName);
                s.setSpan(new ForegroundColorSpan(Color.GRAY), 7, s.length(), 0);
                s.setSpan(new RelativeSizeSpan(0.8f), 7, s.length(), 0);
                mVersionLabel.setText(String.format("Version - %s", versionName));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
