package com.kyawhtut.ucstgovoting.ui.activity;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.database.AppDatabase;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.model.VotingModel;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogFragmentBlur;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogServerUnAvailableFragment;
import com.kyawhtut.ucstgovoting.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class WelcomeActivity extends AppCompatActivity {

    private CompositeDisposable mCompositeDisposable;
    private AppDatabase mAppDatabase;

    @BindView(R.id.version_tv)
    TextView mVersionTv;

    @BindView(R.id.re_connect)
    TextView mReConnect;

    @BindView(R.id.connection_error)
    RelativeLayout mConnectionError;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this, this);

        setVersion();

        mCompositeDisposable = new CompositeDisposable();
        mAppDatabase = AppDatabase.getINSTANCE(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        netWorkCheck();
    }

    private void netWorkCheck() {
        mConnectionError.setVisibility(View.GONE);
        if (!NetworkUtils.isOnline(this)) {
            mConnectionError.setVisibility(View.VISIBLE);
            new DialogServerUnAvailableFragment().newInstance(
                    16,
                    4,
                    false,
                    false,
                    getResources().getString(R.string.connection_error)
            ).setListener(new DialogFragmentBlur.DialogDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                }
            }).show(getFragmentManager(), "");
        } else {
            mCompositeDisposable.add(
                    VotingModel.getINSTANCE().getServerStatus()
                            .subscribeOn(Schedulers.io())
                            .map(selectionResponse -> {
                                if (selectionResponse.message == null) {
                                    List<Selection> tmp = new ArrayList<>();
                                    for (Selection selection : selectionResponse.selectionList) {
                                        selection.photo_col = new Gson().toJson(selection.photo);
                                        tmp.add(selection);
                                    }
                                    mAppDatabase.selectionDao().deleteAll();
                                    long[] lol = mAppDatabase.selectionDao().insertSelection(tmp);
                                    Timber.i("id : %s", new Gson().toJson(lol));
                                    return "success";
                                }
                                return selectionResponse.message;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(s -> {
                                Timber.i(s);
                                if (s.equals("success")) {
                                    finish();
                                    startActivity(HomeActivity.getIntent(WelcomeActivity.this));
                                } else {
                                    new DialogServerUnAvailableFragment().newInstance(
                                            32,
                                            4,
                                            false,
                                            false,
                                            s
                                    ).setListener(new DialogFragmentBlur.DialogDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            finish();
                                        }
                                    }).show(getFragmentManager(), "");
                                }
                            }, throwable -> {
                                Timber.e(throwable);
                                new DialogServerUnAvailableFragment().newInstance(
                                        16,
                                        4,
                                        false,
                                        false,
                                        getResources().getString(R.string.connection_error)
                                ).setListener(new DialogFragmentBlur.DialogDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        finish();
                                    }
                                }).show(getFragmentManager(), "");
                            })
            );
        }
    }

    @OnClick(R.id.re_connect)
    public void onClickReConnect() {
        netWorkCheck();
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

    @Override
    protected void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }
}
