package com.kyawhtut.ucstgovoting.network.response;

import com.google.gson.annotations.SerializedName;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;

import java.util.List;

public class SelectionResponse {

    @SerializedName("selection")
    public List<Selection> selectionList;

    @SerializedName("message")
    public String message;

    public SelectionResponse(List<Selection> selectionList) {
        this.selectionList = selectionList;
    }

    public SelectionResponse(String message) {
        this.message = message;
    }
}
