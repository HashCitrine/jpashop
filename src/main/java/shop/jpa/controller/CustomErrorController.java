package shop.jpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class CustomErrorController implements ErrorController {
    private static final String errorPath = "/error";

    @Override
    public String getErrorPath() {
        return errorPath;
    }

    @RequestMapping("error")
    public String errorPage(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus= HttpStatus.valueOf(Integer.valueOf(status.toString()));
        log.info("httpStatus : " + httpStatus);

        model.addAttribute("code", status.toString());

        /* msg : 에러 이유
        model.addAttribute("msg", httpStatus.getReasonPhrase());
        model.addAttribute("timestamp", LocalDateTime.now());       */
        return "others/error";
    }


}
