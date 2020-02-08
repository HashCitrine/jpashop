package shop.jpa.domain;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.item.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Review {

    @Id @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    private String title;
    private String memo;

    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;


    @OneToMany(mappedBy = "review")
    private List<Comment> comments = new ArrayList<>();

}
