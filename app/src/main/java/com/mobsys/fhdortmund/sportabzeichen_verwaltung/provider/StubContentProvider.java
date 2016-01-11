package com.mobsys.fhdortmund.sportabzeichen_verwaltung.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


/**
 * Created by bleile on 18.12.2015.
 * This is only a stub content provider which is needed for the Sync Adapter
 */
public class StubContentProvider extends ContentProvider {

    @Override
    public boolean onCreate(){
        return true;
    }

    @Override
    public String getType(Uri uri){
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
