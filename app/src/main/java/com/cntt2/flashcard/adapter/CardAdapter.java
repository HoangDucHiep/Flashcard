package com.cntt2.flashcard.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cntt2.flashcard.R;
import com.cntt2.flashcard.model.Card;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private  final List<Card> cardList;
    public CardAdapter(List<Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flashcard, parent, false);
        return new CardViewHolder(view);
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder {
        WebView webFront, webBack;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            webFront = itemView.findViewById(R.id.webFront);
            webBack = itemView.findViewById(R.id.webBack);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        // Chỉ load HTML khi cần thiết
        holder.webFront.loadDataWithBaseURL(null, wrapHtml(card.getFront()), "text/html", "UTF-8", null);
        holder.webBack.loadDataWithBaseURL(null, wrapHtml(card.getBack()), "text/html", "UTF-8", null);

        // Đảm bảo mặt trước luôn hiển thị khi thẻ mới được chuyển đến hoặc khi thẻ được tái sử dụng
        holder.webFront.setVisibility(View.VISIBLE);
        holder.webBack.setVisibility(View.GONE);



        // Xử lý sự kiện click vào thẻ
        holder.itemView.setOnClickListener(v -> {
            // Flip giữa mặt trước và mặt sau khi click
            if (holder.webBack.getVisibility() == View.GONE) {
                flipCard(holder.webFront, holder.webBack);
            } else {
                flipCard(holder.webBack, holder.webFront);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }



    // Hàm tạo hiệu ứng flip
    private void flipCard(View fromView, View toView) {
        ObjectAnimator hideFront = ObjectAnimator.ofFloat(fromView, "rotationY", 0f, 90f);
        ObjectAnimator showBack = ObjectAnimator.ofFloat(toView, "rotationY", -90f, 0f);

        hideFront.setDuration(200);
        showBack.setDuration(200);

        hideFront.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Ẩn mặt trước và hiển thị mặt sau
                fromView.setVisibility(View.GONE);
                toView.setVisibility(View.VISIBLE);
                showBack.start();
            }
        });

        // Đảm bảo rằng hiệu ứng flip chỉ xảy ra khi có mặt trước và mặt sau
        if (fromView.getVisibility() == View.VISIBLE && toView.getVisibility() == View.GONE) {
            hideFront.start();
        }
    }

    // Hàm gói nội dung HTML
    private String wrapHtml(String content) {
        return "<html><head><meta name='viewport' content='width=device-width, initial-scale=1.0' />"
                + "<style>body{font-size:16px;text-align:center;word-wrap:break-word;max-width:100%; margin: 0 auto; max-height: 2000px; overflow-y: scroll;} img{max-width:100%; height:auto;}</style></head><body>"
                + content + "</body></html>";
    }

}
