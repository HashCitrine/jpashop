package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.DeliveryStatus;

@Getter @Setter
public class StatusForm {
    private DeliveryStatus deliveryStatus;
}
