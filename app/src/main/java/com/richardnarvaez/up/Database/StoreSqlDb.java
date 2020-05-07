package com.richardnarvaez.up.Database;

/**
 * Created by RICHARD on 10/03/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.richardnarvaez.up.Model.ItemCesta;
import com.richardnarvaez.up.Model.ItemFavorite;

import java.util.ArrayList;

public class StoreSqlDb {

    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "rs13nsw9vn7bs.db";

    private static final String CESTA_TABLE_NAME = "cesta";
    private static final String FAVORITES_TABLE_NAME = "favorites";
    private static final String TAG = "TABLE SQL";

    public enum ITEM_TYPE {FAVORITE, CESTA}

    private StoreNeftyDbHelper dbHelper;

    private Playlists playlists;

    private itemCesta cestaItems;
    private itemCesta favoriteVideos;

    private static StoreSqlDb ourInstance = new StoreSqlDb();

    public static StoreSqlDb getInstance() {
        return ourInstance;
    }

    private StoreSqlDb() {
    }

    public void init(Context context) {
        dbHelper = new StoreNeftyDbHelper(context);
        dbHelper.getWritableDatabase();
        playlists = new Playlists();
        cestaItems = new itemCesta(CESTA_TABLE_NAME);
        favoriteVideos = new itemCesta(FAVORITES_TABLE_NAME);
    }

    public itemCesta productos (ITEM_TYPE type) {
        if (type == ITEM_TYPE.FAVORITE) {
            return favoriteVideos;
        } else if (type == ITEM_TYPE.CESTA) {
            return cestaItems;
        }
        Log.e(TAG, "Error. Unknown video type!");
        return null;
    }

    public Playlists playlists() {
        return playlists;
    }

    private final class StoreNeftyDbHelper extends SQLiteOpenHelper {
        public StoreNeftyDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(itemCestaEntry.DATABASE_FAVORITES_TABLE_CREATE);
            db.execSQL(itemCestaEntry.DATABASE_CESTA_TABLE_CREATE);
            db.execSQL(itemPlayListEntry.DATABASE_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(itemCestaEntry.DROP_QUERY_CESTA);
            db.execSQL(itemCestaEntry.DROP_QUERY_FAVORITES);
            db.execSQL(itemPlayListEntry.DROP_QUERY);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public class itemCesta {

        private String tableName;

        private itemCesta(String tableName) {
            this.tableName = tableName;
        }

        public boolean create(ItemCesta item) {

            if(checkIfExists(item.getId())){
                return false;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(itemCestaEntry.COLUMN_ITEM_ID, item.getId());
            values.put(itemCestaEntry.COLUMN_TITLE, item.getTitle());
            values.put(itemCestaEntry.COLUMN_PRICE, item.getPrice());
            values.put(itemCestaEntry.COLUMN_THUMBNAIL_URL, item.getThumbnailURL());
            values.put(itemCestaEntry.COLUMN_DESCRIPTION, item.getViewCount());

            return db.insert(tableName, itemCestaEntry.COLUMN_NAME_NULLABLE, values) > 0;
        }

        public boolean checkIfExists(String productoId) {
            Log.d(TAG,"ID:" + productoId);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String Query = "SELECT * FROM " + tableName + " WHERE " + itemCestaEntry.COLUMN_ITEM_ID + "='" + productoId + "'";
            Log.d(TAG,"Query:" + Query);
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
            return true;
        }

        public long getItemCount() {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long cnt  = DatabaseUtils.queryNumEntries(db, tableName);
            db.close();
            return cnt;
        }

        public ArrayList<ItemCesta> readAll() {

            final String SELECT_QUERY_ORDER_DESC = "SELECT * FROM " + tableName + " ORDER BY "
                    + itemCestaEntry.COLUMN_ENTRY_ID + " DESC";

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            ArrayList<ItemCesta> list = new ArrayList<>();

            Cursor c = db.rawQuery(SELECT_QUERY_ORDER_DESC, null);
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(itemCestaEntry.COLUMN_ITEM_ID));
                String title = c.getString(c.getColumnIndexOrThrow(itemCestaEntry.COLUMN_TITLE));
                Float price = c.getFloat(c.getColumnIndexOrThrow(itemCestaEntry.COLUMN_PRICE));
                String thumbnailUrl = c.getString(c.getColumnIndexOrThrow(itemCestaEntry.COLUMN_THUMBNAIL_URL));
                String description = c.getString(c.getColumnIndexOrThrow(itemCestaEntry.COLUMN_DESCRIPTION));
                list.add(new ItemCesta(itemId, title, thumbnailUrl, price, description));
            }
            c.close();
            return list;
        }

        public boolean delete(String itemId) {
            return dbHelper.getWritableDatabase().delete(tableName,
                    itemCestaEntry.COLUMN_ITEM_ID + "='" + itemId + "'", null) > 0;
        }

        public boolean deleteAll() {
            return dbHelper.getWritableDatabase().delete(tableName, "1", null) > 0;
        }
    }

    public static abstract class itemCestaEntry implements BaseColumns {
        public static final String COLUMN_ENTRY_ID = "_id";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_DESCRIPTION = "description";

        public static final String COLUMN_NAME_NULLABLE = "null";

        private static final String DATABASE_CESTA_TABLE_CREATE =
                "CREATE TABLE " + CESTA_TABLE_NAME + "(" +
                        COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_ITEM_ID + " TEXT NOT NULL UNIQUE," +
                        COLUMN_TITLE + " TEXT NOT NULL,"+
                        COLUMN_PRICE + " REAL NOT NULL,"+
                        COLUMN_THUMBNAIL_URL + " TEXT," +
                        COLUMN_DESCRIPTION + " TEXT)";

        private static final String DATABASE_FAVORITES_TABLE_CREATE =
                "CREATE TABLE " + FAVORITES_TABLE_NAME + "(" +
                        COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_ITEM_ID + " TEXT NOT NULL UNIQUE," +
                        COLUMN_TITLE + " TEXT NOT NULL,"+
                        COLUMN_PRICE + " REAL NOT NULL,"+
                        COLUMN_THUMBNAIL_URL + " TEXT," +
                        COLUMN_DESCRIPTION + " TEXT)";

        public static final String DROP_QUERY_CESTA = "DROP TABLE " + CESTA_TABLE_NAME;
        public static final String DROP_QUERY_FAVORITES = "DROP TABLE " + FAVORITES_TABLE_NAME;
    }

    public class Playlists {

        private Playlists() {
        }

        public boolean create(ItemFavorite favorite) {
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(itemCestaEntry.COLUMN_ITEM_ID, favorite.getId());
            values.put(itemCestaEntry.COLUMN_TITLE, favorite.getTitle());
            values.put(itemCestaEntry.COLUMN_PRICE, favorite.getPrice());
            values.put(itemCestaEntry.COLUMN_THUMBNAIL_URL, favorite.getThumbnailURL());
            values.put(itemCestaEntry.COLUMN_DESCRIPTION, favorite.getDescription());

            // Insert the new row, returning the primary key value of the new row. If -1, operation has failed
            return db.insert(itemPlayListEntry.TABLE_NAME, itemPlayListEntry.COLUMN_NAME_NULLABLE, values) > 0;
        }

        public ArrayList<ItemFavorite> readAll() {

            ArrayList<ItemFavorite> list = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor c = db.rawQuery(itemPlayListEntry.SELECT_QUERY_ORDER_DESC, null);
            while (c.moveToNext()) {
                String playlistId = c.getString(c.getColumnIndexOrThrow(itemPlayListEntry.COLUMN_PLAYLIST_ID));
                String title = c.getString(c.getColumnIndexOrThrow(itemPlayListEntry.COLUMN_TITLE));
                long number = c.getLong(c.getColumnIndexOrThrow(itemPlayListEntry.COLUMN_VIDEOS_NUMBER));
                String status = c.getString(c.getColumnIndexOrThrow(itemPlayListEntry.COLUMN_STATUS));
                String thumbnailUrl = c.getString(c.getColumnIndexOrThrow(itemPlayListEntry.COLUMN_THUMBNAIL_URL));
                list.add(new ItemFavorite(title, thumbnailUrl, playlistId, number, status));
            }
            c.close();
            return list;
        }

        public boolean delete(String playlistId) {
            return dbHelper.getWritableDatabase().delete(itemPlayListEntry.TABLE_NAME,
                    itemPlayListEntry.COLUMN_PLAYLIST_ID + "='" + playlistId + "'", null) > 0;
        }

        public boolean deleteAll() {
            return dbHelper.getWritableDatabase().delete(itemPlayListEntry.TABLE_NAME, "1", null) > 0;
        }
    }

    public static abstract class itemPlayListEntry implements BaseColumns {
        public static final String TABLE_NAME = "playlists";
        public static final String COLUMN_ENTRY_ID = "_id";
        public static final String COLUMN_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VIDEOS_NUMBER = "videos_number";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";

        public static final String COLUMN_NAME_NULLABLE = "null";

        private static final String DATABASE_TABLE_CREATE =
                "CREATE TABLE " + itemPlayListEntry.TABLE_NAME + "(" +
                        COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_PLAYLIST_ID + " TEXT NOT NULL UNIQUE," +
                        COLUMN_TITLE + " TEXT NOT NULL," +
                        COLUMN_VIDEOS_NUMBER + " INTEGER," +
                        COLUMN_THUMBNAIL_URL + " TEXT," +
                        COLUMN_STATUS + " TEXT);";

        public static final String DROP_QUERY = "DROP TABLE " + TABLE_NAME;

        public static final String SELECT_QUERY_ORDER_DESC = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ENTRY_ID + " DESC";
    }
}
