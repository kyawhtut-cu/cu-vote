package com.kyawhtut.ucstgovoting.network;

import com.kyawhtut.ucstgovoting.network.response.SelectionResponse;

import io.reactivex.Single;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SelectionApi {

    @POST("v1.api/getAllSelectionList/{api_key}")
    Single<SelectionResponse> getServerStatus(
            @Path("api_key") String api_key
    );
}
