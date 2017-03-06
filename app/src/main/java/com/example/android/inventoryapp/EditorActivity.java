package com.example.android.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.InventoryContract.InventoryEntry;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mName;
    private EditText mPrice;
    private EditText mSupplier_name;
    private EditText mSupplier_phone;
    private EditText mSupplier_email;
    private LoaderManager mLoader;
    private Button add_item;
    Uri received_uri;
    private TextView quantity;
    private Button image_url;
    private Button add_button;
    private boolean image_captured = false;
    private Button sub_button;
    private String LOG_TAG;
    private int quantity_int;
    private Button order;
    private byte[] image_byte;
    private InventoryDbHelper mHelper;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        LOG_TAG = EditorActivity.class.getSimpleName();
        Log.v(LOG_TAG, "onCreate Editor Activity");
        Intent intent = getIntent();
        received_uri = intent.getData();

        mHelper = new InventoryDbHelper(this);
        image_url = (Button) findViewById(R.id.image_url_input);
        mSupplier_name = (EditText) findViewById(R.id.supplier_name_edit);
        mSupplier_phone = (EditText) findViewById(R.id.supplier_phone_edit);
        mSupplier_email = (EditText) findViewById(R.id.supplier_email_edit);
        mName = (EditText) findViewById(R.id.name_edit_view);
        mPrice = (EditText) findViewById(R.id.price_int);
        quantity = (TextView) findViewById(R.id.quantity_int);
        add_item = (Button) findViewById(R.id.add_item_editor);
        add_button = (Button) findViewById(R.id.add_button);
        sub_button = (Button) findViewById(R.id.sub_button);
        order = (Button) findViewById(R.id.order_more);


        if (received_uri == null) {
            setTitle("Add Item");
            invalidateOptionsMenu();
            order.setVisibility(View.GONE);
            add_item.setText("ADD ITEM");
        } else {
            setTitle("Edit Item");
            order.setVisibility(View.VISIBLE);
            add_item.setText("UPDATE ITEM");
            mLoader = getSupportLoaderManager();
            mLoader.initLoader(0, null, this);
        }

        image_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent take_picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (take_picture.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(take_picture, REQUEST_IMAGE_CAPTURE);
                        Log.v(LOG_TAG, "Image captured successfully.");
                        image_captured = true;
                    }
                } else {
                    ActivityCompat.requestPermissions(EditorActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            123);
                }
            }
        });


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity_int = Integer.parseInt(quantity.getText().toString());
                quantity_int++;
                quantity.setText(Integer.toString(quantity_int));
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projections = {
                        InventoryEntry._ID,
                        InventoryEntry.COLUMN_SUPPLIER_EMAIL
                };

                Cursor cursor = getContentResolver().query(received_uri,
                        projections,
                        null,
                        null,
                        null);
                Log.v(LOG_TAG, "order more");
                if (cursor.moveToFirst()) {

                    int emailIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
                    String email = cursor.getString(emailIndex);
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + email.trim()));
                    startActivity(intent);

                }

            }
        });

        sub_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity_int = Integer.parseInt(quantity.getText().toString());
                if (quantity_int > 0) {
                    quantity_int--;
                    quantity.setText(Integer.toString(quantity_int));
                }

            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(mName.getText().toString())) {
                    mName.setError("Required");
                }
                if (TextUtils.isEmpty(mPrice.getText().toString())) {
                    mPrice.setError("Required");
                }
                if (TextUtils.isEmpty(mSupplier_name.getText().toString())) {
                    mSupplier_name.setError("Required");
                }
                if (TextUtils.isEmpty(mSupplier_phone.getText().toString())) {
                    mSupplier_phone.setError("Required");
                }
                if (TextUtils.isEmpty(mSupplier_email.getText().toString())) {
                    mSupplier_email.setError("Required");
                }
                if (!TextUtils.isEmpty(mName.getText().toString()) && !TextUtils.isEmpty(mPrice.getText().toString()) &&
                        !TextUtils.isEmpty(mSupplier_name.getText().toString()) &&
                        !TextUtils.isEmpty(mSupplier_phone.getText().toString()) &&
                        !TextUtils.isEmpty(mSupplier_email.getText().toString()) && image_captured) {
                    saveItem();
                    Intent main = new Intent(EditorActivity.this, InventoryActivity.class);
                    startActivity(main);
                } else {
                    Toast.makeText(EditorActivity.this, "Add an image!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult Started successfully.");

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.v(LOG_TAG, "Bitmap created successfully.");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (imageBitmap != null) {
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                Log.v(LOG_TAG, "Image compressed successfully.");
                image_byte = stream.toByteArray();
                Log.v(LOG_TAG, "byte[] created successfully");
            }
        }
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {

        if (received_uri == null) {
            MenuItem item = menu.findItem(R.id.delete_button);
            item.setVisible(false);
        }
        return true;
    }

    public void saveItem() {
        String name = mName.getText().toString().trim();
        int price = 0;
        if (!TextUtils.isEmpty(mPrice.getText().toString().trim()))
            price = Integer.parseInt(mPrice.getText().toString().trim());
        String supplier_name = mSupplier_name.getText().toString().trim();
        long supplier_phone = 0;
        if (!TextUtils.isEmpty(mSupplier_phone.getText().toString().trim()))
            supplier_phone = Long.parseLong(mSupplier_phone.getText().toString().trim());
        String supplier_email = mSupplier_email.getText().toString().trim();
        int quantity_count = Integer.parseInt(quantity.getText().toString());
        Log.v(LOG_TAG, name + price + supplier_name + supplier_phone + supplier_email);
        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(supplier_name) && TextUtils.isEmpty(supplier_email)) {
            return;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, name);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity_count);
        values.put(InventoryEntry.COLUMN_SUPPLIER_NAME, supplier_name);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplier_phone);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL, supplier_email);
        values.put(InventoryEntry.COLUMN_IMAGE_URL, image_byte);

        if (received_uri == null) {
            Log.v(LOG_TAG, "null uri");
            Uri uri = getContentResolver().insert(Uri.withAppendedPath(InventoryEntry.BASE_ITEM_URI, InventoryEntry.ITEM_CONTENT), values);
            Log.v(LOG_TAG, "Values add successfully.");
            Log.v(LOG_TAG, "uri made");
            if (uri != null)
                Toast.makeText(this, "New Item Added", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Error Adding Item", Toast.LENGTH_SHORT).show();
        } else {
            int rows = getContentResolver().update(received_uri,
                    values,
                    null,
                    null);
            if (rows == 0)
                Toast.makeText(this, "No row updated", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_button:
                showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Do you wanna delete item ?");
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        if (received_uri != null) {
            int rows = getContentResolver().delete(received_uri,
                    null,
                    null);

            if (rows > 0)
                Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Delete Unsuccessful", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projections = new String[]{
                InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_IMAGE_URL
        };

        Log.v(LOG_TAG, "onCreateLoader: Editor Activity");

        return new CursorLoader(getApplicationContext(),
                received_uri,
                projections,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            Log.v(LOG_TAG, "onLoadFinished: Editor Activity");
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
            String quantity_count = data.getString(quantityIndex);
            mName.setText(name);
            mPrice.setText(price);
            mSupplier_name.setText(supplierName);
            mSupplier_phone.setText(supplierPhone);
            mSupplier_email.setText(supplierEmail);
            quantity.setText(quantity_count);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText(" ");
        mPrice.setText(" ");
        mSupplier_name.setText(" ");
        mSupplier_phone.setText(" ");
        mSupplier_email.setText(" ");
    }
}
