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
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogQRFragment;
import com.kyawhtut.ucstgovoting.ui.fragment.FragmentSelection;
import com.kyawhtut.ucstgovoting.ui.fragment.FragmentSelectionList;
import com.kyawhtut.ucstgovoting.ui.fragment.DialogVotingConfirmFragment;
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
import timber.log.Timber;

public class HomeActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    Bundle bundle = new Bundle();

    private String mDialogMessage;
    private String mSelectionPhoto;
    public String mSelectionId;

    @BindView(R.id.fab_finish)
    FloatingActionButton mFabFinish;

    public static final int PERMISSION_ACCESS_FILE_READ_WRITE_SAVE = 0x10;
    public static final int PERMISSION_ACCESS_FILE_READ_WRITE_SHARE = 0x11;
    private Bitmap mBitmap;
    private PermissionListener mPermissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bundle.putString("title", getStringResource(R.string.fragment_home));
        changeFrameLayout(
                new FragmentSelection(),
                getStringResource(R.string.fragment_home),
                bundle
        );
        hideFab();
    }

    public void hideFab() {
        mFabFinish.hide();
    }

    public void showFab() {
        mFabFinish.show();
    }

    @OnClick(R.id.fab_finish)
    public void onClickFab(View view) {
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
                            mSelectionUtil.setString(key, mSelectionId);
                            dialog.dismiss();
                            mSelectionId = null;
                            HomeActivity.super.onBackPressed();
                        }

                        @Override
                        public void onClickCancel() {
                            mSelectionId = null;
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
        mSelectionId = selection.selection_id;
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
}
