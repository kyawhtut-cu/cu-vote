package com.kyawhtut.ucstgovoting.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListenerCallBack;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogFragmentBlur;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogPhotoPreviewFragment;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogQRFragment;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogVotingConfirmFragment;
import com.kyawhtut.ucstgovoting.ui.fragment.FragmentPhoto;
import com.kyawhtut.ucstgovoting.ui.fragment.FragmentSelection;
import com.kyawhtut.ucstgovoting.ui.fragment.FragmentSelectionList;
import com.kyawhtut.ucstgovoting.utils.DateUtils;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;
import com.kyawhtut.ucstgovoting.utils.fonts.FontUtils;
import com.kyawhtut.ucstgovoting.utils.permission.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import shortbread.Shortcut;
import timber.log.Timber;

public class HomeActivity extends BaseActivity {

    public static final int PERMISSION_ACCESS_FILE_READ_WRITE_SAVE = 0x10;
    public static final int PERMISSION_ACCESS_FILE_READ_WRITE_SHARE = 0x11;
    public String mLastSelectedId;
    @BindView(R.id.fab_finish)
    FloatingActionButton mFabFinish;
    private String mDialogMessage;
    private String mSelectionPhoto;
    private Bitmap mBitmap;
    private PermissionListener mPermissionListener;
    private Bundle mBundle = new Bundle();

    private DialogFragmentBlur mBlurDialog;

