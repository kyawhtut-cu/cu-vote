package com.kyawhtut.ucstgovoting.network;

import com.kyawhtut.ucstgovoting.network.response.SelectionDetailResponse;
import com.kyawhtut.ucstgovoting.network.response.SelectionResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SelectionApi {

    @POST("v2.api/getAllSelectionList/{api_key}")
    Single<SelectionResponse> getServerStatus(
            @Path("api_key") String api_key
    );

    @POST("v2.api/getSelectionDetail/{api_key}")
    @FormUrlEncoded
    Single<SelectionDetailResponse> getDetailSelection(
            @Path("api_key") String api_key,
            @Field("name") String name
    );
}
