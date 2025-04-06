package com.cntt2.flashcard.data.repository;

import android.content.Context;

import com.cntt2.flashcard.data.local.dao.DeskDao;
import com.cntt2.flashcard.model.Desk;

import java.util.List;

public class DeskRepository {
    private DeskDao deskDao;

    public DeskRepository(Context context) {
        this.deskDao = new DeskDao(context);
    }

    public long insertDesk(Desk desk) {
        return deskDao.insertDesk(desk);
    }

    public void deleteDesk(Desk desk) {
        deskDao.deleteDesk(desk.getId());
    }

    public List<Desk> getDesksByFolderId(int folderId) {
        return deskDao.getDesksByFolderId(folderId);
    }
}
