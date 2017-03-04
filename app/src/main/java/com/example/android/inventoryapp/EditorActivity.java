package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.example.android.inventoryapp.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mName;
    private EditText mPrice;
    private EditText mSupplier_name;
    private EditText mSupplier_phone;
    private EditText mSupplier_email;
    private LoaderManager mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        Intent intent = getIntent();
        Uri received_uri = intent.getData();

        mName = (EditText) findViewById(R.id.name_edit_view);
        mPrice = (EditText) findViewById(R.id.price_int);
        mSupplier_name = (EditText) findViewById(R.id.supplier_name_edit);
        mSupplier_phone = (EditText) findViewById(R.id.supplier_phone_edit);
        mSupplier_email = (EditText) findViewById(R.id.supplier_email_edit);

        if(received_uri == null)
        {
            setTitle("Add Item");
            invalidateOptionsMenu();
        }
        else
        {
            setTitle("Edit Item");
            mLoader = getSupportLoaderManager();
            mLoader.initLoader(0,null,this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = new String[] {
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL
        };

        return new CursorLoader(getApplicationContext(),
                Uri.withAppendedPath(InventoryEntry.BASE_ITEM_URI,InventoryEntry.ITEM_CONTENT),
                projections,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst())
        {
            int nameIndex = data.getColumnIndex(InventoryEntry.COLUMN_NAME);
            int priceIndex = data.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityIndex = data.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierNameIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            int supplierEmailIndex = data.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            String name = data.getString(nameIndex);
            String price = data.getString(priceIndex);
            String supplierName = data.getString(supplierNameIndex);
            String supplierPhone = data.getString(supplierPhoneIndex);
            String supplierEmail = data.getString(supplierEmailIndex);
            mName.setText(name);
            mPrice.setText(price);
            mSupplier_name.setText(supplierName);
            mSupplier_phone.setText(supplierPhone);
            mSupplier_email.setText(supplierEmail);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mPrice.setText("");
        mSupplier_name.setText("");
        mSupplier_phone.setText("");
        mSupplier_email.setText("");
    }
}
