package com.example.mycarparksearch;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class SQLiteControl extends SQLiteOpenHelper {

    public SQLiteControl(Context context) {
        super(context, "Database", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFavorite = "CREATE TABLE `Favorite` "
                + "(CarParkNo TEXT PRIMARY KEY, isFavorite INTEGER)";
        String createRating = "CREATE TABLE `Rating` "
                + "(CarParkNo TEXT PRIMARY KEY, Rating REAL, Comment TEXT)";
        db.execSQL(createFavorite);
        db.execSQL(createRating);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String dropFavorite = "DROP TABLE IF EXISTS `Favorite`";
        String dropRating = "DROP TABLE IF EXISTS `Rating`";
        db.execSQL(dropFavorite);
        db.execSQL(dropRating);
        onCreate(db);
    }

    public void updateFavorite(String carParkNo, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        int isFavoriteInt = isFavorite ? 1: 0;
        String updateFavoriteSQL = "INSERT OR REPLACE INTO `Favorite` (CarParkNo, isFavorite) "
                + " VALUES (\"" + carParkNo + "\", " + isFavoriteInt + ")";
        db.execSQL(updateFavoriteSQL);
    }

    public boolean getFavorite(String carParkNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String getFavoriteSQL = "SELECT isFavorite FROM `Favorite` WHERE CarParkNo = \"" + carParkNo + "\"";
        Cursor cursor = db.rawQuery(getFavoriteSQL, null);
        if (cursor.moveToNext()) {
            int isFavoriteInt = cursor.getInt(0);
            cursor.close();
            if (isFavoriteInt == 1) {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public void updateRating(String carParkNo, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateFavoriteSQL = "INSERT OR REPLACE INTO `Rating` (CarParkNo, Rating, Comment) "
                + " VALUES (\"" + carParkNo + "\", " + rating + ", \"" + comment + "\")";
        db.execSQL(updateFavoriteSQL);
    }

    public ArrayList<Object> getRating(String carParkNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String getFavoriteSQL = "SELECT Rating, Comment FROM `Rating` WHERE CarParkNo = \"" + carParkNo + "\"";
        Cursor cursor = db.rawQuery(getFavoriteSQL, null);
        if (cursor.moveToNext()) {
            Float rating = cursor.getFloat(0);
            String comment = cursor.getString(1);
            cursor.close();

            ArrayList<Object> result = new ArrayList<>();
            result.add(rating);
            result.add(comment);
            return result;
        }
        cursor.close();
        return null;
    }
}
