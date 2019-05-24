package com.google.developer.colorvalue;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.developer.colorvalue.data.CardAdapter;
import com.google.developer.colorvalue.data.CardProvider;

import static com.google.developer.colorvalue.data.CardProvider.Contract.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements
         LoaderManager.LoaderCallbacks<Cursor>  {

    private static final int CARD_LOADER_ID = 1111;
    private static final String TAG = MainActivity.class.getName();
    private CardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        mCardAdapter = new CardAdapter();
        recycler.setAdapter(mCardAdapter);
        recycler.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addCardActivity = new Intent(MainActivity.this, AddCardActivity.class);


                startActivity(addCardActivity);
            }
        });



        //Ensure a loader is initialized and active
        getSupportLoaderManager().initLoader(CARD_LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the card data
            Cursor mCardsData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mCardsData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mCardsData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
//                CardProvider cardProvider = new CardProvider();

                try {
                    return getContentResolver().query(CONTENT_URI,
                            null,
                            null,
                            null,
                            null);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage()+" Failed to asynchronously load cards.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mCardsData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCardAdapter.swapCursor(data);
        Log.e(TAG, "No of cursor: "+data.getCount());


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all cards
        getSupportLoaderManager().restartLoader(CARD_LOADER_ID, null, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

//
    }

}
