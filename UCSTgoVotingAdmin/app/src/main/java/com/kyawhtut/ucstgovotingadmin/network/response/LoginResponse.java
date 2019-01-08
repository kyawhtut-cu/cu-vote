package com.kyawhtut.ucstgovotingadmin.network.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("message")
    public String message;

    public LoginResponse(String message) {
        this.message = message;
    }
}
