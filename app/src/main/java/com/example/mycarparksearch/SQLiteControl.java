package com.example.mycarparksearch;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

import java.util.ArrayList;

import static com.example.mycarparksearch.MapsActivity.CAR_PARK_NO;


public class SQLiteControl extends SQLiteOpenHelper {


    public SQLiteControl(Context context) {
        super(context, "Database", null, 1);
    }

    /*
    Create relevant tables if they are not there yet
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFavorite = "CREATE TABLE `Favorite` "
                + "(CarParkNo TEXT PRIMARY KEY, isFavorite INTEGER)";
        String createRating = "CREATE TABLE `Rating` "
                + "(CarParkNo TEXT PRIMARY KEY, Rating REAL, Comment TEXT)";
        String createSavedCarpark = "CREATE TABLE `SavedCarpark` "
                + "(CarParkNo TEXT PRIMARY KEY, Name TEXT, Time TEXT, Days TEXT, NotifyBy TEXT)";
        db.execSQL(createFavorite);
        db.execSQL(createRating);
        db.execSQL(createSavedCarpark);
    }

    /*
    Actions performed when there is an upgrade of database version
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String dropFavorite = "DROP TABLE IF EXISTS `Favorite`";
        String dropRating = "DROP TABLE IF EXISTS `Rating`";
        String dropSavedCarpark = "DROP TABLE IF EXISTS `SavedCarpark`";
        db.execSQL(dropFavorite);
        db.execSQL(dropRating);
        db.execSQL(dropSavedCarpark);
        onCreate(db);
    }

    /*
    Update the 'favorite' status of a car park
     */
    public void updateFavorite(String carParkNo, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        int isFavoriteInt = isFavorite ? 1 : 0;
        String updateFavoriteSQL = "INSERT OR REPLACE INTO `Favorite` (CarParkNo, isFavorite) "
                + " VALUES (\"" + carParkNo + "\", " + isFavoriteInt + ")";
        db.execSQL(updateFavoriteSQL);
    }

    /*
    Retrieve the 'favorite' status of a car park
     */
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
//    Remove favorite status of a car park
    public void deleteFavorite(String carParkNo){
        SQLiteDatabase db = this.getReadableDatabase();
        String deleteFavorite = "DELETE FROM Favorite WHERE CarParkNo =\"" +carParkNo +"\"" ;
        db.execSQL(deleteFavorite);

    }

    /*
    Update the rating and comment of a car park
     */
    public void updateRating(String carParkNo, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateFavoriteSQL = "INSERT OR REPLACE INTO `Rating` (CarParkNo, Rating, Comment) "
                + " VALUES (\"" + carParkNo + "\", " + rating + ", \"" + comment + "\")";
        db.execSQL(updateFavoriteSQL);
    }

    /*
    Retrieve the rating and comment of a car park
     */
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

    public void updateSavedCarpark(String carParkNo, String name, String time, String days, String notifyBy) {
        SQLiteDatabase db = this.getWritableDatabase();
        String updateSavedCarparkSQL = "INSERT OR REPLACE INTO `SavedCarpark` (CarParkNo, Name, Time, Days, NotifyBy) "
                + " VALUES (\"" + carParkNo + "\", \"" + name + "\", \"" + time + "\", \"" + days + "\", \"" + notifyBy + "\")";
        db.execSQL(updateSavedCarparkSQL);
    }

    public ArrayList<String> getSavedCarpark(String carParkNo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String getSavedCarparkSQL = "SELECT Name, Time, Days, NotifyBy FROM `SavedCarpark` WHERE CarParkNo = \"" + carParkNo + "\"";
        Cursor cursor = db.rawQuery(getSavedCarparkSQL, null);
        if (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String time = cursor.getString(1);
            String days = cursor.getString(2);
            String notifyBy = cursor.getString(3);
            cursor.close();

            ArrayList<String> result = new ArrayList<>();
            result.add(name);
            result.add(time);
            result.add(days);
            result.add(notifyBy);
            return result;
        }
        cursor.close();
        return null;
    }

    public Cursor viewData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query ="SELECT carParkNo FROM `Favorite` ";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor viewSavedCarpark(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query ="SELECT Name FROM 'SavedCarpark'";
        Cursor cursor =db.rawQuery(query,null);
        return cursor;
    }
}
