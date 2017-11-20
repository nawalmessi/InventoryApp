package com.example.hppc.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by HP PC on 11/12/2017.
 */

public class InventoryDBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "inventory.db";
    private final static int DATABASE_Version = 1;

    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Create_table = String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY ,%s TEXT,%s INTEGER,%s INTEGER,%s BLOB);",
                InventoryContract.InventoryEntry.TABLE_NAME,
                InventoryContract.InventoryEntry._ID,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_Name,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryContract.InventoryEntry.COLUMN_PRODUCT_Image);
        db.execSQL(Create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
