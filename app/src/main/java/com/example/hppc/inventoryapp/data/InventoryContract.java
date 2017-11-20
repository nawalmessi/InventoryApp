package com.example.hppc.inventoryapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by HP PC on 11/12/2017.
 */

public class InventoryContract {
    public final static Uri BASE_URI = Uri.parse("content://com.example.hppc.inventoryapp");

    public final static class InventoryEntry implements BaseColumns {

        public final static String TABLE_NAME = "products";
        public final static String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_Name = "name";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_Image = "image";
        public final static Uri PRODUCT_URI = Uri.withAppendedPath(BASE_URI, TABLE_NAME);
    }
}
