package com.cntt2.flashcard.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cntt2.flashcard.R;
import com.cntt2.flashcard.ZoomOutPageTransformer;
import com.cntt2.flashcard.adapter.CardAdapter;
import com.cntt2.flashcard.model.Card;

import java.util.ArrayList;
import java.util.List;

public class StudyActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study); // Sử dụng layout study.xml

        // Ánh xạ ViewPager2
        viewPager = findViewById(R.id.viewPager);

        // Tạo danh sách thẻ (sample data)
        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("Apple", "Táo", "2025-04-05"));
        cardList.add(new Card("Dog", "Chó", "2025-04-05"));
        cardList.add(new Card("Book", "Sách", "2025-04-05"));

        // Tạo adapter và gắn vào ViewPager2
        cardAdapter = new CardAdapter(cardList);
        viewPager.setAdapter(cardAdapter);

        // Thêm hiệu ứng hoạt ảnh khi chuyển thẻ (tuỳ chọn)
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }
}
