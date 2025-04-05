package com.cntt2.flashcard.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.cntt2.flashcard.R;
import com.cntt2.flashcard.animation.ZoomOutPageTransformer;
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

        // Đảm bảo sử dụng HTML khi cần thiết
        cardList.add(new Card(
                "Dog\n <p>ý là chất lượng dịch vụ của cs nguyễn trãi bị giảm mạnh luôn ấy ạ? Mình còn tưởng mình bị overthinking chứ bạn mình cũng thấy vậy. Mong cs mình train lại nhân viên chứ mặt hằm hằm xong cứ nói được câu là bỏ đi không nói lời nào, ý là không tôn trọng mình luôn ấy :)) mình chẳng dám đi nữa luôn ấy \uD83D\uDE42\n</p>",
                wrapHtml("<p>This is a default toolbar demo of Rich text editor.</p>\n" +
                        "<p><img src=\"https://richtexteditor.com/images/editor-image.png\" width=\"100%\"/></p>"+
                        "<p>Dogs are mammals with sharp teeth, an excellent sense of smell, and a fine sense of hearing. Each of a dog’s four legs ends in a foot, or paw, with five toes. Each toe has a soft pad and a claw. A coat of hair keeps the dog warm. It cools off by panting and hanging its tongue out of its mouth.\n" +
                        "\n" +
                        "Apart from these common features, dogs come in many different sizes, shapes, and colors. Dogs that have similar sizes, looks, and behaviors make up groups called breeds. There are more than 400 different breeds of dog. Many dogs are combinations of different breeds. They are known as mutts. Some dogs are combinations of particular breeds. For example, a Labradoodle is a combination of a Labrador Retriever and a Poodle. Some of the most popular breeds are Beagles, Boxers, Bulldogs, Collies, Dachshunds, </p>"
                        +"<p><img src=\"https://richtexteditor.com/images/editor-image.png\" width=\"100%\"/></p>"
                ),
                "2025-04-05"
        ));

        cardList.add(new Card("Cat", "Mèo", "2025-04-05"));
        cardList.add(new Card("Book", "Sách", "2025-04-05"));

        // Tạo adapter và gắn vào ViewPager2
        cardAdapter = new CardAdapter(cardList);
        viewPager.setAdapter(cardAdapter);

        // Thêm hiệu ứng hoạt ảnh khi chuyển thẻ (tuỳ chọn)
        viewPager.setPageTransformer(new ZoomOutPageTransformer());
    }

    // Hàm gói nội dung HTML
    private String wrapHtml(String content) {
        return "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0' />"
                + "<style>body{font-size:16px;text-align:center;word-wrap:break-word;max-width:100%; margin: 0 auto; max-height: 2000px; overflow-y: scroll;} img{max-width:100%; height:auto;}</style></head><body>"
                + content + "</body></html>";
    }

}
