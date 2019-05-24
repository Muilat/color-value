package com.google.developer.colorvalue.data;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.developer.colorvalue.CardActivity;
import com.google.developer.colorvalue.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private Cursor mCursor;
    private final String TAG = CardAdapter.class.getSimpleName();



    @Override
    public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context mContext = parent.getContext();
        final  ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = viewHolder.getAdapterPosition();
                Card card = getItem(position);

                Intent cardIntent
                        = new Intent(mContext, CardActivity.class);
                cardIntent.putExtra(CardActivity.EXTRA_CARD, card);
                mContext.startActivity(cardIntent);
            }
        });

        return viewHolder;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.N)
    public void onBindViewHolder(CardAdapter.ViewHolder holder, int position) {

        mCursor.moveToPosition(position); // get to the right location in the cursor

        Card card = new Card(mCursor);
        holder.name.setText(card.mName+"");
        holder.name.setBackgroundColor(card.getColorInt());

        Log.e(TAG, card.mName+" is here");

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * Return a {@link Card} represented by this item in the adapter.
     * Method is used to run machine tests.
     *
     * @param position Cursor item position
     * @return A new {@link Card}
     */
    public Card getItem(int position) {
        if (mCursor.moveToPosition(position)) {
            return new Card(mCursor);
        }
        return null;
    }

    /**
     * @param data update cursor
     */
    public void swapCursor(Cursor data) {
        Log.e(TAG, "Swapping cursor");
        mCursor = data;
        notifyDataSetChanged();
    }

    /**
     * An Recycler item view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.color_name);
        }
    }
}
