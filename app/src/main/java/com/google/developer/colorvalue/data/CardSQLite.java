package com.google.developer.colorvalue.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.developer.colorvalue.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.provider.BaseColumns._ID;
import static com.google.developer.colorvalue.data.CardProvider.Contract.Columns.COLOR_HEX;
import static com.google.developer.colorvalue.data.CardProvider.Contract.Columns.COLOR_NAME;
import static com.google.developer.colorvalue.data.CardProvider.Contract.TABLE_NAME;

/**
 * Helper class to manage database
 */
public class CardSQLite extends SQLiteOpenHelper {

    private static final String TAG = CardSQLite.class.getName();
    private static final String DB_NAME = "colorvalue.db";
    private static final int DB_VERSION = 1;

    private Resources mResources;

    public CardSQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mResources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + TABLE_NAME + " (" +
                _ID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                COLOR_NAME + " TEXT NOT NULL,"+
                COLOR_HEX + " TEXT NOT NULL);";
        Log.e(TAG, "attemmpting to create");

        db.execSQL(CREATE_TABLE);
        Log.e(TAG, "Db created");

        //only isert the asset from colourValue.json if the db is being created for the first time
        addDemoCards(db);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Query for altering table here
        final String ALTER_TABLE = "";

        if(ALTER_TABLE.equals("")){
            db.execSQL(ALTER_TABLE);
        }
        else
        {
            //no alter query so drop the existing database
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            //create a new database
            onCreate(db);

        }

    }

    public static String getColumnString(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndex(name));
    }

    public static int getColumnInt(Cursor cursor, String name) {
        return cursor.getInt(cursor.getColumnIndex(name));
    }

    /**
     * save demo cards into database
     */
    private void addDemoCards(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                readCardsFromResources(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Unable to pre-fill database", e);
        }
    }

    /**
     * load demo color cards from {@link raw/colorcards.json}
     */
    private void readCardsFromResources(SQLiteDatabase db) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        InputStream in = mResources.openRawResource(R.raw.colorcards);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        //Parse resource into key/values
        JSONObject root = new JSONObject(builder.toString());


        JSONArray arr = root.getJSONArray("cards");


        for (int i = 0; i<arr.length(); i++){
//        for (int i = 0; i<jsonObject.length(); i++){
            JSONObject obj = arr.getJSONObject(i);
            ContentValues contentValues = new ContentValues();
            // Put the insect  into the ContentValues
            contentValues.put(COLOR_HEX, obj.getString("hex"));
            contentValues.put(COLOR_NAME, obj.getString("name"));

            long id =db.insert(TABLE_NAME,null,contentValues);

            Log.e(TAG, "including data: "+id);

        }
    }

}
