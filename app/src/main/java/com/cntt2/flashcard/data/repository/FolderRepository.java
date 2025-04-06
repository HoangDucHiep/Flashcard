package com.cntt2.flashcard.data.repository;

import android.content.Context;

import com.cntt2.flashcard.data.local.dao.DeskDao;
import com.cntt2.flashcard.data.local.dao.FolderDao;
import com.cntt2.flashcard.model.Folder;

import java.util.List;

public class FolderRepository {
    private FolderDao folderDao;
    private DeskDao deskDao;

    public FolderRepository(Context context) {
        folderDao = new FolderDao(context);
        deskDao = new DeskDao(context);
    }

    public void insertFolder(Folder folder) {
        folderDao.insertFolder(folder);
    }

    public void deleteFolder(Folder folder) {
        folderDao.deleteFolder(folder.getId());
    }

    public List<Folder> getAllFolders() {
        List<Folder> folders = folderDao.getAllFolders();
        for (Folder folder : folders) {
            folder.setDesks(deskDao.getDesksByFolderId(folder.getId()));
        }
        return folders;
    }
}
