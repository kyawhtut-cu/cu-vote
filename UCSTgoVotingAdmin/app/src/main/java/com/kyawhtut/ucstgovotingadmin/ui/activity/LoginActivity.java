package com.kyawhtut.ucstgovotingadmin.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kyawhtut.ucstgovotingadmin.BuildConfig;
import com.kyawhtut.ucstgovotingadmin.R;
import com.kyawhtut.ucstgovotingadmin.model.AdminModel;
import com.kyawhtut.ucstgovotingadmin.utils.SharedPreferenceInjection;
import com.kyawhtut.ucstgovotingadmin.utils.SharedPreferenceUtil;
import com.kyawhtut.ucstgovotingadmin.utils.Utils;
import com.kyawhtut.ucstgovotingadmin.utils.fonts.FontUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class LoginActivity extends BaseActivity {

    public static Intent getIntent(Context ctx) {
        return new Intent(ctx, LoginActivity.class);
    }

    @BindView(R.id.ed_password)
    EditText mEdPassword;

    @BindView(R.id.password_input)
    TextInputLayout mPasswordInput;

    @BindView(R.id.email_input)
    TextInputLayout mEmailInput;

    @BindView(R.id.ed_email)
    EditText mEdEmail;

    @BindView(R.id.version_label)
    TextView mVersionLabel;

    private SharedPreferenceUtil mPreferenceUtil;
    private AdminModel mAdminModel;

    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdminModel = AdminModel.getINSTANCE();
        mPreferenceUtil = SharedPreferenceInjection.provideSelection(this);

        mDisposable = new CompositeDisposable();

        setVersion();
    }

    @Override
    public int getLayoutView() {
        return R.layout.activity_admin_login;
    }

    @OnCheckedChanged(R.id.show_password)
    public void onCheckChangePassword(boolean check) {
        if (check)
            mEdPassword.setInputType(InputType.TYPE_CLASS_TEXT);
        else
            mEdPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
        mEdPassword.setSelection(mEdPassword.getText().toString().length());
    }

    @OnClick(R.id.about_us)
    public void onClickAboutUs() {
        start(this, AboutActivity.class);
    }

    @OnClick(R.id.btn_sign_in)
    public void onClickBtnSignIn() {
        if (TextUtils.isEmpty(mEdEmail.getText().toString()) || TextUtils.isEmpty(mEdPassword.getText().toString())) {
            if (TextUtils.isEmpty(mEdEmail.getText().toString())) {
                mEmailInput.setError("Email is required.");
            }
            if (TextUtils.isEmpty(mEdPassword.getText().toString())) {
                mPasswordInput.setError("Password is required");
            }
        } else {
            String email = mEdEmail.getText().toString();
            String password = mEdPassword.getText().toString();
            mDisposable.add(
                    mAdminModel.loginUser(
                            email,
                            password
                    ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(loginResponse -> {
                                if (loginResponse.message.equals(BuildConfig.API_KEY)) {
                                    mPreferenceUtil.putString(Utils.USER_EMAIL, email);
                                    mPreferenceUtil.putString(Utils.USER_PASSWORD, password);
                                    mPreferenceUtil.putBoolean(Utils.USER_LOGIN, true);
                                    finish();
                                    startActivity(HomeActivity.getIntent(LoginActivity.this));
                                } else {
                                    mPreferenceUtil.clearValue();
                                    showDialog(loginResponse.message);
                                }
                            }, throwable -> {
                                showDialog(throwable.getMessage());
                                Timber.e(throwable);
                            })
            );
        }
    }

    private void showDialog(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_information, null);
        TextView mTvMessage = view.findViewById(R.id.tv_message);
        TextView mTvOk = view.findViewById(R.id.btn_ok);
        alert.setView(view);
        AlertDialog dialog = alert.create();

        mTvMessage.setText(FontUtils.getConvertedString(message));
        mTvOk.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDisposable.clear();
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
