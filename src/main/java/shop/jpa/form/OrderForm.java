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

    @Min(value = 1, message = "1개 이상 주문하실 수 있습니다.")
    private int count;
    private int orderPrice;

    @NotEmpty(message = "주소를 입력해야 합니다.")
    private String postcode;
    @NotEmpty
    private String mainAddress;
    private String extraAddress;
}
