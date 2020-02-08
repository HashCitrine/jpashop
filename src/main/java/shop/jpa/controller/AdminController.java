package shop.jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import shop.jpa.domain.*;
import shop.jpa.domain.item.Item;
import shop.jpa.form.ItemForm;
import shop.jpa.service.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {
    private final ItemService itemService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final PagingService pagingService;

    private final int unit = 10;
    private final int viewPage = 5;

    // admin 메인 페이지
    @GetMapping("")
    public String admin(HttpSession session, Model model) {
        Long memberId = memberService.getLoginMemberId(session);

        int page = 1;
        int unit = 5;
        int viewPage = 0;

        // ADMIN 일 때
        // item 목록 & 페이징
        List<Item> findItem = itemService.findItems();
        List<?> items = pagingService.getBoardPage(findItem, page, unit, viewPage, model);
        model.addAttribute("itemCount", findItem.size());
        model.addAttribute("items", items);

        // order 목록 & 페이징
        List<Order> findOrder = orderService.findOrders();
        List<?> orders = pagingService.getBoardPage(findOrder, page, unit, viewPage, model);
        model.addAttribute("orderCount", findOrder.size());
        model.addAttribute("orders", orders);

        // member 목록 & 페이징
        List<Member> findMember = memberService.findMembers();
        List<?> members = pagingService.getBoardPage(findMember, page, unit, viewPage, model);
        model.addAttribute("memberCount", findMember.size());
        model.addAttribute("members", members);

        // NORMAL 일 때
        // order 목록 & 페이징
        List<Order> findMyOrder = orderService.findMyOrders(memberId);
        List<?> myOrders = pagingService.getBoardPage(findMyOrder, page, unit, viewPage, model);
        model.addAttribute("myOrderCount", findMyOrder.size());
        model.addAttribute("myOrders", myOrders);

        // review 목록 & 페이징
        List<Review> findReview = reviewService.findByMemberId(memberId);
        List<?> reviews = pagingService.getBoardPage(findReview, page, unit, viewPage, model);
        model.addAttribute("reviewCount", findReview.size());
        model.addAttribute("reviews", reviews);

        // comment 목록 & 페이징
        List<Comment> findComment = commentService.findByMemberId(memberId);
        List<?> comments = pagingService.getBoardPage(findComment, page, unit, viewPage, model);
        model.addAttribute("commentCount", findComment.size());
        model.addAttribute("comments", comments);

        return "admin/adminIndex";
    }

    // 전체 회원 목록
    @GetMapping("members")
    public String members() {
        return "redirect:/admin/members/list/1";
    }
    @GetMapping("members/list")
    public String membersReturn() {
        return "redirect:/admin/members/list/1";
    }

    @GetMapping("members/list/{page}")
    public String memberList(@PathVariable("page") int page, Model model, HttpSession session) {
        // member 목록 & 페이징
        List<Member> findMember = memberService.findMembers();
        List<?> members = pagingService.getBoardPage(findMember, page, this.unit, this.viewPage, model);
        model.addAttribute("memberCount", findMember.size());
        model.addAttribute("members", members);

        return "admin/memberList";
    }

    // 전체 상품 목록
    @GetMapping("items")
    public String items() {
        return "redirect:/admin/items/list/1";
    }
    @GetMapping("items/list")
    public String itemsReturn() {
        return "redirect:/admin/items/list/1";
    }

    @GetMapping("items/list/{page}")
    public String itemList(@PathVariable("page") int page, Model model, HttpSession session) {
        // item 목록 & 페이징
        List<Item> findItem = itemService.findItems();
        List<?> items = pagingService.getBoardPage(findItem, page, this.unit, this.viewPage, model);
        model.addAttribute("itemCount", findItem.size());
        model.addAttribute("items", items);

        return "admin/itemList";
    }


    // 전체 주문 목록
    @GetMapping("orders")
    public String orders() {
        return "redirect:/admin/orders/list/1";
    }
    @GetMapping("orders/list")
    public String ordersReturn() {
        return "redirect:/admin/orders/list/1";
    }

    @GetMapping("orders/list/{page}")
    public String orderList(@PathVariable("page") int page, Model model, HttpSession session) {
        // order 목록 & 페이징
        List<Order> findOrder = orderService.findOrders();
        List<?> orders = pagingService.getBoardPage(findOrder, page, unit, viewPage, model);
        model.addAttribute("orderCount", findOrder.size());
        model.addAttribute("orders", orders);

        return "admin/orderList";
    }


    // 주문한 상품
    @GetMapping("myOrders")
    public String myOrders() {
        return "redirect:/admin/myOrders/list/1";
    }
    @GetMapping("myOrders/list")
    public String myOrdersReturn() {
        return "redirect:/admin/myOrders/list/1";
    }

    @GetMapping("myOrders/list/{page}")
    public String myOrderList(@PathVariable("page") int page, Model model, HttpSession session) {

        Member member = (Member)session.getAttribute("member");
        List<Order> findMyOrder = orderService.findMyOrders(member.getId());

        // order 목록 & 페이징
        List<?> myOrders = pagingService.getBoardPage(findMyOrder, page, this.unit, this.viewPage, model);
        model.addAttribute("myOrderCount", findMyOrder.size());
        model.addAttribute("myOrders", myOrders);
        return "admin/myOrderList";
    }

    // 후기 목록
    @GetMapping("reviews")
    public String reivews() {
        return "redirect:/admin/reviews/list/1";
    }
    @GetMapping("reviews/list")
    public String reivewsReturn() {
        return "redirect:/admin/reviews/list/1";
    }

    @GetMapping("reviews/list/{page}")
    public String reviewList(@PathVariable("page") int page, Model model, HttpSession session) {
        Member member = (Member)session.getAttribute("member");
        List<Review> findReview = reviewService.findByMemberId(member.getId());

        // review 목록 & 페이징
        List<?> reviews = pagingService.getBoardPage(findReview, page, unit, viewPage, model);
        model.addAttribute("reviewCount", findReview.size());
        model.addAttribute("reviews", reviews);
        return "admin/reviewList";
    }

    // 댓글 목록
    @GetMapping("comments")
    public String comments() {
        return "redirect:/admin/comments/list/1";
    }
    @GetMapping("comments/list")
    public String commentsReturn() {
        return "redirect:/admin/comments/list/1";
    }

    @GetMapping("comments/list/{page}")
    public String commentList(@PathVariable("page") int page, Model model, HttpSession session) {
        Member member = (Member)session.getAttribute("member");
        List<Comment> findComment = commentService.findByMemberId(member.getId());

        // comment 목록 & 페이징
        List<?> comments = pagingService.getBoardPage(findComment, page, unit, viewPage, model);
        model.addAttribute("commentCount", findComment.size());
        model.addAttribute("comments", comments);
        return "admin/commentList";
    }

    /**
     * 정렬
     * **/

    // 상품 정렬
    // 상품 판매순
    @GetMapping("items/sale")
    public String itemSalePath(){
        return "redirect:/admin/items/sale/1";
    }

    @GetMapping("items/sale/{page}")
    public String itemSaleOrder(@PathVariable("page") int page, Model model){

            List<?> items = pagingService.getBoardPage(itemService.orderBySaleCount() , page, this.unit, this.viewPage, model);
            model.addAttribute("items", items);
            model.addAttribute("itemCount", items.size());
            return "admin/itemList";
    }

    // 상품 후기
    @GetMapping("items/review")
    public String itemReviewPath(){
        return "redirect:/admin/items/review/1";
    }
    @GetMapping("items/review/{page}")
    public String itemReviewOrder(@PathVariable("page") int page, Model model){

        List<?> items = pagingService.getBoardPage(itemService.orderBySaleCount() , page, this.unit, this.viewPage, model);
        model.addAttribute("items", items);
        model.addAttribute("itemCount", items.size());
        return "admin/itemList";
        }


    // 주문 정렬
    @GetMapping("orders/{status}")
    public String orderListPath(@PathVariable("status") String Status){
        return "redirect:/admin/orders/{status}/1";
    }

    @GetMapping("orders/{status}/{page}")
    public String orderListReady(@PathVariable("page") int page, @PathVariable("status") String status, Model model){
        List<?> orders = pagingService.getBoardPage(orderService.findByStatus(status.toUpperCase()), page, this.unit, this.viewPage, model);
        model.addAttribute("orders", orders);
        model.addAttribute("orderCount", orders.size());
        model.addAttribute("sort", status);

        return "admin/orderList";
    }

    // 주문한 상품 정렬
    @GetMapping("myOrders/{status}")
    public String myOrderListPath(@PathVariable("status") String staus){
        return "redirect:/admin/myOrders/{status}/1";
    }

    @GetMapping("myOrders/{status}/{page}")
    public String myOrderListReady(@PathVariable("page") int page, @PathVariable("status") String status, Model model, HttpSession session){
        Member member = (Member)session.getAttribute("member");
        List<?> myOrders = pagingService.getBoardPage(orderService.findMyOrderByStatus(status.toUpperCase(), member.getId()), page, this.unit, this.viewPage, model);
        model.addAttribute("myOrders", myOrders);
        model.addAttribute("myOrderCount", myOrders.size());
        model.addAttribute("sort", status);

        return "admin/myOrderList";
    }


    // 폐기 예정
    @GetMapping("qna")
    public String adminQnaList() {
        return "admin/adminQnaList";
    }

    @GetMapping("test")
    public String adminQna(Model model) {
        model.addAttribute("form", new ItemForm());
        return "admin/adminQna";
    }
}
