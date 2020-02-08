package shop.jpa.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Comment {

    @Id @GeneratedValue
    @Column(name = "Comment_id")
    private Long id;

    private String memo;
    private LocalDateTime date;
    private Long parent;

    private LocalDateTime sequence;
    private Long num;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;

/*    public void setMember(Member member) {
        this.member = member;
        member.getComments().add(this);
    }

 */
}
