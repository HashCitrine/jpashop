package shop.jpa.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {

    private String address;
    private String postcode;

    protected Address() {
    }

    public Address(String address, String postcode) {
        this.address = address;
        this.postcode = postcode;
    }
}
