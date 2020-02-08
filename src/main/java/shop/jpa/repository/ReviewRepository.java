package shop.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.jpa.domain.Review;
import shop.jpa.domain.item.Item;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    private final EntityManager em;

    public void save(Review review) {
        if(review.getId() == null) {
            em.persist(review);
        } else {
            em.merge(review);
        }
    }

    public Review findOne(Long id) {
        return em.find(Review.class, id);
    }

    public List<Review> findAll() {
        return em.createQuery("select r from Review r", Review.class)
                .getResultList();
    }

    public List<Review> findByItemId(Long itemId) {
        return em.createQuery("select r from Review r where r.item.id =:itemId", Review.class)
                .setParameter("itemId", itemId)
                .getResultList();
    }

    public List<Review> findByMemberId(Long memberId) {
        return em.createQuery("select r from Review r where r.member.id =:memberId", Review.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 확인하고 삭제
    public Long findCount(Long itemId) {
        return em.createQuery("select count(r) from Review r where r.item.id = ?1", Long.class)
                .setParameter(1, itemId)
                .getSingleResult();
    }
}
