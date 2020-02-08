package shop.jpa.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.jpa.domain.Comment;
import shop.jpa.domain.Review;
import shop.jpa.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public void saveComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional
    public List<Comment> findByMemberId(Long memberId){
        return commentRepository.findByMemberId(memberId);
    }

    @Transactional
    public void updateComment(Long id, String memo) {
        Comment comment = commentRepository.findOne(id);
        comment.setMemo(memo);
        comment.setDate(LocalDateTime.now());
    }

    public List<Comment> findComments(Review review) {
        return commentRepository.findAll(review);
    }

    public Comment findOne(Long parentId) {
        return commentRepository.findOne(parentId);
    }

    public Long getSequenceCount(LocalDateTime sequence) {
        return commentRepository.findBySequence(sequence);
    }

    @Transactional
    public Review deleteComment(Long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        Comment parent = commentRepository.findOne(comment.getParent());

        Review review = comment.getReview();

        // 부모 댓글이 삭제(null) 되어 있으면 자식과 함께 둘 다 삭제(remove)
        if(comment.getParent() != 0) {
            try {
                parent.getMemo();
            } catch (NullPointerException e) {
                e.printStackTrace();

                commentRepository.delete(parent);
                commentRepository.delete(comment);
                throw e;
            }
        }

        // 자식이 없는 댓글이면 삭제(remove)
        if(commentRepository.findByParentId(commentId).isEmpty()) {
            commentRepository.delete(commentRepository.findOne(commentId));
        } else{ // 자식이 있는 댓글이면 값을 null로 처리 -> '댓글이 삭제되었습니다.' 표시
            comment.setMemo(null);
            commentRepository.save(comment);
        }

        return review;
    }
}
