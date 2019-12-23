package com.kyawhtut.ucstgovoting.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mFragmentTitle;

    public PhotoViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mFragmentTitle = new ArrayList<>();
    }

    public void addFragment(String title, Fragment fragment) {
        this.mFragmentTitle.add(title);
        this.mFragmentList.add(fragment);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentList.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.mFragmentTitle.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}
