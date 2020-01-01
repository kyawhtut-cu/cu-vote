package com.kyawhtut.ucstgovoting.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.ChooseSelectionRvAdapter;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListenerCallBack;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.ui.activity.HomeActivity;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;
import com.kyawhtut.ucstgovoting.utils.discretescroll.DSVOrientation;
import com.kyawhtut.ucstgovoting.utils.discretescroll.DiscreteScrollView;
import com.kyawhtut.ucstgovoting.utils.discretescroll.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

public class FragmentSelection extends BaseFragment {

    @BindView(R.id.selection_view_rv)
    DiscreteScrollView mSelectionViewRv;
    @BindArray(R.array.label_for_selection)
    String[] mLabelForSelection;
    private ChooseSelectionRvAdapter mAdapter;
    private List<Selection> mSelectionList = new ArrayList<>();

    private CompositeDisposable mCompositeDisposable;
    private DefaultItemClickListenerCallBack<Selection> mListener;

    @Override
    protected int getLayout() {
        return R.layout.fragment_selection_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public FragmentSelection addListener(DefaultItemClickListenerCallBack<Selection> listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCompositeDisposable = new CompositeDisposable();

        ((HomeActivity) getActivity()).getSupportActionBar().setTitle(getStringResource(R.string.fragment_home));
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((HomeActivity) getActivity()).getSupportActionBar().show();

        mAdapter = new ChooseSelectionRvAdapter(getContext(), mListener);

        mSelectionViewRv.setOrientation(DSVOrientation.VERTICAL);
        mSelectionViewRv.setAdapter(mAdapter);
        mSelectionViewRv.setItemTransitionTimeMillis(150);
        mSelectionViewRv.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        bindSelection();
    }

    private boolean isEmptyInnocenceID() {
        return isEmpty(SelectionUtil.INNOCENCE_ID);
//        return isEmpty(SelectionUtil.INNOCENCE_BOY_ID) && isEmpty(SelectionUtil.INNOCENCE_GIRL_ID);
    }

    private void bindSelection() {
        mSelectionList.clear();
        if (isEmpty(SelectionUtil.KING_ID) && isEmpty(SelectionUtil.QUEEN_ID) && isEmpty(SelectionUtil.ATTRACTIVE_BOY_ID) && isEmpty(SelectionUtil.ATTRACTIVE_GIRL_ID) &&
                isEmptyInnocenceID()) {
            ((HomeActivity) getActivity()).showFab();
            setHasOptionsMenu(true);
        } else {
            ((HomeActivity) getActivity()).hideFab();
            setHasOptionsMenu(false);
        }
        bindSelection(SelectionUtil.KING_ID, 0);
        bindSelection(SelectionUtil.QUEEN_ID, 1);
        bindSelection(SelectionUtil.ATTRACTIVE_BOY_ID, 2);
        bindSelection(SelectionUtil.ATTRACTIVE_GIRL_ID, 3);
        bindSelection(SelectionUtil.INNOCENCE_ID, 4);
        /*bindSelection(SelectionUtil.INNOCENCE_BOY_ID, 4);
        bindSelection(SelectionUtil.INNOCENCE_GIRL_ID, 5);*/

        mAdapter.swipeData(mSelectionList);
    }

    private boolean isEmpty(String key) {
        return !TextUtils.isEmpty(mSelectionUtil.getString(key));
    }

    private void bindSelection(String key, int pos) {
        Selection selection = mAppDatabase.selectionDao().getSelection(mSelectionUtil.getString(key));
        if (selection == null) {
            selection = new Selection();
            selection.name = mLabelForSelection[pos];
        } else
            selection.name = selection.name + "(" + mLabelForSelection[pos] + ")";
        mSelectionList.add(selection);
    }

    @Override
    public void onStop() {
        super.onStop();
        mCompositeDisposable.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        menu.findItem(R.id.action_about_us).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear_voting) {
            clearVoting();
        }
        return true;
    }

    public void clearVoting() {
        mSelectionUtil.clearValue();
        bindSelection();
    }
}