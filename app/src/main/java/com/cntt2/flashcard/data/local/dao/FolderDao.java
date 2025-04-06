package com.cntt2.flashcard.data.local.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cntt2.flashcard.data.local.DatabaseHelper;
import com.cntt2.flashcard.model.Folder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FolderDao {
    private DatabaseHelper dbHelper;

    public FolderDao(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertFolder(Folder folder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (folder.getParentFolderId() != null) {
            values.put("parent_folder_id", folder.getParentFolderId());
        }
        values.put("name", folder.getName());
        values.put("created_at", folder.getCreatedAt());
        long id = db.insert("folders", null, values);
        db.close();
        return id;
    }

    public void deleteFolder(int folderId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("folders", "id = ?", new String[]{String.valueOf(folderId)});
        db.close();
    }

    @SuppressLint("Range")
    public List<Folder> getAllFolders() {
        List<Folder> allFolders = new ArrayList<>();
        Map<Integer, Folder> folderMap = new HashMap<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM folders", null);
        if (cursor.moveToFirst()) {
            do {
                Folder folder = new Folder();
                folder.setId(cursor.getInt(cursor.getColumnIndex("id")));
                int parentFolderId = cursor.isNull(cursor.getColumnIndex("parent_folder_id")) ? -1 : cursor.getInt(cursor.getColumnIndex("parent_folder_id"));
                folder.setParentFolderId(parentFolderId == -1 ? null : parentFolderId);
                folder.setName(cursor.getString(cursor.getColumnIndex("name")));
                folder.setCreatedAt(cursor.getString(cursor.getColumnIndex("created_at")));
                folderMap.put(folder.getId(), folder);
                allFolders.add(folder);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // build the hierarchy
        for (Folder folder : allFolders) {
            if (folder.getParentFolderId() != null) {
                Folder parentFolder = folderMap.get(folder.getParentFolderId());
                if (parentFolder != null) {
                    parentFolder.addSubFolder(folder);
                }
            }
        }

        // filter root folders
        List<Folder> rootFolders = new ArrayList<>();
        for (Folder folder : allFolders) {
            if (folder.getParentFolderId() == null) {
                rootFolders.add(folder);
            }
        }

        db.close();
        return rootFolders;
    }
}