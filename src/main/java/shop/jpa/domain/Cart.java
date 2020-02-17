package shop.jpa.domain;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.item.Item;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
public class Cart {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Member member;

    @ManyToOne(fetch = LAZY)
    private Item item;
    private int count;

    private Boolean buy;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    //==비즈니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }

    /**
     * 주문상품 전체 가격 조회
     */

    public int getUnitPrice(){
        return item.getPrice() * this.count;
    }



}
