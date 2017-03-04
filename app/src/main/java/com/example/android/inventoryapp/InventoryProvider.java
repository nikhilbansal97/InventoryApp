package com.example.android.inventoryapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.android.inventoryapp.InventoryContract.InventoryEntry;


/**
 * Created by NIKHIL on 05-03-2017.
 */

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    public static final int ITEM_CODE = 1000;
    public static final int ITEM_ID_CODE = 1001;

    public static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private InventoryDbHelper mDbHelper;

    static{
        matcher.addURI(InventoryEntry.AUTHORITY , InventoryEntry.ITEM_CONTENT , ITEM_CODE);
        matcher.addURI(InventoryEntry.AUTHORITY , InventoryEntry.ITEM_ID_CONTENT , ITEM_ID_CODE);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase mDatabase = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int result = matcher.match(uri);
        switch (result)
        {
            case ITEM_CODE:
                cursor = mDatabase.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case ITEM_ID_CODE:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = mDatabase.query(InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query for uri " + uri + " result is " + result);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int result = matcher.match(uri);
        switch (result)
        {
            case ITEM_CODE:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case ITEM_ID_CODE:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Invalid uri of result" + result);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        String name = values.getAsString(InventoryEntry.COLUMN_NAME);
        String price = values.getAsString(InventoryEntry.COLUMN_PRICE);
        String phone = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        String email = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
        String supplier_name = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME);

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(supplier_name) || TextUtils.isEmpty(price) || TextUtils.isEmpty(phone))
        {
            throw new IllegalArgumentException("Incomplete data");
        }
        int result = matcher.match(uri);
        switch(result)
        {
            case ITEM_CODE:
                return insertItem(uri,values);
            default:
                throw new IllegalArgumentException("Insert failed");
        }
    }

    private Uri insertItem(Uri uri , ContentValues values)
    {
        SQLiteDatabase mDatabase = mDbHelper.getWritableDatabase();
        long id = mDatabase.insert(InventoryEntry.TABLE_NAME,null,values);
        if(id == -1)
            throw new IllegalArgumentException("Failed to insert data");
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase mDatabase = mDbHelper.getWritableDatabase();

        int result = matcher.match(uri);
        switch (result)
        {
            case ITEM_CODE:
                return mDatabase.delete(InventoryEntry.TABLE_NAME , selection , selectionArgs);
            case ITEM_ID_CODE:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return  mDatabase.delete(InventoryEntry.TABLE_NAME, selection , selectionArgs);
            default :
                throw new IllegalArgumentException("Failed to delete record");
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int result = matcher.match(uri);
        switch (result)
        {
            case ITEM_CODE:
                return updateItem(uri , values , selection , selectionArgs);
            case ITEM_ID_CODE:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri , values , selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update failed");
        }

    }
    private int updateItem(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs)
    {
        if(values.containsKey(InventoryEntry.COLUMN_NAME))
        {
            if((values.getAsString(InventoryEntry.COLUMN_NAME)).isEmpty())
                throw new IllegalArgumentException("Item Name Required");
        }

        if(values.containsKey(InventoryEntry.COLUMN_SUPPLIER_NAME))
        {
            if((values.getAsString(InventoryEntry.COLUMN_SUPPLIER_NAME)).isEmpty())
                throw new IllegalArgumentException("Supplier Name Required");
        }

        if(values.containsKey(InventoryEntry.COLUMN_PRICE))
        {
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
            if(price == null || price <= 0)
                throw new IllegalArgumentException("Invalid price");
        }

        if(values.containsKey(InventoryEntry.COLUMN_SUPPLIER_EMAIL))
        {
            if((values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL)).isEmpty())
                throw new IllegalArgumentException("Supplier Email Required");
        }

        if(values.containsKey(InventoryEntry.COLUMN_SUPPLIER_PHONE))
        {
            Integer phone = values.getAsInteger(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            if(phone == null || phone <= 0)
                throw new IllegalArgumentException("Invalid Phone number");
        }

        SQLiteDatabase mDatabase = mDbHelper.getWritableDatabase();

        int rows = mDatabase.update(InventoryEntry.TABLE_NAME,values,selection,selectionArgs);

        return rows;
    }
}
