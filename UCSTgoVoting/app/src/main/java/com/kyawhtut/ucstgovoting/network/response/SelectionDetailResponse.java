package com.kyawhtut.ucstgovoting.network.response;

import com.google.gson.annotations.SerializedName;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;

public class SelectionDetailResponse {

    @SerializedName(value = "message")
    public String message;

    @SerializedName("selection")
    public Selection mSelection;
}
