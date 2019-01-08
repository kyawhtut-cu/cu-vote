package com.kyawhtut.ucstgovoting.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.kyawhtut.ucstgovoting.database.dao.SelectionDao;
import com.kyawhtut.ucstgovoting.database.db_vo.Selection;
import com.kyawhtut.ucstgovoting.utils.Utils;

@Database(entities = {Selection.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SelectionDao selectionDao();

    public static AppDatabase INSTANCE;

    public static AppDatabase getINSTANCE(Context ctx) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(ctx, AppDatabase.class, Utils.DB_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public void destoryDB() {
        INSTANCE = null;
    }

}