    public static Intent getIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBundle.putString("title", getStringResource(R.string.fragment_home));
        changeFrameLayout(
                getFragmentSelection(),
                getStringResource(R.string.fragment_home),
                mBundle
        );
        hideFab();
    }

    private FragmentSelection getFragmentSelection() {
        return new FragmentSelection().addListener(new DefaultItemClickListenerCallBack<Selection>() {
            @Override
            public void onClickPhoto(Selection data, int position) {
                previewPhoto(data.selection_id);
            }

            @Override
            public void onClickItem(Selection data, int position) {
                if (data.photo == null) {
                    switch (position) {
                        case 0:
                            kingClick();
                            break;
                        case 1:
                            queenClick();
                            break;
                        case 2:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_attractive_boy));
                            changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_attractive_boy), mBundle);
                            break;
                        case 3:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_attractive_girl));
                            changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_attractive_girl), mBundle);
                            break;
                        case 4:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_innocence_boy));
                            changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_innocence_boy), mBundle);
                            break;
                        case 5:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_innocence_girl));
                            changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_innocence_girl), mBundle);
                            break;
                    }
                }
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
    }

    public void hideFab() {
        mFabFinish.hide();
    }

    public void showFab() {
        mFabFinish.show();
    }

    @OnClick(R.id.fab_finish)
    public void onClickFab() {
        new DialogQRFragment().newInstance(
                16,
                4,
                false,
                false
        ).show(getFragmentManager(), "blur_sample");
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_home;
    }

    public void changeFrameLayout(Fragment fragment, String tag, Bundle bundle) {
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        checkFragment();
    }

    public void checkFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            String tag = getSupportFragmentManager().findFragmentById(R.id.content_frame).getTag();
            if (tag.equals(getStringResource(R.string.fragment_selection_list_king)) ||
                    tag.equals(getStringResource(R.string.fragment_selection_list_queen)) ||
                    tag.equals(getStringResource(R.string.fragment_selection_list_attractive_boy)) ||
                    tag.equals(getStringResource(R.string.fragment_selection_list_attractive_girl)) ||
                    tag.equals(getStringResource(R.string.fragment_selection_list_innocence_boy)) ||
                    tag.equals(getStringResource(R.string.fragment_selection_list_innocence_girl))) {
                if (((FragmentSelectionList) getSupportFragmentManager().findFragmentById(R.id.content_frame)).isSelected) {
                    new DialogVotingConfirmFragment().setListener(new DialogVotingConfirmFragment.DialogButtonClickListener() {
                        @Override
                        public void onClickOk(DialogInterface dialog) {
                            String key = getSupportActionBar().getTitle().toString();
                            if (key.equals(getStringResource(R.string.fragment_selection_list_king))) {
                                key = SelectionUtil.KING_ID;
                            } else if (key.equals(getStringResource(R.string.fragment_selection_list_queen))) {
                                key = SelectionUtil.QUEEN_ID;
                            } else if (key.equals(getStringResource(R.string.fragment_selection_list_attractive_boy))) {
                                key = SelectionUtil.ATTRACTIVE_BOY_ID;
                            } else if (key.equals(getStringResource(R.string.fragment_selection_list_attractive_girl))) {
                                key = SelectionUtil.ATTRACTIVE_GIRL_ID;
                            } else if (key.equals(getStringResource(R.string.fragment_selection_list_innocence_boy))) {
                                key = SelectionUtil.INNOCENCE_BOY_ID;
                            } else if (key.equals(getStringResource(R.string.fragment_selection_list_innocence_girl))) {
                                key = SelectionUtil.INNOCENCE_GIRL_ID;
                            }
                            mSelectionUtil.setString(key, mLastSelectedId);
                            dialog.dismiss();
                            mLastSelectedId = null;
                            HomeActivity.super.onBackPressed();
                        }

                        @Override
                        public void onClickCancel() {
                            mLastSelectedId = null;
                            HomeActivity.super.onBackPressed();
                        }
                    }).newInstance(
                            16,
                            4,
                            false,
                            false,
                            mDialogMessage,
                            mSelectionPhoto
                    ).show(getFragmentManager(), "Confirm Dialog");
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    public void showVotingConfirmDialog(Selection selection, String type) {
        List<String> photo = new Gson().fromJson(selection.photo_col, new TypeToken<List<String>>() {
        }.getType());
        mDialogMessage = FontUtils.getConvertedString(getResources().getString(R.string.selection_ask_msg, selection.name, type));
        mSelectionPhoto = (photo != null && photo.size() > 0) ? photo.get(0) : "";
        mLastSelectedId = selection.selection_id;
    }

    public void saveQR(Bitmap bitmap, PermissionListener listener) {
        mBitmap = bitmap;
        mPermissionListener = listener;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_ACCESS_FILE_READ_WRITE_SAVE);
        } else {
            saveQRImage(bitmap, listener);
        }
    }

    private void saveQRImage(Bitmap bitmap, PermissionListener listener) {
        File fileDirectory = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "ucstgo_voting_qr");
        if (!fileDirectory.exists())
            fileDirectory.mkdir();
        File imgPath = new File(fileDirectory, System.currentTimeMillis() + ".png");
        if (!imgPath.exists()) {
            try {
                imgPath.createNewFile();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(imgPath));
                if (TextUtils.isEmpty(imgPath.getPath())) {
                    listener.onError("Error saving screen shoot");
                } else {
                    listener.onSuccess(imgPath.getPath());
                }
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }

    public void shareQR(Bitmap bitmap, PermissionListener listener) {
        mBitmap = bitmap;
        mPermissionListener = listener;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_ACCESS_FILE_READ_WRITE_SHARE);
        } else {
            shareQRImage(getImageUri(bitmap));
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, new ByteArrayOutputStream());
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, DateUtils.getCurrentTimeString(), null));
    }

    private void shareQRImage(Uri uri) {
        Intent shareIntent = new Intent("android.intent.action.SEND");
        shareIntent.setType("image/jpg");
        shareIntent.putExtra("android.intent.extra.STREAM", uri);
        startActivity(Intent.createChooser(shareIntent, "Share image using"));
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
            checkFragment();
        } else if (id == R.id.action_about_us) {
            startActivity(AboutActivity.getIntent(this));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_ACCESS_FILE_READ_WRITE_SAVE) {
                saveQRImage(mBitmap, mPermissionListener);
            } else if (requestCode == PERMISSION_ACCESS_FILE_READ_WRITE_SHARE) {
                shareQRImage(getImageUri(mBitmap));
            }
        } else {
            Timber.e("Permission denied");
            mPermissionListener.onError("Permission denied");
        }
    }

    @Shortcut(id = "king", rank = 3, icon = R.drawable.ic_shortcut_voting_test, shortLabel = "King")
    public void kingClick() {
        if (hasData()) {
            if (TextUtils.isEmpty(mSelectionUtil.getString(SelectionUtil.KING_ID))) {
                Bundle bundle = new Bundle();
                bundle.putString("title", getStringResource(R.string.fragment_selection_list_king));
                changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_king), bundle);
            } else {
                previewPhoto(mSelectionUtil.getString(SelectionUtil.KING_ID));
            }
        } else {
            finish();
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }

    @Shortcut(id = "queen", rank = 2, icon = R.drawable.ic_shortcut_voting_test, shortLabel = "Queen")
    public void queenClick() {
        if (hasData()) {
            if (TextUtils.isEmpty(mSelectionUtil.getString(SelectionUtil.QUEEN_ID))) {
                mBundle.putString("title", getStringResource(R.string.fragment_selection_list_queen));
                changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_king), mBundle);
            } else {
                previewPhoto(mSelectionUtil.getString(SelectionUtil.QUEEN_ID));
            }
        } else {
            finish();
            startActivity(new Intent(this, WelcomeActivity.class));
        }
    }

    @Shortcut(id = "all_selection", rank = 1, icon = R.drawable.ic_more_selection, shortLabel = "More Selection")
    public void selectionAll() {
        if (!hasData()) {
            finish();
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        if (isEmpty(SelectionUtil.KING_ID) && isEmpty(SelectionUtil.QUEEN_ID) && isEmpty(SelectionUtil.ATTRACTIVE_BOY_ID) && isEmpty(SelectionUtil.ATTRACTIVE_GIRL_ID) &&
                isEmpty(SelectionUtil.INNOCENCE_BOY_ID) && isEmpty(SelectionUtil.INNOCENCE_GIRL_ID)) {
            showFab();
            onClickFab();
        }
    }

    private void previewPhoto(String key) {
        Selection data = mAppDatabase.selectionDao().getSelection(key);
        Bundle bundle = new Bundle();
        bundle.putString("title", data.name);
        bundle.putString("selection_id", data.selection_id);
        changeFrameLayout(
                new FragmentPhoto(),
                getStringResource(R.string.fragment_photo),
                bundle
        );
    }

    private boolean hasData() {
        return mAppDatabase.selectionDao().selectionCount() != 0;
    }

    private boolean isEmpty(String key) {
        return !TextUtils.isEmpty(mSelectionUtil.getString(key));
    }
}
