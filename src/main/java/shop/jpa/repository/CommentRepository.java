package shop.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.jpa.domain.Comment;
import shop.jpa.domain.Review;
import shop.jpa.domain.item.Item;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final EntityManager em;

    public void save(Comment comment) {
        if(comment.getId() == null) {
            em.persist(comment);
        } else {
            em.merge(comment);
        }
    }

    public Comment findOne(Long id) {
        return em.find(Comment.class, id);
    }

    public List<Comment> findAll(Review review) {
        return em.createQuery("select c from Comment c where c.review =:review ORDER BY c.sequence ASC, c.date ASC", Comment.class)
                .setParameter("review", review)
                .getResultList();
    }

    public Long findBySequence(LocalDateTime date) {
        return em.createQuery("select count(c) from Comment c where c.sequence=?1", Long.class)
                .setParameter(1, date)
                .getSingleResult();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

    public List<Comment> findByParentId(Long commentId) {
        return em.createQuery("select c from Comment c where c.parent = :parentId", Comment.class)
                .setParameter("parentId", commentId)
                .getResultList();
    }

    public List<Comment> findByMemberId(Long memberId){
        return em.createQuery("select c from Comment c where c.member.id =:memberId", Comment.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

}
