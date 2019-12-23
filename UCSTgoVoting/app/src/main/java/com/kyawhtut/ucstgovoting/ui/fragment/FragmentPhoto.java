package com.kyawhtut.ucstgovoting.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.PhotoViewPagerAdapter;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.ui.activity.HomeActivity;
import com.kyawhtut.ucstgovoting.utils.fonts.FontUtils;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import butterknife.BindView;

public class FragmentPhoto extends BaseFragment {

    @BindView(R.id.photo_view_pager)
    ViewPager mPhotoViewPager;

    @BindView(R.id.indicator)
    CirclePageIndicator mIndicator;

    private Selection mSelection;
    private PhotoViewPagerAdapter mPhotoViewPagerAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String selection_id = getArguments().getString("selection_id");
        ((HomeActivity) getActivity()).getSupportActionBar().show();
        ((HomeActivity) getActivity()).getSupportActionBar().setTitle(FontUtils.getConvertedString(getArguments().getString("title")));
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelection = mAppDatabase.selectionDao().getSelection(selection_id);

        selection_id = mSelection.photo_col;

        mPhotoViewPagerAdapter = new PhotoViewPagerAdapter(getChildFragmentManager());
        List<String> photo = new Gson().fromJson(selection_id, new TypeToken<List<String>>() {
        }.getType());
        for (String s : photo) {
            FragmentPhotoView fragment = new FragmentPhotoView();
            Bundle bundle = new Bundle();
            bundle.putString("photo_url", s);
            fragment.setArguments(bundle);
            mPhotoViewPagerAdapter.addFragment(mSelection.name, fragment);
        }
        mPhotoViewPager.setAdapter(mPhotoViewPagerAdapter);
        mPhotoViewPager.setOffscreenPageLimit(mPhotoViewPagerAdapter.getCount());
        mIndicator.setViewPager(mPhotoViewPager);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_photo;
    }
}
