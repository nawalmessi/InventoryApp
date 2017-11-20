package com.example.hppc.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.hppc.inventoryapp.data.InventoryContract;
import com.example.hppc.inventoryapp.databinding.ActivityDetailsBinding;

import java.io.ByteArrayOutputStream;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    final int REQUEST_IMAGE_CAPTURE = 1;
    ActivityDetailsBinding binding;
    Boolean editMode = false;
    Uri producRowUri;
    boolean unsavedData = false;
    int qty = 0;
    byte[] imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

        final long id = getIntent().getLongExtra("ID", -1);
        if (id != -1) {
            editMode = true;
            producRowUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.PRODUCT_URI, id);
            getSupportLoaderManager().initLoader(2, null, this);
            binding.deletButton.setVisibility(View.VISIBLE);

        }
        binding.deletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                builder.setMessage("Do you want to delete this product ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getContentResolver().delete(producRowUri, null, null);
                        finish();

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        binding.orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent orderIntent = new Intent(Intent.ACTION_SENDTO);
                orderIntent.setData(Uri.parse("mailto:supplier@example.com"));
                startActivity(orderIntent);
            }
        });
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                unsavedData = true;
                return false;
            }
        };
        binding.nameEditText.setOnTouchListener(listener);
        binding.quantityEditText.setOnTouchListener(listener);
        binding.priceEditText.setOnTouchListener(listener);
        binding.productImageView.setOnTouchListener(listener);

        binding.productImageView.setImageResource(R.drawable.ic_photo_camera_black_24dp);
        binding.productImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        });

        qty = Integer.parseInt(binding.quantityEditText.getText().toString().trim());

        binding.qtyPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qty++;
                binding.quantityEditText.setText(qty + "");
            }
        });
        binding.qtyMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qty > 0) {
                    qty--;
                    binding.quantityEditText.setText(qty + "");
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            binding.productImageView.setImageBitmap(imageBitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imageData = stream.toByteArray();
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, producRowUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Name));
            binding.nameEditText.setText(name);
            int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY));
            binding.quantityEditText.setText(String.valueOf(quantity));
            int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE));
            binding.priceEditText.setText(String.valueOf(price));
            byte[] image = cursor.getBlob(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Image));

            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

            binding.productImageView.setImageBitmap(bitmap);

        }
        cursor.close();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        binding.productImageView.setImageResource(R.drawable.ic_photo_camera_black_24dp);
        binding.priceEditText.setText("");
        binding.quantityEditText.setText("");
        binding.priceEditText.setText("");

    }

    @Override
    public void onBackPressed() {
        if (unsavedData) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
            builder.setMessage("You didn't save the producr Do you want yo leave?");
            builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DetailsActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu_item:
                ContentValues values = new ContentValues();
                values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Name, binding.nameEditText.getText().toString());
                values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE, binding.priceEditText.getText().toString());
                values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, binding.quantityEditText.getText().toString());
                if (imageData != null) {
                    values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Image, imageData);
                }
                if (editMode) {
                    try {
                        getContentResolver().update(producRowUri, values, null, null);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        getContentResolver().insert(InventoryContract.InventoryEntry.PRODUCT_URI, values);
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;


        }
        return super.onOptionsItemSelected(item);
    }
}
