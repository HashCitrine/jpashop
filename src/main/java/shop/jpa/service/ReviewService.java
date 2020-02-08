package shop.jpa.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.jpa.domain.Review;
import shop.jpa.domain.item.Item;
import shop.jpa.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Transactional
    public void saveReview(Review reivew) {
        reviewRepository.save(reivew);
    }

    @Transactional
    public List<Review> findByMemberId(Long memberId){
        return reviewRepository.findByMemberId(memberId);
    }

    @Transactional
    public void updateReview(Long id, String memo) {
        Review review = reviewRepository.findOne(id);
        review.setMemo(memo);
        review.setDate(LocalDateTime.now());
    }

    public Review findOne(Long reviewId) {
        return reviewRepository.findOne(reviewId);
    }

    public List<Review> findByItemId(Long itemId) {
        return reviewRepository.findByItemId(itemId);
    }

}
