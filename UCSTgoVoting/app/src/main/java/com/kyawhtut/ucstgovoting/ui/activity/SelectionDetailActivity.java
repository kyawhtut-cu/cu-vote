package com.kyawhtut.ucstgovoting.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.PhotoRvAdapter;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListenerCallBack;
import com.kyawhtut.ucstgovoting.model.VotingModel;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogFragmentBlur;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogPhotoPreviewFragment;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.NetworkUtils;
import com.kyawhtut.ucstgovoting.utils.Utils;
import com.kyawhtut.ucstgovoting.utils.fonts.FontUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class SelectionDetailActivity extends BaseActivity {

    @BindView(R.id.selection_image)
    ImageView selectionImage;

    @BindView(R.id.tv_selection_name)
    TextView tvSelectionName;

    @BindView(R.id.tv_selection_information)
    TextView tvSelectionInformation;

    @BindView(R.id.rv_image)
    RecyclerView rvImage;

    @BindView(R.id.error_label)
    TextView errorLabel;

    @BindView(R.id.loading_label)
    TextView loadingLabel;

    @BindView(R.id.loading_layout)
    Group loadingLayout;

    @BindView(R.id.try_again_layout)
    Group tryAgainLayout;

    @BindView(R.id.btn_try_again)
    Button btnTryAgain;

    @BindView(R.id.no_data)
    ConstraintLayout noData;

    @BindView(R.id.selection_layout)
    ScrollView selectionLayout;

    private PhotoRvAdapter mPhotoRvAdapter;
    private String link = "";
    private String selectionFacebookId = "";
    private CompositeDisposable mCompositeDisposable;
    private DialogFragmentBlur mBlurDialog;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        link = getIntent().getDataString();

        mCompositeDisposable = new CompositeDisposable();

        Pattern mPattern = Pattern.compile("http:\\/\\/voting\\.kyawhtut\\.com\\/detail\\/(.*)", Pattern.CASE_INSENSITIVE);

        Matcher matcher = mPattern.matcher(link);

        mPhotoRvAdapter = new PhotoRvAdapter(this, new DefaultItemClickListenerCallBack<String>() {
            @Override
            public void onClickPhoto(String data, int position) {
                loadPhoto(data);
            }

            @Override
            public void onDialogShow(String url) {
                mBlurDialog = new DialogPhotoPreviewFragment().newInstance(
                        32,
                        4,
                        false,
                        false,
                        url
                );
                mBlurDialog.show(getFragmentManager(), "");
            }

            @Override
            public void onDialogDismiss() {
                if (mBlurDialog != null) {
                    mBlurDialog.dismiss();
                }
            }
        });
        rvImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImage.setAdapter(mPhotoRvAdapter);

        try {
            if (matcher.matches()) {
                link = URLDecoder.decode(matcher.group(1), "UTF-8");

                Timber.i("Name : %s", link);
            } else {
                Timber.i(link);
            }
        } catch (UnsupportedEncodingException e) {
            Timber.e(e);
        }

        if (NetworkUtils.isOnline(this)) {
            onClickTryAgain();
        } else {
            selectionLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            tryAgainLayout.setVisibility(View.VISIBLE);
            errorLabel.setText(getStringResource(R.string.connection_error));
        }

        errorLabel.setText(FontUtils.getConvertedString(this, R.string.selection_empty));
        btnTryAgain.setText(FontUtils.getConvertedString(this, R.string.try_again));

        loadingLabel.setText(FontUtils.getConvertedString(this, R.string.selection_data_loading));
    }

    @OnClick(R.id.btn_try_again)
    public void onClickTryAgain() {
        if (!NetworkUtils.isOnline(this)) {
            selectionLayout.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            tryAgainLayout.setVisibility(View.VISIBLE);
            errorLabel.setText(getStringResource(R.string.connection_error));
        } else {
            selectionLayout.setVisibility(View.GONE);
            tryAgainLayout.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.VISIBLE);
            this.setTitle("Loading...");
            mCompositeDisposable.add(VotingModel.getINSTANCE().getDetailSelection(link)
                    .subscribe(selectionDetailResponse -> {
                        if (selectionDetailResponse.message == null) {
                            selectionLayout.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                            tryAgainLayout.setVisibility(View.GONE);
                            loadingLayout.setVisibility(View.GONE);

                            selectionFacebookId = selectionDetailResponse.mSelection.fbProfile;
                            loadPhoto(selectionDetailResponse.mSelection.photo.get(0));
                            this.setTitle(FontUtils.getConvertedString(selectionDetailResponse.mSelection.name));
                            tvSelectionName.setText(FontUtils.getConvertedString(selectionDetailResponse.mSelection.name));
                            String info = getString(
                                    (Integer.parseInt(selectionDetailResponse.mSelection.gender) == 0) ? R.string.selection_information_male : R.string.selection_information_female,
                                    selectionDetailResponse.mSelection.class_name,
                                    getResources().getStringArray(R.array.gender)[Integer.parseInt(selectionDetailResponse.mSelection.gender)],
                                    getCount(selectionDetailResponse.mSelection.count_one),
                                    getCount(selectionDetailResponse.mSelection.count_two),
                                    getCount(selectionDetailResponse.mSelection.count_three));
                            mPhotoRvAdapter.swipeData(selectionDetailResponse.mSelection.photo);
                            tvSelectionInformation.setText(FontUtils.getConvertedString(info));
                        } else {
                            this.setTitle("Server close");
                            selectionLayout.setVisibility(View.GONE);
                            loadingLayout.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                            tryAgainLayout.setVisibility(View.VISIBLE);
                            errorLabel.setText(FontUtils.getConvertedString(selectionDetailResponse.message));
                        }
                    }, throwable -> {
                        if (throwable instanceof HttpException) {
                            this.setTitle("Error - " + ((HttpException) throwable).code());
                        } else {
                            this.setTitle("Error");
                        }
                        selectionLayout.setVisibility(View.GONE);
                        loadingLayout.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                        tryAgainLayout.setVisibility(View.VISIBLE);
                        errorLabel.setText(throwable.getMessage());
                    })
            );
        }
    }

    private void loadPhoto(String url) {
        GlideApp.with(this)
                .load(url.replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE))
                .thumbnail(0.1f)
                .into(selectionImage);
    }

    private String getCount(String count) {
        String tmp = "";
        for (int index = 0; index < count.length(); index++) {
            tmp += getResources().getStringArray(R.array.mm_text)[Integer.parseInt(count.substring(index, index + 1))];
        }
        return tmp;
    }

    @OnClick(R.id.tv_selection_fb)
    public void onClickFacebook() {
        startActivity(getOpenFacebookIntent(this, selectionFacebookId));
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_selection_detail;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        menu.findItem(R.id.action_clear_voting).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_about_us) {
            startActivity(AboutActivity.getIntent(this));
        }

        return super.onOptionsItemSelected(item);
    }
}
