package shop.jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import shop.jpa.domain.*;
import shop.jpa.domain.item.Item;
import shop.jpa.form.ItemForm;
import shop.jpa.form.OrderForm;
import shop.jpa.form.StatusForm;
import shop.jpa.service.CartService;
import shop.jpa.service.ItemService;
import shop.jpa.service.MemberService;
import shop.jpa.service.OrderService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final CartService cartService;

    // 장바구니 등록
    @PostMapping("add/{itemId}/")
    public String addCart(@PathVariable("itemId") Long itemId, @ModelAttribute("itemForm") ItemForm itemForm, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        Cart cart = new Cart();
        cartService.add((Member)session.getAttribute("member"), itemService.findOne(itemId), itemForm.getCount());

        // redirect:/shop/{itemId}
        return "others/addCart";
    }

    // 장바구니 삭제
    @GetMapping("/{cartId}/remove/")
    public String removeCart(@PathVariable("cartId") Long cartId, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }
        cartService.remove(cartId, (Member)session.getAttribute("member"));

        return "redirect:/back";
    }

    // 장바구니 폼
    @GetMapping("cart")
    public String cartFrom(Model model, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }
        Member member = memberService.getLoginMember(session);
        List<Cart> carts = cartService.findCart(member.getId());

        model.addAttribute("carts", carts);
        model.addAttribute("totalPrice", cartService.getTotalPrice(member.getId()));
        return "order/cart";
    }

    // 상품 구매폼
    @GetMapping("order")
    public String orderForm(Model model, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }
        OrderForm form = new OrderForm();
        model.addAttribute("form", form);

        Member member = memberService.getLoginMember(session);
        List<Cart> carts = cartService.findCart(member.getId());

        model.addAttribute("carts", carts);
        model.addAttribute("totalPrice", cartService.getTotalPrice(member.getId()));
        model.addAttribute("date", LocalDateTime.now());

        return "order/orderPage";
    }

    // 상품 구매
    @PostMapping("buy")
    public String orderItem(@Valid @ModelAttribute("form") OrderForm form, Model model, HttpSession session, BindingResult result) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        if (form.getMainAddress().isEmpty()) {
            FieldError fieldError  = new FieldError("form", "mainAddress", "주소를 입력해야 합니다.");
            result.addError(fieldError);

            // 주문서에 필요한 내용을 다시 반
            Member member = memberService.getLoginMember(session);
            List<Cart> carts = cartService.findCart(member.getId());

            model.addAttribute("carts", carts);
            model.addAttribute("totalPrice", cartService.getTotalPrice(member.getId()));
            model.addAttribute("date", LocalDateTime.now());
            return "order/orderPage";
        }


        Address address = new Address(form.getMainAddress() + form.getExtraAddress(), form.getPostcode());

        Member member = memberService.getLoginMember(session);
        List<Cart> carts = cartService.findCart(member.getId());

        orderService.order(session, address, carts);

        return "order/orderSuccess";
    }

    // 주문 수정폼(상세보기)
    @GetMapping("/orders/update/{orderId}")
    public String orderUpdateForm(@PathVariable("orderId") Long orderId, Model model, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        Order order = orderService.findById(orderId);
        Member member = memberService.getLoginMember(session);

        if (order.getMember().getId() != member.getId() && member.getRole() != Role.ADMIN) {
            boolean a = order.getMember().getId() != member.getId();
            boolean b = member.getRole() != Role.ADMIN;

            System.out.println( a+ ", " + b);
            return "redirect:/";
        }

        List<Cart> carts = order.getCarts();

        int totalPrice = 0;
        for (Cart cart : carts) {
            totalPrice += cart.getUnitPrice();
        }

        OrderForm orderForm = new OrderForm();
        Address address = order.getDelivery().getAddress();

        orderForm.setMainAddress(address.getAddress());
        orderForm.setPostcode(address.getPostcode());
        orderForm.setMember(order.getMember());
        orderForm.setOrder(order);
        orderForm.setOrderPrice(totalPrice);

        StatusForm statusForm = new StatusForm();
        statusForm.setDeliveryStatus(order.getDelivery().getStatus());

        model.addAttribute("carts", carts);
        model.addAttribute("orderForm", orderForm);
        model.addAttribute("statusForm", statusForm);
        model.addAttribute("statusGroup", DeliveryStatus.values());

        return "order/orderUpdate";
    }


    // 주문 수정
    @PostMapping("/orders/update/{orderId}")
    public String orderUpdate(@PathVariable("orderId") Long orderId, @ModelAttribute("statusForm") StatusForm statusForm, Model model, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }
        Order order = orderService.findById(orderId);
        order.getDelivery().setStatus(statusForm.getDeliveryStatus());

        orderService.updateOrder(order);

        return "redirect:/orders/update/{orderId}";
    }


    // 주문 취소
    @GetMapping("/orders/cancel/{orderId}")
    public String orderCancel(@PathVariable("orderId") Long orderId, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        Order order = orderService.findById(orderId);
        if(order.getDelivery().getStatus() == DeliveryStatus.READY && order.getStatus() != OrderStatus.CANCEL) {
            orderService.cancelOrder(orderId);
            return "redirect:/back";
        }

        return "order/alreadyDelivery";
    }


    // 재주문
    @GetMapping("/orders/reorder/{orderId}")
    public String reorder(@PathVariable("orderId") Long orderId, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }
        Order order = orderService.findById(orderId);
        order.setStatus(OrderStatus.ORDER);
        order.getDelivery().setStatus(DeliveryStatus.READY);

        orderService.updateOrder(order);

        return "redirect:/orders/update/{orderId}";
    }

}
