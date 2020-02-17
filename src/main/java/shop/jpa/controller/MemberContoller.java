package shop.jpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import shop.jpa.domain.Member;
import shop.jpa.domain.Role;
import shop.jpa.form.LoginForm;
import shop.jpa.form.MemberForm;
import shop.jpa.form.ResetForm;
import shop.jpa.repository.MemberRepository;
import shop.jpa.service.MemberService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MemberContoller {

    private final MemberService memberService;

    // member
    @GetMapping("register")
    public String register(Model model) {
        model.addAttribute("form", new MemberForm());
        return "user/register";
    }

    @PostMapping("register")
    public String createUser(@Valid @ModelAttribute("form") MemberForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println("회원가입 에러 발생");
            return "user/register";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            FieldError error = new FieldError("form", "confirmPassword", "비밀번호가 일치하지 않습니다.");
            result.addError(error);
            return "user/register";
        }

        if (!memberService.findByEmail(form.getEmail()).isEmpty()) {
            FieldError error = new FieldError("form", "email", "이미 가입한 회원입니다.");
            result.addError(error);
            return "user/register";
        }

        Member member = new Member();

        member.setName(form.getName());
        member.setEmail(form.getEmail());

        member.setPassword(memberService.getSha256(form.getPassword()));
        member.setDate(LocalDateTime.now());

        member.setRole(Role.NORMAL);
        if(form.getName().equals("관리자")) {
            member.setRole(Role.ADMIN);
        }

        String code = UUID.randomUUID().toString();
        member.setVerifyCode(code);

        // 이메일 전송
        memberService.sendVerifiedEmail(form.getEmail(), code);

        // 회원가입
        memberService.join(member);

        return "email/sendEmail";
    }

    @GetMapping("verify/{code}")
    public String verifyCodeCheck(@PathVariable("code") String code, Model model) {

        model.addAttribute("verify", memberService.verifyEmail(code));

        return "email/verify";
    }

    private final MemberRepository memberRepository;

    @GetMapping("login")
    public String login(Model model, HttpSession session) {
        if (session.getAttribute("member") != null) {
            return "redirect:/";
        }
        model.addAttribute("login", new LoginForm());

        return "user/login";
    }

    // 로그인
    @PostMapping("login")
    public String login(@Valid @ModelAttribute("login") LoginForm form, BindingResult result, HttpSession session) {

        List<Member> member = memberService.login(form.getEmail(), memberService.getSha256(form.getPassword()));

        if (result.hasErrors()) {
            System.out.println("로그인 에러 발생");
            return "user/login";
        }

        if (memberService.findByEmail(form.getEmail()).isEmpty()) {
            FieldError error = new FieldError("form", "password", "가입되지 않은 회원입니다.");
            result.addError(error);
            return "user/login";
        }

        if (memberService.loginCheck(member)) {
            FieldError error = new FieldError("form", "password", "아이디 혹은 비밀번호가 일치하지 않습니다.");
            result.addError(error);
            return "user/login";
        }

        if (!memberService.veifyCheck(member)) {
            FieldError error = new FieldError("form", "password", "이메일 인증을 하셔야 합니다.");
            result.addError(error);
            return "user/login";
        }

        // 세션에 member 객체 유지 (20분)
        Member loginMember = member.get(0);
        session.setAttribute("member", loginMember);
        session.setAttribute("login", true);
        session.setMaxInactiveInterval(30 * 60);

        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();
        return "redirect:/";
    }


    // 인증 메일 재전송폼
    @GetMapping("resendEmail")
    public String resendForm(Model model) {
        model.addAttribute("form", new LoginForm());
        return "email/resendEmail";
    }

    // 인증 메일 재전송
    @PostMapping("resendVerify")
    public String resendEmail(@ModelAttribute("form") LoginForm form, BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println("인증메일 에러 발생");
            return "email/resendEmail";
        }

        if (memberService.findByEmail(form.getEmail()).isEmpty()) {
            FieldError error = new FieldError("form", "email", "가입되지 않은 회원입니다.");
            result.addError(error);
            return "email/resendEmail";
        }

        if (memberService.findByEmail(form.getEmail()).get(0).getVerify()) {
            FieldError error = new FieldError("form", "email", "인증된 회원입니다.");
            result.addError(error);
            return "email/resendEmail";
        }

        Member member = memberService.findByEmail(form.getEmail()).get(0);

        String code = UUID.randomUUID().toString();
        member.setVerifyCode(code);

        // 이메일 전송
        memberService.sendVerifiedEmail(form.getEmail(), code);
        return "email/sendEmail";
    }

    // 비밀번호 찾기폼
    @GetMapping("forgotPassword")
    public String forgotPasswordForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "user/forgotPassword";
    }

    // 비밀번호 변경 메일 전송
    @PostMapping("forgotPassword")
    public String forgotPassword(@Valid @ModelAttribute("loginForm") LoginForm loginForm, BindingResult result, Model model) {

        if (result.hasErrors()) {
            System.out.println("비밀번호 변경 메일 에러 발생");
            return "user/forgotPassword";
        }

        if (memberService.findByEmail(loginForm.getEmail()).isEmpty()) {
            FieldError error = new FieldError("form", "email", "가입되지 않은 회원입니다.");
            result.addError(error);
            return "user/forgotPassword";
        }


        String code = UUID.randomUUID().toString();

        // 이메일 전송
        memberService.sendPasswordEmail(loginForm.getEmail(), code);
        return "email/sendPasswordEmail";
    }

    // 비밀번호 변경폼
    @GetMapping("reset/{code}")
    public String resetPasswordForm(@PathVariable("code") String code, Model model) {


        if(memberService.checkForgotPassword(code)){
            ResetForm resetForm = new ResetForm();
            resetForm.setCode(code);
            model.addAttribute("resetForm", resetForm);
            return "user/resetPassword";
        }
        return "others/error";
    }

    // 비밀번호 변경
    @PostMapping("reset/{code}")
    public String resetPass(@Valid @ModelAttribute("resetForm") ResetForm resetForm, @PathVariable("code") String code, BindingResult result){

        if (result.hasErrors()) {
            System.out.println("비밀번호 변경 에러 발생");
            return "user/resetPassword";
        }

        if (!resetForm.getPassword().equals(resetForm.getConfirmPassword())) {
            System.out.println("if 통과");
            FieldError error = new FieldError("resetForm", "confirmPassword", "비밀번호가 일치하지 않습니다.");
            result.addError(error);
            return "user/resetPassword";
        }

        memberService.resetPassword(code, memberService.getSha256(resetForm.getPassword()));

        return "user/updatePassword";
    }


    // 개인정보 수정폼 - 로그인한 회원용
    @GetMapping("/privacy")
    public String updatePrivacyForm(Model model, HttpSession session) {

        try {
            Member member = memberService.getLoginMember(session);
            MemberForm form = new MemberForm();

            form.setName(member.getName());
            form.setEmail(member.getEmail());
            form.setRole(member.getRole());
            form.setMemberId(member.getId());


            model.addAttribute("form", form);
            model.addAttribute("statusGroup", Role.values());
            return "user/updatePrivacy";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    // 개인정보 수정폼 - 관리자용
    @PostMapping("/members/update/{memberId}")
    public String updatePrivacyForm(@PathVariable("memberId") Long memberId, Model model, HttpSession session) {

        try {
            Member member = memberService.findOne(memberId);
            MemberForm form = new MemberForm();

            form.setName(member.getName());
            form.setEmail(member.getEmail());
            form.setRole(member.getRole());
            form.setMemberId(member.getId());


            model.addAttribute("form", form);
            model.addAttribute("statusGroup", Role.values());
            return "user/updatePrivacy";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/";
    }

    // 개인정보 수정
    @PostMapping("/privacy/update/{memberId}")
    public String updatePrivacy(@PathVariable("memberId") Long memberId, @ModelAttribute("form") MemberForm form, BindingResult result, HttpSession session) {

        if (result.hasErrors()) {
            System.out.println("개인 정보 수정 에러 발생");
            return "user/updatePrivacy";
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            FieldError error = new FieldError("form", "confirmPassword", "비밀번호가 일치하지 않습니다.");
            result.addError(error);
            return "user/updatePrivacy";
        }

        Role sessionRole = memberService.getRole(session);
        if (form.getPassword().isEmpty() && sessionRole != Role.ADMIN) {
            FieldError error = new FieldError("form", "password", "새로운 비밀번호를 입력해야 합니다.");
            result.addError(error);
            return "user/updatePrivacy";
        }

        Member member = memberService.findOne(memberId);
        member.setName(form.getName());


        if (sessionRole == Role.ADMIN) {
            member.setRole(form.getRole());

        } if (!form.getPassword().equals("")) {
            member.setPassword(memberService.getSha256(form.getPassword()));
        }

        memberService.update(member, session);
        return "redirect:/admin/";
    }


}
