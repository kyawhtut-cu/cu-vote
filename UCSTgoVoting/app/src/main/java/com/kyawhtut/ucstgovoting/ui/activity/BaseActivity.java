package com.kyawhtut.ucstgovoting.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.database.AppDatabase;
import com.kyawhtut.ucstgovoting.utils.SelectionInjection;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    protected SelectionUtil mSelectionUtil;
    protected AppDatabase mAppDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this, this);

        setSupportActionBar(mToolbar);

        mSelectionUtil = SelectionInjection.provideSelection(this);
        mAppDatabase = AppDatabase.getINSTANCE(this);
    }

    protected String getStringResource(int resource) {
        return getResources().getString(resource);
    }

    protected abstract int getLayout();
}
