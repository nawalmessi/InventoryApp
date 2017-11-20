package com.example.hppc.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hppc.inventoryapp.data.InventoryContract;

/**
 * Created by HP PC on 11/12/2017.
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
    public void bindView(View view, final Context context, Cursor cursor) {

        ImageView productImage = (ImageView) view.findViewById(R.id.product_image_view);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity_text_view);
        TextView priceTextView = (TextView) view.findViewById(R.id.price_text_view);

        final String name = cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Name));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY));
        final int price = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE));
        final byte[] image = cursor.getBlob(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Image));
        final int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRODUCT_ID));

        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        productImage.setImageBitmap(bitmap);
        nameTextView.setText(name);
        quantityTextView.setText("Quantity : " + quantity);
        priceTextView.setText("Price : " + price);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                if (quantity > 0) {
                    values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);
                    context.getContentResolver().update(ContentUris.withAppendedId(InventoryContract.InventoryEntry.PRODUCT_URI, id), values, null, null);
                } else {
                    Toast.makeText(context, "This product is sold out", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
