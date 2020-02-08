package shop.jpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.jpa.domain.Cart;
import shop.jpa.domain.item.Item;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartRepository {
    private final EntityManager em;

    public void save(Cart cart) {
        if(cart.getId() == null) {
            em.persist(cart);
        } else {
            em.merge(cart);
        }
    }

    public List<Cart> findByMemberId(Long memberId) {
        return em.createQuery("select c from Cart c where c.member.id =:memberId and c.buy = false", Cart.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public void remove(Cart cart){
        em.remove(cart);
    }

    public Cart findOne(Long id) {
        return em.find(Cart.class, id);
    }
}
