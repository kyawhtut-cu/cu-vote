package com.kyawhtut.ucstgovotingadmin.model;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.kyawhtut.ucstgovotingadmin.BuildConfig;
import com.kyawhtut.ucstgovotingadmin.network.Api;
import com.kyawhtut.ucstgovotingadmin.network.response.LoginResponse;
import com.kyawhtut.ucstgovotingadmin.network.response.VotingResponse;
import com.kyawhtut.ucstgovotingadmin.utils.UnsafeOkHttpClient;
import com.kyawhtut.ucstgovotingadmin.utils.Utils;

import java.util.List;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminModel {

    public static AdminModel INSTANCE;

    private Api mApi;

    public static AdminModel getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new AdminModel();
        }
        return INSTANCE;
    }

    private AdminModel() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mApi = retrofit.create(Api.class);
    }

    public Single<VotingResponse> votedSelection(List<String> selectionId) {
        return this.mApi.votedSelection(
                BuildConfig.API_KEY,
                selectionId.get(0),
                selectionId.get(1),
                selectionId.get(2),
                selectionId.get(3),
                selectionId.get(4),
                selectionId.get(5)
                /*, selectionId.get(6)*/
        );
    }

    public Single<LoginResponse> loginUser(String email, String password) {
        return this.mApi.loginUser(
                BuildConfig.API_KEY,
                email,
                password
        );
    }
}
