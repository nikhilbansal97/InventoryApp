package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by NIKHIL on 05-03-2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.name_item);
        name.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME)));

        TextView price = (TextView) view.findViewById(R.id.price_item);
        price.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE)));

        TextView quantity = (TextView) view.findViewById(R.id.quantity_item);
        quantity.setText(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY)));
    }
}
