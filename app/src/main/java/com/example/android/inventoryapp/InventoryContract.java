package com.example.android.inventoryapp;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by NIKHIL on 04-03-2017.
 */

public final class InventoryContract {

    private InventoryContract(){};

    public static final class InventoryEntry implements BaseColumns{

        public static final String TABLE_NAME = "items";
        public static final String AUTHORITY = "com.example.android.inventoryapp";
        public static final String ITEM_CONTENT = "items";
        public static final String ITEM_ID_CONTENT= "items/#";
        public static final Uri BASE_ITEM_URI = Uri.parse("content://" + AUTHORITY );
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY;


        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SUPPLIER_NAME = "SupplierName";
        public static final String COLUMN_SUPPLIER_PHONE = "SupplierPhone";
        public static final String COLUMN_SUPPLIER_EMAIL = "SupplierEmail";

    }



}
