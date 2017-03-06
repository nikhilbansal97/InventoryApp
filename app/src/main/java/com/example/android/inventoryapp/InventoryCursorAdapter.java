package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventoryapp.InventoryContract.InventoryEntry;

/**
 * Created by NIKHIL on 05-03-2017.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {


        int idIndex = cursor.getColumnIndex(InventoryEntry._ID);
        int nameIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
        int priceIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
        final int quantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
        int imageIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE_URL);

        final long id = cursor.getLong(idIndex);
        String name_item = cursor.getString(nameIndex);
        int price_item = cursor.getInt(priceIndex);
        int quantity_item = cursor.getInt(quantityIndex);
        byte[] image = cursor.getBlob(imageIndex);
        Bitmap image_bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        TextView name_textView = (TextView) view.findViewById(R.id.name_item);
        TextView price_textView = (TextView) view.findViewById(R.id.price_item);
        final TextView quantity_textView = (TextView) view.findViewById(R.id.quantity_item);
        ImageView image_view = (ImageView) view.findViewById(R.id.inventory_image);

        name_textView.setText(name_item);
        price_textView.setText(Integer.toString(price_item));
        quantity_textView.setText(Integer.toString(quantity_item));
        //Picasso.with(context).load(image_url).resize(50, 50).centerCrop().into(image_view);
        image_view.setImageBitmap(image_bitmap);

        Button bt = (Button) view.findViewById(R.id.sale_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(quantity_textView.getText().toString());
                if (quantity > 0) {
                    quantity--;
                    quantity_textView.setText(Integer.toString(quantity));
                    ContentValues value = new ContentValues();
                    value.put(InventoryEntry.COLUMN_QUANTITY, quantity);
                    Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(InventoryEntry.BASE_ITEM_URI, InventoryEntry.ITEM_CONTENT), id);
                    context.getContentResolver().update(uri,
                            value,
                            null,
                            null);
                }
            }
        });
    }
}
