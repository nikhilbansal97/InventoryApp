package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.inventoryapp.InventoryContract.InventoryEntry;


public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView myList;
    private LoaderManager loader;
    private Button sale_button;
    private InventoryCursorAdapter myAdapter;
    private InventoryDbHelper mHelper;
    private String LOG_TAG = InventoryActivity.class.getSimpleName();
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v(LOG_TAG, "onCreate InventoryActivity");
        FloatingActionButton add_item = (FloatingActionButton) findViewById(R.id.add_item_activity);
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insert = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(insert);
            }
        });

        empty = (TextView) findViewById(R.id.empty_view);
        myList = (ListView) findViewById(R.id.items_list);
        myAdapter = new InventoryCursorAdapter(this, null);
        myList.setAdapter(myAdapter);
        sale_button = (Button) findViewById(R.id.sale_button);
        mHelper = new InventoryDbHelper(this);
        myList.setEmptyView(empty);

        loader = getSupportLoaderManager();
        loader.initLoader(0, null, this);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.v(LOG_TAG, "Item clicked");
                Button bt = (Button) view.findViewById(R.id.sale_button);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //SQLiteDatabase db = mHelper.getWritableDatabase();
                        Log.v(LOG_TAG, "Send");
                    }
                });
                Intent editor = new Intent(InventoryActivity.this, EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(InventoryContract.InventoryEntry.BASE_ITEM_URI, InventoryEntry.ITEM_CONTENT), id);
                editor.setData(uri);
                startActivity(editor);
            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        empty.setVisibility(View.GONE);

        String[] projections = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_IMAGE_URL
        };

        Log.v(LOG_TAG, "onCreateLoader: Inventory Activity");

        return new CursorLoader(getApplicationContext(),
                Uri.withAppendedPath(InventoryEntry.BASE_ITEM_URI, InventoryEntry.ITEM_CONTENT),
                projections,
                null,
                null,
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    private void insertItem() {
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, "Item1");
        values.put(InventoryEntry.COLUMN_PRICE, 25);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, "Supplier1");
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, 9999999);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, "abc@xyz.com");
        values.put(InventoryEntry.COLUMN_IMAGE_URL, "https://umexpert.um.edu.my/Avatar/no-image-found.jpg");

        Uri uri = getContentResolver().insert(Uri.withAppendedPath(InventoryEntry.BASE_ITEM_URI, InventoryEntry.ITEM_CONTENT), values);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                insertItem();
                return true;
            case R.id.delete_all:
                deleteAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wanna delete all items?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int rows = getContentResolver().delete(Uri.withAppendedPath(InventoryEntry.BASE_ITEM_URI, InventoryEntry.ITEM_CONTENT),
                        null,
                        null);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "onLoadFinished : Inventory Activity");
        myAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myAdapter.swapCursor(null);
    }
}
