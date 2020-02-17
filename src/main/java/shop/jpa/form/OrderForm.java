package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.Address;
import shop.jpa.domain.Member;
import shop.jpa.domain.Order;
import shop.jpa.domain.item.Item;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class OrderForm {
    private Member member;
    private Item item;
    private Order order;

    private int orderPrice;

    private String postcode;
    private String mainAddress;
    private String extraAddress;
}
