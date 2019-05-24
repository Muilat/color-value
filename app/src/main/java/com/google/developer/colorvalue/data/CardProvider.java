package com.google.developer.colorvalue.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.google.developer.colorvalue.data.CardProvider.Contract.CONTENT_URI;
import static com.google.developer.colorvalue.data.CardProvider.Contract.TABLE_NAME;

public class CardProvider extends ContentProvider {

    /** Matcher identifier for all cards */
    private static final int CARD = 100;
    /** Matcher identifier for one card */
    private static final int CARD_WITH_ID = 102;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.google.developer.colorvalue/cards
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME, CARD);
        // content://com.google.developer.colorvalue/cards/#
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME + "/#", CARD_WITH_ID);
    }

    private CardSQLite mCardSQLite;

    @Override
    public boolean onCreate() {
        mCardSQLite = new CardSQLite(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
            @Nullable String selection, @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mCardSQLite.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case CARD:
                retCursor =  db.query(TABLE_NAME, projection, selection,
                        selectionArgs,null,null,sortOrder);
                break;
            case CARD_WITH_ID:
                // Get the card ID from the URI path
                String id = uri.getPathSegments().get(1);

                retCursor =  db.query(TABLE_NAME, projection,"_id=?",
                        new String[]{id},null, null, sortOrder);
                break;
            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case CARD:
                // Insert new values into the database
                // Inserting values into cards table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted cards
        int cardsDeleted; // starts as 0

        // Write the code to delete a single row of data
        // [Hint] Use selections to delete an item by its row ID
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case CARD_WITH_ID:
                // Get the card ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                cardsDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (cardsDeleted != 0) {
            // A card was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of cards deleted
        return cardsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
            @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of updated card
        int cardsUpdated; // starts as 0

        // [Hint] Use selections to update an item by its row ID
        switch (match){
            // Handle the single item case, recognized by the ID included in the URI path
            case CARD_WITH_ID:
                // Get the card ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID

                cardsUpdated = db.update(TABLE_NAME,values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (cardsUpdated != 0) {
            // A card was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of cards deleted
        return cardsUpdated;
    }

    /**
     * Database contract
     */
    public static class Contract {
        public static final String TABLE_NAME = "cards";
        public static final String CONTENT_AUTHORITY = "com.google.developer.colorvalue";
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        public static final String PATH_CARDS = "cards";
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARDS).build();

//        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
//                .authority(CONTENT_AUTHORITY)
//                .appendPath(TABLE_NAME)
//                .build();

        public static final class Columns implements BaseColumns {
            public static final String COLOR_HEX = "question";
            public static final String COLOR_NAME = "answer";
        }
    }

}
