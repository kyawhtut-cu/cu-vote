package com.kyawhtut.ucstgovoting.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.kyawhtut.ucstgovoting.R;
import com.kyawhtut.ucstgovoting.adapter.ChooseSelectionRvAdapter;
import com.kyawhtut.ucstgovoting.adapter.clicklistener.DefaultItemClickListenerCallBack;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.ui.activity.HomeActivity;
import com.kyawhtut.ucstgovoting.utils.SelectionUtil;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class FragmentSelection extends BaseFragment {

    @BindView(R.id.selection_view_rv)
    DiscreteScrollView mSelectionViewRv;

    private ChooseSelectionRvAdapter mAdapter;
    private List<Selection> mSelectionList = new ArrayList<>();
    private DialogFragmentBlur dialog;

    @BindArray(R.array.label_for_selection)
    String[] mLabelForSelection;

    private Bundle mBundle = new Bundle();

    private CompositeDisposable mCompositeDisposable;

    @Override
    protected int getLayout() {
        return R.layout.fragment_selection_view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCompositeDisposable = new CompositeDisposable();

        ((HomeActivity) getActivity()).getSupportActionBar().setTitle(getStringResource(R.string.fragment_home));
        ((HomeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((HomeActivity) getActivity()).getSupportActionBar().show();

        mAdapter = new ChooseSelectionRvAdapter(getContext(), new DefaultItemClickListenerCallBack<Selection>() {
            @Override
            public void onClickItem(Selection data, int position) {
                Timber.i("Position : %s", position);
                if (data.photo == null) {
                    switch (position) {
                        case 0:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_king));
                            ((HomeActivity) getActivity()).changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_king), mBundle);
                            break;
                        case 1:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_queen));
                            ((HomeActivity) getActivity()).changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_queen), mBundle);
                            break;
                        case 2:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_attractive_boy));
                            ((HomeActivity) getActivity()).changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_attractive_boy), mBundle);
                            break;
                        case 3:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_attractive_girl));
                            ((HomeActivity) getActivity()).changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_attractive_girl), mBundle);
                            break;
                        case 4:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_innocence_boy));
                            ((HomeActivity) getActivity()).changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_innocence_boy), mBundle);
                            break;
                        case 5:
                            mBundle.putString("title", getStringResource(R.string.fragment_selection_list_innocence_girl));
                            ((HomeActivity) getActivity()).changeFrameLayout(new FragmentSelectionList(), getStringResource(R.string.fragment_selection_list_innocence_girl), mBundle);
                            break;
                    }
                }
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
        });

        mSelectionViewRv.setOrientation(DSVOrientation.VERTICAL);
        mSelectionViewRv.setAdapter(mAdapter);
        mSelectionViewRv.setItemTransitionTimeMillis(150);
        mSelectionViewRv.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        bindSelection();
    }

    private void bindSelection() {
        mSelectionList.clear();
        if (isEmpty(SelectionUtil.KING_ID) && isEmpty(SelectionUtil.QUEEN_ID) && isEmpty(SelectionUtil.ATTRACTIVE_BOY_ID) && isEmpty(SelectionUtil.ATTRACTIVE_GIRL_ID) &&
                isEmpty(SelectionUtil.INNOCENCE_BOY_ID) && isEmpty(SelectionUtil.INNOCENCE_GIRL_ID)) {
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
        bindSelection(SelectionUtil.INNOCENCE_BOY_ID, 4);
        bindSelection(SelectionUtil.INNOCENCE_GIRL_ID, 5);

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
            mSelectionUtil.clearValue();
            bindSelection();
        }
        return true;
    }
}