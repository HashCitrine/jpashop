package shop.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.jpa.domain.Cart;
import shop.jpa.domain.Member;
import shop.jpa.domain.item.Item;
import shop.jpa.repository.CartRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {
    private final CartRepository cartRepository;

    @Transactional
    public void add(Member member, Item item, int count) {
        Cart cart = new Cart();

        for(Cart c : findCart(member.getId())) {
           if(c.getItem() == item){
               c.setCount(c.getCount()+count);
               cartRepository.save(c);
               return;
           }
        }

        cart.setItem(item);
        cart.setMember(member);
        cart.setCount(count);
        cart.setBuy(false);

        cartRepository.save(cart);
    }

    @Transactional
    public void order(List<Cart> carts) {
        for (Cart cart : carts) {
            cart.setBuy(true);
            cartRepository.save(cart);
        }
    }

    public List<Cart> findCart(Long memberId) {
        return cartRepository.findByMemberId(memberId);
    }

    public int getTotalPrice(Long memberId) {
        List<Cart> carts = cartRepository.findByMemberId(memberId);

        int totalPrice = 0;
        for (Cart cart : carts) {
            totalPrice += cart.getUnitPrice();
        }

        return totalPrice;
    }


    @Transactional
    public void remove(Long cartId, Member member) {
        Cart cart = cartRepository.findOne(cartId);

        if (member.getId() == cart.getMember().getId()) {
            cartRepository.remove(cart);
        }
    }
}
