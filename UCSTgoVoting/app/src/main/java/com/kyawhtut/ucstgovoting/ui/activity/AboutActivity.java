package com.kyawhtut.ucstgovoting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.WindowManager;
import android.widget.TextView;

import com.kyawhtut.ucstgovoting.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version_tv)
    TextView mVersionTv;

    public static Intent getIntent(Context ctx) {
        return new Intent(ctx, AboutActivity.class);
    }

    public static Intent getOpenFacebookIntent(Context context, String fbId) {
        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format("fb://profile/%s", fbId)));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse(String.format("https://www.facebook.com/profile.php?id=,%s", fbId)));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this, this);

        setVersion();
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
                mVersionTv.setText(summary);
            } else {
                SpannableString s = new SpannableString("Version - " + versionName);
                s.setSpan(new ForegroundColorSpan(Color.GRAY), 7, s.length(), 0);
                s.setSpan(new RelativeSizeSpan(0.8f), 7, s.length(), 0);
                mVersionTv.setText(String.format("Version - %s", versionName));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.home)
    public void onClickHome() {
        finish();
    }

    @OnClick(R.id.developer_fb)
    public void onClickDeveloperFb() {
        startActivity(getOpenFacebookIntent(this, "100008526678537"));
    }

    @OnClick(R.id.developer_report)
    public void onClickDeveloperReport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"developer.kyawhtut@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Report | CU Vote");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    @OnClick(R.id.library)
    public void onClickLibrary() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Open Source Libraries");
        alert.setItems(R.array.library_list, (dialogInterface, i) -> {
        });
        alert.show();
    }
}
