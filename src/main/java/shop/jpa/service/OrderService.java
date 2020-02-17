package shop.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.jpa.domain.*;
import shop.jpa.repository.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    /**
     * 주문
     */
    @Transactional
    public Long order(HttpSession session, Address address, List<Cart> carts) {
        // 엔티티 조회
        Member member = (Member)session.getAttribute("member");

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(address);
        delivery.setStatus(DeliveryStatus.READY);

        // 주문 생성
        Order order = Order.createOrder(memberRepository.findOne(member.getId()), delivery, carts);

        // 주문 저장
        orderRepository.save(order);

        // 카트 상태 저장
        cartService.order(carts);

        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔터티
        Order order = orderRepository.findOne(orderId);

        // 주문 취소
        order.cancel();
    }

    @Transactional
    public void deleteItem(Long orderId) {
        orderRepository.delete(orderRepository.findOne(orderId));
    }

    // 검색
    public List<Order> findOrders() {
    return orderRepository.findAll();
    }

    public List<Order> findSortOrders(OrderSearch OrderSearch) {
        return orderRepository.findAllByString(OrderSearch);
    }


    // 주문 수정
    @Transactional
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }


    // 주문 상태에 따라 정렬
    public List<Order> findByStatus(String status){

        OrderSearch orderSearch = new OrderSearch();
        System.out.println("status : " + status + ", status.equals(\"CANCEL\") : " + status.equals("CANCEL"));
        if(!status.equals("CANCEL")) {
            orderSearch.setDeliveryStatus(DeliveryStatus.valueOf(status.toUpperCase()));
            orderSearch.setOrderStatus(OrderStatus.ORDER);
        } else {
            orderSearch.setOrderStatus(OrderStatus.CANCEL);
        }

        return orderRepository.findByStatus(orderSearch);
    }


    // 내주문 조회
    public List<Order> findMyOrders(Long memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    // 내주문 정렬
    public List<Order> findMyOrderByStatus(String status, Long memberId){

        OrderSearch orderSearch = new OrderSearch();

        if(!status.equals("CANCEL")) {
            orderSearch.setDeliveryStatus(DeliveryStatus.valueOf(status.toUpperCase()));
            orderSearch.setOrderStatus(OrderStatus.ORDER);
        } else {
            orderSearch.setOrderStatus(OrderStatus.CANCEL);
        }

        return orderRepository.findMyOrderByStatus(orderSearch, memberId);
    }

    public Order findById(Long orderId) {
        return orderRepository.findOne(orderId);
    }
}