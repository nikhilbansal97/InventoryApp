package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.inventoryapp.InventoryContract.InventoryEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private ListView myList;
    private LoaderManager loader;
    private InventoryCursorAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton add_item = (FloatingActionButton) findViewById(R.id.add_item);
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent insert = new Intent(MainActivity.this , EditorActivity.class);
                startActivity(insert);
            }
        });

        myList = (ListView) findViewById(R.id.items_list);
        myAdapter = new InventoryCursorAdapter(this,null);
        myList.setAdapter(myAdapter);

        loader = getSupportLoaderManager();
        loader.initLoader(0 , null , this);

        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editor = new Intent(MainActivity.this , EditorActivity.class);
                Uri uri = ContentUris.withAppendedId(Uri.withAppendedPath(InventoryContract.InventoryEntry.BASE_ITEM_URI , InventoryContract.InventoryEntry.ITEM_CONTENT),id);
                editor.setData(uri);
                startActivity(editor);
            }
        });

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
        myAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myAdapter.swapCursor(null);
    }
}
