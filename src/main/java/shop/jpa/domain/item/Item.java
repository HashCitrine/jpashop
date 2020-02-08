package shop.jpa.domain.item;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.Cart;
import shop.jpa.domain.Comment;
import shop.jpa.domain.Review;
import shop.jpa.domain.exception.NotEnoughStockException;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter @Setter
public class Item {

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private String memo;

    private int price;
    private int stockQuantity;
    private LocalDateTime date;

    private String itemImage;

    @OneToMany(mappedBy = "item")
    private List<Cart> carts = new ArrayList<>();


    @ManyToMany(mappedBy = "items", fetch = LAZY)
    private List<Category> category = new ArrayList<>();


    //=비즈니스 로직==/
    /**
     * stock 증가
     */

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}
