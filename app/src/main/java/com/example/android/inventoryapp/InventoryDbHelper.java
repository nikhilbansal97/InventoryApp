package com.example.android.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.InventoryContract.InventoryEntry;

/**
 * Created by NIKHIL on 05-03-2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "item_shelter.db";

    public static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_QUERY = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryEntry.COLUMN_NAME + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 2," +
                InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL," +
                InventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_SUPPLIER_PHONE + " INTEGER NOT NULL," +
                InventoryEntry.COLUMN_IMAGE_URL + " BLOB," +
                InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL);";

        db.execSQL(CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
