package com.kyawhtut.ucstgovoting.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kyawhtut.ucstgovoting.database.AppDatabase;
import com.kyawhtut.ucstgovoting.utils.SelectionInjection;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    protected SelectionUtil mSelectionUtil;
    protected AppDatabase mAppDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, view);

        mAppDatabase = AppDatabase.getINSTANCE(getContext());
        mSelectionUtil = SelectionInjection.provideSelection(getContext());

        return view;
    }

    protected String getStringResource(int resource) {
        return getContext().getResources().getString(resource);
    }

    protected abstract int getLayout();
}
