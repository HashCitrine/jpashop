package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentForm {
    private Long itemId;
    private Long reviewId;

    private Long parentId;
    private String memo;

}
