package com.kyawhtut.ucstgovoting.model;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.kyawhtut.ucstgovoting.BuildConfig;
import com.kyawhtut.ucstgovoting.network.SelectionApi;
import com.kyawhtut.ucstgovoting.network.response.SelectionDetailResponse;
import com.kyawhtut.ucstgovoting.network.response.SelectionResponse;
import com.kyawhtut.ucstgovoting.utils.Utils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VotingModel {

    private static VotingModel INSTANCE;

    private SelectionApi mApi;

    private VotingModel() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mApi = retrofit.create(SelectionApi.class);
    }

    public static VotingModel getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new VotingModel();
        }
        return INSTANCE;
    }

    public Single<SelectionResponse> getServerStatus() {
        return this.mApi.getServerStatus(BuildConfig.API_KEY);
    }

    public Single<SelectionDetailResponse> getDetailSelection(String name) {
        return this.mApi.getDetailSelection(
                BuildConfig.API_KEY,
                name
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
