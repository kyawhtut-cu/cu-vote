package com.kyawhtut.ucstgovoting.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.SelectionRvAdapter;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListenerCallBack;
import com.kyawhtut.ucstgovoting.adapter.viewholders.SelectionViewHolder;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.ui.activity.AboutActivity;
import com.kyawhtut.ucstgovoting.ui.activity.HomeActivity;
import com.kyawhtut.ucstgovoting.ui.view.ItemsCountView;
import com.kyawhtut.ucstgovoting.utils.GlideApp;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;
import com.kyawhtut.ucstgovoting.utils.Utils;
import com.kyawhtut.ucstgovoting.utils.discretescroll.DSVOrientation;
import com.kyawhtut.ucstgovoting.utils.discretescroll.DiscreteScrollView;
import com.kyawhtut.ucstgovoting.utils.discretescroll.transform.ScaleTransformer;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

public class FragmentSelectionList extends BaseFragment {

    public boolean isSelected = false;
    @BindView(R.id.selection_rv)
    DiscreteScrollView mSelectionRv;
    @BindView(R.id.selection_count)
    ItemsCountView mSelectionCount;
    @BindView(R.id.ec_bg_switcher_element)
    ImageView mEcBgSwitcherElement;
    private SelectionRvAdapter mRvAdapter;
    private String type;
    private DialogFragmentBlur dialog;
    private int mOldPosition = 0;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        type = getArguments().getString("title");
        ((HomeActivity) getActivity()).getSupportActionBar().hide();
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle(type);
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mEcBgSwitcherElement.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            mEcBgSwitcherElement.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

        mRvAdapter = new SelectionRvAdapter(getContext(), new DefaultItemClickListenerCallBack<Selection>() {

            @Override
            public void onDialogDismiss() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onDialogShow(String url) {
                dialog = new DialogPhotoPreviewFragment().newInstance(
                        32,
                        4,
                        false,
                        false,
                        url
                );
                dialog.show(getActivity().getFragmentManager(), "");
            }

            @Override
            public void onClickPhoto(Selection data, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("title", data.name);
                bundle.putString("selection_id", data.selection_id);
                ((HomeActivity) getActivity()).changeFrameLayout(
                        new FragmentPhoto(),
                        getStringResource(R.string.fragment_photo),
                        bundle
                );
            }

            @Override
            public void onClickFavourite(Selection data, int position) {
                isSelected = true;
                int oldSelectedPosition = -1;
                Selection s = null;
                for (int index = 0; index < mRvAdapter.getAllData().size() && oldSelectedPosition == -1; index++) {
                    if (mRvAdapter.getData(index).isSelected) {
                        oldSelectedPosition = index;
                        s = mRvAdapter.getData(index);
                        s.isSelected = false;
                    }
                }
                if (s != null)
                    mRvAdapter.setData(oldSelectedPosition, s);
                data.isSelected = true;
                mRvAdapter.setData(position, data);
                ((HomeActivity) getActivity()).showVotingConfirmDialog(data, type);
            }

            @Override
            public void onClickFacebook(Selection data, int position) {
                startActivity(AboutActivity.getOpenFacebookIntent(getContext(), data.fbProfile));
            }
        });

        mSelectionRv.setOrientation(DSVOrientation.HORIZONTAL);
        mSelectionRv.addOnItemChangedListener((viewHolder, adapterPosition) -> Timber.i("Current Position : %d", adapterPosition));
        mSelectionRv.setAdapter(mRvAdapter);
        mSelectionRv.setItemTransitionTimeMillis(150);
        mSelectionRv.addScrollStateChangeListener(new DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                mOldPosition = adapterPosition;
            }

            @Override
            public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                if (adapterPosition != mOldPosition) {
                    mSelectionCount.update(adapterPosition, mOldPosition, mRvAdapter.getItemCount());
                    updateCurrentBackground(((SelectionViewHolder) currentItemHolder).getCurrentPhoto());
                }
            }

            @Override
            public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

            }
        });
        mSelectionRv.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        bindData();
    }

    private void updateCurrentBackground(String photoUrl) {
        GlideApp.with(getContext())
                .load(photoUrl)
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ucst_logo)
                .fitCenter()
                .into(mEcBgSwitcherElement);
    }

    private void bindData() {
        LiveData<List<Selection>> sLiveData;
        if (type.equals(getStringResource(R.string.fragment_selection_list_king)) ||
                type.equals(getStringResource(R.string.fragment_selection_list_attractive_boy)) ||
                type.equals(getStringResource(R.string.fragment_selection_list_innocence_boy))) {
            sLiveData = mAppDatabase.selectionDao().getOtherList(
                    0,
                    mSelectionUtil.getString(SelectionUtil.KING_ID),
                    mSelectionUtil.getString(SelectionUtil.ATTRACTIVE_BOY_ID),
                    mSelectionUtil.getString(SelectionUtil.INNOCENCE_ID)
            );
        } else if (type.equals(getStringResource(R.string.fragment_selection_list_queen)) ||
                type.equals(getStringResource(R.string.fragment_selection_list_attractive_girl)) ||
                type.equals(getStringResource(R.string.fragment_selection_list_innocence_girl))) {
            sLiveData = mAppDatabase.selectionDao().getOtherList(
                    1,
                    mSelectionUtil.getString(SelectionUtil.QUEEN_ID),
                    mSelectionUtil.getString(SelectionUtil.ATTRACTIVE_GIRL_ID),
                    mSelectionUtil.getString(SelectionUtil.INNOCENCE_ID)
            );
        } else {
            sLiveData = mAppDatabase.selectionDao().getInnocenceList(
                    mSelectionUtil.getString(SelectionUtil.KING_ID),
                    mSelectionUtil.getString(SelectionUtil.QUEEN_ID),
                    mSelectionUtil.getString(SelectionUtil.ATTRACTIVE_BOY_ID),
                    mSelectionUtil.getString(SelectionUtil.ATTRACTIVE_GIRL_ID)
            );
        }
        sLiveData.observe(this, selections -> {
            if (((HomeActivity) getActivity()).mLastSelectedId != null) {
                int index = selections.indexOf(new Selection(((HomeActivity) getActivity()).mLastSelectedId));
                selections.get(index).isSelected = true;
            }
            mRvAdapter.swipeData(selections);
            mSelectionCount.update(0, mOldPosition, mRvAdapter.getItemCount());
            List<String> photo = new Gson().fromJson(selections.get(0).photo_col, new TypeToken<List<String>>() {
            }.getType());
            updateCurrentBackground((photo != null && photo.size() > 0) ? photo.get(0).replace("../", Utils.BASE_URL + Utils.PHOTO_RESOURCE) : "");
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_selection_list;
    }

    @OnClick(R.id.home)
    public void onClickHome() {
        ((HomeActivity) getActivity()).checkFragment();
    }
}
