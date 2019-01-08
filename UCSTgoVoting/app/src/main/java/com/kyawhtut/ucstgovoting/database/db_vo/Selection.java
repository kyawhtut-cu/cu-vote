package com.kyawhtut.ucstgovoting.database.db_vo;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.kyawhtut.ucstgovoting.utils.Utils;

import java.util.List;

@Entity(tableName = Utils.SELECTION_TABLE)
public class Selection {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    @ColumnInfo(name = "id")
    public String id;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    public String name;

    @SerializedName("gender")
    @ColumnInfo(name = "gender")
    public String gender;

    @SerializedName("selection_id")
    @ColumnInfo(name = "selection_id")
    public String selection_id;

    @SerializedName("class")
    @ColumnInfo(name = "class")
    public String class_name;

    @SerializedName("fbProfile")
    @ColumnInfo(name = "fbProfile")
    public String fbProfile;

    @SerializedName("count_one")
    @ColumnInfo(name = "count_one")
    public String count_one;

    @SerializedName("count_two")
    @ColumnInfo(name = "count_two")
    public String count_two;

    @SerializedName("count_three")
    @ColumnInfo(name = "count_three")
    public String count_three;

    @Ignore
    @SerializedName("photo")
    public List<String> photo;

    @ColumnInfo(name = "photo")
    public String photo_col;

    @Ignore
    public boolean isSelected;

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Ignore
    public Selection() {
    }

    @Ignore
    public Selection(String id, String name, String gender, String selection_id, String class_name, String fbProfile, String count_one, String count_two, String count_three, List<String> photo) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.selection_id = selection_id;
        this.class_name = class_name;
        this.fbProfile = fbProfile;
        this.count_one = count_one;
        this.count_two = count_two;
        this.count_three = count_three;
        this.photo = photo;
    }

    @Ignore
    public Selection(String selection_id) {
        this.selection_id = selection_id;
    }

    public Selection(String id, String name, String gender, String selection_id, String class_name, String fbProfile, String count_one, String count_two, String count_three, String photo_col) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.selection_id = selection_id;
        this.class_name = class_name;
        this.fbProfile = fbProfile;
        this.count_one = count_one;
        this.count_two = count_two;
        this.count_three = count_three;
        this.photo_col = photo_col;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return ((Selection) obj).selection_id.equals(this.selection_id);
    }
}
