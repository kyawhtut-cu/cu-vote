package com.kyawhtut.ucstgovotingadmin.network;

import com.kyawhtut.ucstgovotingadmin.network.response.LoginResponse;
import com.kyawhtut.ucstgovotingadmin.network.response.VotingResponse;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    @POST("voting/v2.api/updateVotingUser/{api_key}")
    @FormUrlEncoded
    Single<VotingResponse> votedSelection(
            @Path("api_key") String api_key,
            @Field("king_id") String king_id,
            @Field("queen_id") String queen_id,
            @Field("att_boy_id") String att_boy_id,
            @Field("att_girl_id") String att_girl_id,
            @Field("innocence_id") String innocence_id,
            @Field("token") String token
            /*@Field("innocence_boy_id") String innocence_boy_id,
            @Field("innocence_girl_id") String innocence_girl_id*/
    );

    @POST("voting/v2.api/loginUser/{api_key}")
    @FormUrlEncoded
    Single<LoginResponse> loginUser(
            @Path("api_key") String api_key,
            @Field("email") String email,
            @Field("password") String password
    );
}
