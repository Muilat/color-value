package com.google.developer.colorvalue;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.service.CardService;
import com.google.developer.colorvalue.ui.ColorView;

import static com.google.developer.colorvalue.data.CardProvider.Contract.CONTENT_URI;

public class CardActivity extends AppCompatActivity {

    public static final String EXTRA_CARD = "card_extra";
    static Card card;

    private ColorView colorView;
    private TextView colorName, colorHex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        colorView = (ColorView)findViewById(R.id.colorView);
        colorName = (TextView)findViewById(R.id.card_name);
        colorHex = (TextView)findViewById(R.id.card_hex);

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(EXTRA_CARD)){
            card = getIntent().getExtras().getParcelable(EXTRA_CARD);
            setView(card);
        }else {
            if(savedInstanceState != null){
                card = savedInstanceState.getParcelable(EXTRA_CARD);
                setView(card);

            }
        }
    }

    private void setView(Card card) {
        colorName.setText(card.mName);
        colorHex.setText(card.mColor);
        colorView.setColor(card.getColorInt());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            // Build appropriate uri with String row id appended
            String stringId = Integer.toString(card.getID());
            Uri uri = CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();
            CardService.deleteCard(CardActivity.this, uri);

            Toast.makeText(this, card.mName+" card deleted", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
