package com.kyawhtut.ucstgovoting.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.utils.Utils;

import java.util.List;

@Dao
public interface SelectionDao {

    @Query("select * from " + Utils.SELECTION_TABLE + " where selection_id<>:king_id AND selection_id<>:queen_id AND selection_id<>:attractive_boy_id AND selection_id<>:attractive_girl_id")
    LiveData<List<Selection>> getInnocenceList(
            String king_id,
            String queen_id,
            String attractive_boy_id,
            String attractive_girl_id
    );

    @Query("select * from " + Utils.SELECTION_TABLE + " where gender = :gender AND selection_id<>:first_id AND selection_id<>:second_id AND selection_id<>:third_id")
    LiveData<List<Selection>> getOtherList(
            int gender,
            String first_id,
            String second_id,
            String third_id
    );

    @Query("select * from " + Utils.SELECTION_TABLE + " where selection_id=:selection_id")
    Selection getSelection(String selection_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertSelection(List<Selection> selectionList);

    @Query("select count(*) from " + Utils.SELECTION_TABLE)
    int selectionCount();

    @Query("DELETE FROM " + Utils.SELECTION_TABLE)
    void deleteAll();
}
