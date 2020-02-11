package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
public class ItemForm {
    private Long id;

    private String name;
    private String memo;

    private int price;
    private int stockQuantity;
    private int count;
    private int catergory;

    private MultipartFile itemImage;
    private String itemImageAddress;
}
