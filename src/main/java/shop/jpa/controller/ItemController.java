package shop.jpa.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.jpa.domain.Comment;
import shop.jpa.domain.Member;
import shop.jpa.domain.Review;
import shop.jpa.domain.item.Book;
import shop.jpa.form.CommentForm;
import shop.jpa.form.ItemForm;
import shop.jpa.form.ReviewForm;
import shop.jpa.service.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final MemberService memberService;
    private final ReviewService reviewService;
    private final CommentService commentService;
    private final PagingService pagingService;

    @GetMapping(value="new")
    public String createItemForm(Model model, HttpSession session){
        model.addAttribute("form", new ItemForm());
        return "item/createItem";
    }


    // 상품 등록
    @PostMapping(value="/new")
    public String createItem(@Valid @ModelAttribute("form") ItemForm form, Model model, HttpSession session) throws Exception {
        Book book = new Book();
        System.out.println(book.getItemImage());

        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setMemo(form.getMemo());

        book.setDate(LocalDateTime.now());

        book.setItemImage(itemService.uploadFile(form.getItemImage()));

        System.out.println(book.getItemImage());

        itemService.saveItem(book);
        return "redirect:/";
    }

    /*
    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }
*/


    // 아이템 단독창
    @GetMapping("/shop/{itemId}")
    public String viewItem(@PathVariable("itemId") Long itemId, Model model, HttpSession session) {
        Book item = (Book) itemService.findOne(itemId);

        ItemForm itemForm = new ItemForm();
        itemForm.setId(item.getId());

        itemForm.setName(item.getName());
        itemForm.setPrice((item.getPrice()));
        itemForm.setStockQuantity(item.getStockQuantity());
        itemForm.setMemo(item.getMemo());
        itemForm.setItemImageAddress(item.getItemImage());
        model.addAttribute("itemForm", itemForm);

        List<Review> reviews = reviewService.findByItemId(item.getId());

        model.addAttribute("reviews", pagingService.getBoardPage(reviews, 1, 3, 1, model));
        model.addAttribute("reviewCount", reviews.size());
        return "itemPage";

    }

    /*
    상품 수정 폼
     */
    @GetMapping(value = "/update/{itemId}")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book updateItem = (Book) itemService.findOne(itemId);

        ItemForm form = new ItemForm();
        form.setId(updateItem.getId());

        form.setName(updateItem.getName());
        form.setPrice((updateItem.getPrice()));
        form.setStockQuantity(updateItem.getStockQuantity());
        form.setMemo(updateItem.getMemo());
        form.setItemImageAddress(updateItem.getItemImage());
        model.addAttribute("form", form);

        return "item/updateItem";
    }
        /*
        상품 수정
         */

    @PostMapping(value = "/update/{itemId}")
    public String updateItem(@ModelAttribute("form") ItemForm form, @PathVariable("itemId") Long itemId) {
        /* merge 병합 방법
        Book book = new Book();
        book.setId(form.getId());
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
         */

        // 변경 감지 방법
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity(), form.getMemo());

        return "redirect:/admin/";

    }

    // 상품 삭제
    @GetMapping(value = "/delete/{itemId}")
    public String deleteItem(@PathVariable("itemId") Long itemId) {
        itemService.deleteItem(itemId);
        return "redirect:/admin/";

    }

    // 상품 리뷰 목록
    @GetMapping("/{itemId}/reviews/{page}")
    public String reviews(@PathVariable("itemId") Long itemId, @PathVariable("page") int page, Model model, HttpSession session) {
        List<Review> reviews = reviewService.findByItemId(itemId);
        pagingService.getBoardPage(reviews, page, 10, 5, model);
        model.addAttribute("reviewCount", reviews.size());

        /*
        model.addAttribute("reviews", reviewService.getReviews(itemId, page));
        model.addAttribute("itemId", itemId);

        reviewService.setStartEndPage(page, itemId);

        model.addAttribute("start", reviewService.getStart());
        model.addAttribute("end", reviewService.getEnd());
        model.addAttribute("lastPage", reviewService.getAllReviewCount(itemId) / 5 + 1);
         */

        return "itemReview";
    }

    // 리뷰 등록폼
    @GetMapping("/{itemId}/review/new")
    public String createReviewForm(@PathVariable("itemId") Long itemId, Model model, HttpSession session) {
        ReviewForm form = new ReviewForm();
        form.setItemId(itemId);

        model.addAttribute("form",form);

        return "review/createReview";
    }

    // 리뷰 등록
    @PostMapping("/{itemId}/review/new")
    public String createReview(@PathVariable("itemId") Long itemId, @ModelAttribute("form") ReviewForm form, Model model, HttpSession session) {
        Long memberId = memberService.getLoginMemberId(session);
        Review review = new Review();

        review.setTitle(form.getTitle());
        review.setMemo(form.getMemo());
        review.setDate(LocalDateTime.now());

        System.out.println("form itemId : " + form.getItemId());

        review.setItem(itemService.findOne(form.getItemId()));
        review.setMember(memberService.findOne(memberId));

        reviewService.saveReview(review);

        return "redirect:/shop/{itemId}";
    }


    // 리뷰 수정폼
    @GetMapping("/update/review/{reviewId}}")
    public String updateReviewForm(@PathVariable("reviewId") Long reviewId, Model model, HttpSession session) {
        Long memberId = memberService.getLoginMemberId(session);
        Review review = reviewService.findOne(reviewId);

        if(memberId != review.getMember().getId() || session.getAttribute("Role").equals("ADMIN")){
            return "error";
        }

        ReviewForm form = new ReviewForm();
        form.setReviewId(reviewId);
        form.setTitle(review.getTitle());
        form.setMemo(review.getMemo());

        model.addAttribute("form",form);

        return "review/createReview";
    }

    // 리뷰 수정
    @PostMapping("/update/review/{reviewId}")
    public String updateReview(@PathVariable("reviewId") Long reviewId, @ModelAttribute("form") ReviewForm form, Model model, HttpSession session) {

        Review review = reviewService.findOne(reviewId);

        review.setTitle(form.getTitle());
        review.setMemo(form.getMemo() + " [수정 : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")) + "]");

        reviewService.saveReview(review);

        return "redirect:/shop/{itemId}";
    }



    // 리뷰 상세 페이지
    @GetMapping("{itemId}/review/{reviewId}")
    public String reviewPage(@PathVariable("itemId") Long itemId, @PathVariable("reviewId") Long reviewId, HttpSession session, Model model) {


        Review review = reviewService.findOne(reviewId);
        model.addAttribute("review", review);
        model.addAttribute("comments", commentService.findComments(review));

        // 댓글
        CommentForm commentForm = new CommentForm();
        commentForm.setItemId(itemId);
        model.addAttribute("form", commentForm);

        // 수정 화면
        model.addAttribute("updateForm", commentForm);

        // 댓글 주인 확인
        try {
            Member member = memberService.getLoginMember(session);
            model.addAttribute("memberId", member.getId());
            model.addAttribute("memberRole", member.getRole());
        } catch (Exception e){
            e.printStackTrace();
        }


        return "reviewPage";
    }


    // 댓글 등록
    @PostMapping("/{itemId}/review/{reviewId}/{parentId}")
    public String createComment(@PathVariable("itemId") Long id, @PathVariable("reviewId") Long reviewId, @PathVariable("parentId") Long parentId,
                                @ModelAttribute("form") CommentForm form, Model model, HttpSession session) {
        Long memberId = memberService.getLoginMemberId(session);

        Comment comment = new Comment();
        comment.setMemo(form.getMemo());

        LocalDateTime date = LocalDateTime.now();
        comment.setDate(date);
        comment.setSequence(date);
        comment.setMember(memberService.findOne(memberId));
        comment.setReview(reviewService.findOne(reviewId));
        comment.setParent(0L);
        comment.setNum(1L);
        System.out.println("parentId : " + parentId);

        if(parentId != 0) {
            comment.setParent(form.getParentId());
            comment.setSequence(commentService.findOne(form.getParentId()).getDate());
            comment.setNum(commentService.getSequenceCount(commentService.findOne(form.getParentId()).getDate()) + 1);
        }

        commentService.saveComment(comment);

        return "redirect:/{itemId}/review/{reviewId}";
    }

    // 댓글 수정
    @PostMapping("/update/comment/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId, @ModelAttribute("form") CommentForm form) {
        Comment comment = commentService.findOne(commentId);
        comment.setMemo(form.getMemo() + " [수정 : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm")) + "]");

        commentService.saveComment(comment);

        return "redirect:/back";
    }

    // 댓글 삭제
    @GetMapping("/delete/comment/{commentId}")
    public String deleteComment(@PathVariable("commentId") Long commentId) {
        Review review = commentService.deleteComment(commentId);

        return "redirect:/back";
    }

}
