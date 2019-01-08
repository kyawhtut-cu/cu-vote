package com.kyawhtut.ucstgovoting.network.response;

import com.google.gson.annotations.SerializedName;

public class VotingResponse {

    @SerializedName("message")
    public String message;

    public VotingResponse(String message) {
        this.message = message;
    }
}
