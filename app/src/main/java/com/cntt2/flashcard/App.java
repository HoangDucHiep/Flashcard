package com.cntt2.flashcard;

import android.app.Application;

public class App extends Application {
    private static App instance;
//    private DatabaseHelper databaseHelper;
//    private FolderRepository folderRepository;
//    private DeskRepository deskRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        databaseHelper = new DatabaseHelper(this);
//        folderRepository = new FolderRepository(this);
//        deskRepository = new DeskRepository(this);
    }

    public static App getInstance() {
        return instance;
    }

//    public DatabaseHelper getDatabaseHelper() {
//        return databaseHelper;
//    }
//
//    public FolderRepository getFolderRepository() {
//        return folderRepository;
//    }
//
//    public DeskRepository getDeskRepository() {
//        return deskRepository;
//    }
}