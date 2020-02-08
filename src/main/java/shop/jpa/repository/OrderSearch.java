package shop.jpa.repository;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.DeliveryStatus;
import shop.jpa.domain.OrderStatus;

@Getter @Setter
public class OrderSearch {

    private String memberName;
    private String title;

    private OrderStatus orderStatus;
    private DeliveryStatus deliveryStatus;
}
