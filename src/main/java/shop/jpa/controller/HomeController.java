package shop.jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import shop.jpa.domain.Order;
import shop.jpa.repository.OrderSearch;
import shop.jpa.service.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final ItemService itemService;
    private final PagingService pagingService;

    private final int maxItem = 6;
    private final int viewPage = 5;


    // item page
    @GetMapping(value = "/")
    public String home(HttpSession session, Model model) {
        return "redirect:/home/1";
    }

    @GetMapping(value = "/home/{page}")
    public String homePage(@PathVariable("page") int page, HttpSession session, Model model) {
        List<?> items = pagingService.getBoardPage(itemService.findItems(), page, this.maxItem, this.viewPage, model);

        model.addAttribute("items", items);
        model.addAttribute("sort", "home");
        return "index";
    }

    @GetMapping(value = "/review/{page}")
    public String orderByReview(@PathVariable("page") int page, HttpSession session, Model model) {
        List<?> items = pagingService.getBoardPage(itemService.orderByReview(), page, this.maxItem, this.viewPage, model);

        model.addAttribute("items", items);
        model.addAttribute("sort", "review");
        return "index";
    }

    @GetMapping(value = "/sale/{page}")
    public String orderBySale(@PathVariable("page") int page, HttpSession session, Model model) {
        List<?> items = pagingService.getBoardPage(itemService.orderBySaleCount(), page, this.maxItem, this.viewPage, model);

        model.addAttribute("items", items);
        model.addAttribute("sort", "sale");

        return "index";
    }


    @GetMapping("shop")
    public String shopping() {
        return "redirect:/home/1";
    }

    // '반영 되었습니다.' 메시지 후 이전 페이지로
    @GetMapping("back")
    public String back(){
        return "others/back";
    }

}
