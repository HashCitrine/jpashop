package shop.jpa.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Column(name="email", unique = true)
    private String email;

    private String password;

    private boolean verify;

    private String verifyCode;

    @Enumerated(EnumType.STRING)
    private Role role;


    /*
    @Embedded
    private Address address;

     */

    @Column(name="date")
    private LocalDateTime date;

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Cart> carts = new ArrayList<>();

    public boolean getVerify() {
        return this.verify;
    }
}
