package com.kyawhtut.ucstgovoting.adapter.clicklistener;

public interface DefaultItemClickListener<D> {

    void onClickItem(D data, int position);

    void onDialogDismiss();

    void onDialogShow(String url);

    void onClickPhoto(D data, int position);

    void onClickFavourite(D data, int position);

    void onClickFacebook(D data, int position);
}
