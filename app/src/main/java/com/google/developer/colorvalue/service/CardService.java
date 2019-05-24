package com.google.developer.colorvalue.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.developer.colorvalue.data.CardProvider;

/**
 * Handling asynchronous database task on separate thread.
 */
public class CardService extends IntentService {

    private static final String TAG = CardService.class.getSimpleName();

    private static final String ACTION_INSERT = TAG + ".INSERT";
    private static final String ACTION_DELETE = TAG + ".DELETE";

    public static final String EXTRA_VALUES = TAG + ".extra.CARD_VALUES";
    public static final String EXTRA_DELETE_URI = TAG + ".extra.DELETE_URI";

    public CardService() {
        super(TAG);
    }

    public static void insertCard(Context context, ContentValues values) {
        Intent intent = new Intent(context, CardService.class);
        intent.setAction(ACTION_INSERT);
        intent.putExtra(EXTRA_VALUES, values);
        context.startService(intent);
    }

    public static void deleteCard(Context context, Uri uri) {
        Intent intent = new Intent(context, CardService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_DELETE_URI, uri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_INSERT.equals(action)) {
                ContentValues values = intent.getParcelableExtra(EXTRA_VALUES);
                handleActionInsert(values);
            } else if (ACTION_DELETE.equals(action)) {
                Uri uri = intent.getParcelableExtra(EXTRA_DELETE_URI);
                handleActionDelete(uri);
            }
        }
    }

    private void handleActionInsert(ContentValues values) {
        if (getContentResolver().insert(CardProvider.Contract.CONTENT_URI, values) != null) {
            Log.d(TAG, "Inserted new card");
        } else {
            Log.w(TAG, "Error inserting new card");
        }
    }

    private void handleActionDelete(Uri uri) {
        if (getContentResolver().delete(uri, null, null) != 0) {
            Log.d(TAG, "Card deleted");
        } else {
            Log.w(TAG, "Error deleting card");
        }
    }
}
