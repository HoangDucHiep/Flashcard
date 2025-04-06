package com.cntt2.flashcard.ui.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cntt2.flashcard.App;
import com.cntt2.flashcard.R;
import com.cntt2.flashcard.model.Card;
import com.cntt2.flashcard.model.Desk;
import com.cntt2.flashcard.model.Folder;
import com.cntt2.flashcard.model.LearningSession;
import com.cntt2.flashcard.model.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        testDatabase();
    }

    private void testDatabase() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // 1. Thêm một folder gốc
        Folder rootFolder = new Folder("Root Folder", currentDate);
        App.getInstance().getFolderRepository().insertFolder(rootFolder);
        Log.d(TAG, "Inserted Root Folder: " + rootFolder.getName());

        // 2. Thêm một folder con
        List<Folder> rootFolders = App.getInstance().getFolderRepository().getAllFolders();
        if (!rootFolders.isEmpty()) {
            Folder parentFolder = rootFolders.get(0);
            Folder subFolder = new Folder("Sub Folder", currentDate);
            subFolder.setParentFolderId(parentFolder.getId());
            App.getInstance().getFolderRepository().insertFolder(subFolder);
            Log.d(TAG, "Inserted Sub Folder: " + subFolder.getName() + " into " + parentFolder.getName());
        }

        if (!rootFolders.isEmpty()) {
            Folder parentFolder = rootFolders.get(0);
            Desk desk = new Desk("English Vocabulary", parentFolder.getId(), currentDate);
            App.getInstance().getDeskRepository().insertDesk(desk);
            Log.d(TAG, "Inserted Desk: " + desk.getName() + " into " + parentFolder.getName());
        }

        // 4. Thêm một desk vào folder con
        if (!rootFolders.isEmpty() && !rootFolders.get(0).getSubFolders().isEmpty()) {
            Folder subFolder = rootFolders.get(0).getSubFolders().get(0);
            Desk subDesk = new Desk("Math Formulas", subFolder.getId(), currentDate);
            App.getInstance().getDeskRepository().insertDesk(subDesk);
            Log.d(TAG, "Inserted Desk: " + subDesk.getName() + " into " + subFolder.getName());
        }

        // 5. Thêm một card với ảnh vào desk
        List<Folder> folders = App.getInstance().getFolderRepository().getAllFolders();
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            Card card = new Card("Hello", "Xin chào", currentDate);
            card.setDeskId(targetDesk.getId());
            App.getInstance().getCardRepository().insertCard(card);
            Log.d(TAG, "Inserted Card: " + card.getFront() + " with Front Image: " + ", Back Image: ");
        }

        // 6. Thêm một card không có ảnh
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            Card cardNoImage = new Card("Good morning", "Chào buổi sáng", currentDate);
            cardNoImage.setDeskId(targetDesk.getId());
            App.getInstance().getCardRepository().insertCard(cardNoImage);
            Log.d(TAG, "Inserted Card without Image: " + cardNoImage.getFront());
        }

        // 7. Thêm một review cho card
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            List<Card> cards = App.getInstance().getCardRepository().getCardsByDeskId(targetDesk.getId());
            if (!cards.isEmpty()) {
                Card targetCard = cards.get(0);
                Review review = new Review(targetCard.getId(), 2.5, 1, 0, currentDate);
                App.getInstance().getReviewRepository().insertReview(review);
                Log.d(TAG, "Inserted Review for Card: " + targetCard.getFront());
            }
        }

        // 8. Thêm một learning session
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            LearningSession session = new LearningSession(targetDesk.getId(), currentDate);
            session.setEndTime(currentDate);
            session.setCardsStudied(2);
            session.setPerformance(0.8);
            App.getInstance().getLearningSessionRepository().insertSession(session);
            Log.d(TAG, "Inserted Learning Session for Desk: " + targetDesk.getName());
        }

        // 9. Truy vấn và in dữ liệu
        Log.d(TAG, "=== All Folders ===");
        List<Folder> allFolders = App.getInstance().getFolderRepository().getAllFolders();
        for (Folder folder : allFolders) {
            Log.d(TAG, "Folder: " + folder.getName() + ", SubFolders: " + folder.getSubFolders().size() + ", Desks: " + folder.getDesks().size());
            for (Desk desk : folder.getDesks()) {
                Log.d(TAG, "  Desk: " + desk.getName());
            }
            for (Folder subFolder : folder.getSubFolders()) {
                Log.d(TAG, "  SubFolder: " + subFolder.getName() + ", Desks: " + subFolder.getDesks().size());
                for (Desk subDesk : subFolder.getDesks()) {
                    Log.d(TAG, "    Desk: " + subDesk.getName());
                }
            }
        }

        Log.d(TAG, "=== Cards in First Desk ===");
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            List<Card> deskCards = App.getInstance().getCardRepository().getCardsByDeskId(targetDesk.getId());
            for (Card card : deskCards) {
                Log.d(TAG, "Card: " + card.getFront() + " / " + card.getBack());
            }
        }

        Log.d(TAG, "=== Reviews Due Today ===");
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            List<Review> reviews = App.getInstance().getReviewRepository().getReviewsDueToday(targetDesk.getId(), currentDate);
            for (Review review : reviews) {
                Log.d(TAG, "Review: Card ID " + review.getCardId() + ", Next Review: " + review.getNextReviewDate());
            }
        }

        Log.d(TAG, "=== Learning Sessions ===");
        if (!folders.isEmpty() && !folders.get(0).getDesks().isEmpty()) {
            Desk targetDesk = folders.get(0).getDesks().get(0);
            List<LearningSession> sessions = App.getInstance().getLearningSessionRepository().getSessionsByDeskId(targetDesk.getId());
            for (LearningSession session : sessions) {
                Log.d(TAG, "Session: Start " + session.getStartTime() + ", Cards Studied: " + session.getCardsStudied());
            }
        }

    }
}