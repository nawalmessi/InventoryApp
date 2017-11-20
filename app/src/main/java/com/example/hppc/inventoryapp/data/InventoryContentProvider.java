package com.example.hppc.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by HP PC on 11/12/2017.
 */

public class InventoryContentProvider extends ContentProvider {
    InventoryDBHelper dbHelper;
    final static UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        mUriMatcher.addURI("com.example.hppc.inventoryapp", "products", 1);
        mUriMatcher.addURI("com.example.hppc.inventoryapp", "products/#", 2);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code = mUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (code) {
            case 1:
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case 2:
                selection = InventoryContract.InventoryEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("cannot resolve the Uri" + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int code = mUriMatcher.match(uri);
        String productName = values.getAsString(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Name);
        if (productName.isEmpty()) {

            throw new IllegalArgumentException("product requires a name");
        }
        Integer productQuantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY);
        if (productQuantity == null || (productQuantity != null && productQuantity < 0)) {
            throw new IllegalArgumentException("product requires a valid quantity");
        }
        Integer productPrice = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE);
        if (productPrice == null || (productPrice != null && productPrice < 0)) {
            throw new IllegalArgumentException("product requires a valid price");
        }
        byte[] productImage = values.getAsByteArray(InventoryContract.InventoryEntry.COLUMN_PRODUCT_Image);
        if (productImage == null) {
            throw new IllegalArgumentException("please take a photo for the product");
        }
        if (code == 1) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int affectedRows = 0;
        switch (code) {
            case 1:
                affectedRows = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case 2:
                selection = InventoryContract.InventoryEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                affectedRows = db.delete(InventoryContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("cannot resolve the Uri" + uri);
        }
        if (affectedRows > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return affectedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int code = mUriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        switch (code) {

            case 1:
                affectedRows = db.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case 2:
                selection = InventoryContract.InventoryEntry.COLUMN_PRODUCT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                affectedRows = db.update(InventoryContract.InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("cannot resolve the Uri" + uri);
        }
        if (affectedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return affectedRows;
    }

}

