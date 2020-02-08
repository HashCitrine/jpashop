package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReviewForm {
    private Long itemId;
    private Long reviewId;

    private String title;
    private String memo;

}
