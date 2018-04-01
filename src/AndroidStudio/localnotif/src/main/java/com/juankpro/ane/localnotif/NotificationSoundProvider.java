package com.juankpro.ane.localnotif;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;

import com.juankpro.ane.localnotif.util.AssetDecompressor;

import java.io.FileNotFoundException;

public class NotificationSoundProvider extends ContentProvider {
    public NotificationSoundProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    @Override
    public AssetFileDescriptor openAssetFile(Uri uri, String mode) throws FileNotFoundException {
        String lastPathSegment = uri.getLastPathSegment();
        if (!lastPathSegment.endsWith(".wav") && !lastPathSegment.endsWith(".mp3")) {
            return null;
        }
        return new AssetDecompressor(getContext()).decompress(uri.getLastPathSegment());
    }
}