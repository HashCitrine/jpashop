package shop.jpa.domain;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.item.Item;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

    public int getUnitPrice(){
        return item.getPrice() * this.count;
    }

    public static int getTotalPrice(List<Cart> carts, Member member) {
        int totalPrice = 0;
        for(Cart cart : carts) {
            if(cart.member.getId() == member.getId()) {
                totalPrice = cart.getUnitPrice();
            }
        }
        return totalPrice;
    }

}
