package shop.jpa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.jpa.domain.Member;
import shop.jpa.domain.Role;
import shop.jpa.repository.MemberRepository;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.util.List;

@Service
@Transactional(readOnly = true)
// @AllArgsConstructor  : 모든 필드를 이용해서 생성자 생성
@RequiredArgsConstructor // : final 필드만 가지고 생성자 생
public class MemberService {

    private final MemberRepository memberRepository;

    private int start;
    private int end;

    private int maxPage;
/*
    @Autowired  // set대신 생성자를 이용해서 memberRepository 수정 (Autowired 생략 가능)
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
*/

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    @Transactional
    public void update(Member member, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("member");

        if (member.getId() == loginMember.getId() || member.getRole() == Role.ADMIN) {
            memberRepository.save(member);
        }
    }

    @Transactional
    public Boolean verifyEmail(String code) {
        try {
            Member member = memberRepository.findByCode(code);
            member.setVerifyCode(null);
            member.setVerify(true);
            memberRepository.save(member);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 이메일
    @Transactional
    public Boolean checkForgotPassword(String code) {
        try {
            Member member = memberRepository.findByCode(code);
            if (member.getVerify() == true) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @Transactional
    public void resetPassword(String code, String password) {
        try {
            Member member = memberRepository.findByCode(code);
            member.setVerifyCode(null);
            member.setPassword(password);
            memberRepository.save(member);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getEmail());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    // 중복 이메일 검색
    public List<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    // 로그인시 가입되지 않은 회원일 때
    public Boolean loginCheck(List<Member> member) {
        return member.isEmpty();
    }

    // 로그인시 이메일 인증하지 않은 회원일 때
    public boolean veifyCheck(List<Member> member) {
        return member.get(0).getVerify();
    }

    // 로그인
    public List<Member> login(String email, String password) {
        return memberRepository.findByEmailPassword(email, password);
    }


    // 페이징
    public List<Member> getBoardPage(int firstPage, int maxPage) {
        this.maxPage = maxPage;
        return memberRepository.getBoardList(firstPage, maxPage);
    }


    public void setStartEndPage(int currentPage) {
        int maxPage = (memberRepository.getRowCount() / this.maxPage) + 1;

        int unit = 5;                               // 나타낼 페이징의 수 : 5개씩 (1~%)
        int firstPage = currentPage / unit + 1;   // ㅎ
        int lastPage = firstPage + unit - 1;         // 첫 페이지 + 4가 마지막 페이지


        if (firstPage < 1) {
            firstPage = 1;
        }

        this.start = firstPage;

        if (lastPage > maxPage) {
            this.end = maxPage;
        } else {
            this.end = lastPage;
        }
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public long getAllMemberCount() {
        return memberRepository.getRowCount();
    }


    public Member getLoginMember(HttpSession session) {
        try {
            return (Member) session.getAttribute("member");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getLoginMemberId(HttpSession session) {
        Member member = getLoginMember(session);
        return member.getId();
    }

    public Role getRole(HttpSession session) {
        Member member = getLoginMember(session);
        return member.getRole();
    }


    // email 보내기
    private final JavaMailSender javaMailSender;

    // 인증 메일
    public void sendVerifiedEmail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            String url = "https://springboot-jpashop.page/verify/" + code;
            String html = "<a href=" + url + ">이메일 인증하기</a>";

            mimeMessageHelper.setFrom("hashcitrine@springboot-jpashop.page");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("SB Shop 회원가입 인증 메일입니다.");
            mimeMessageHelper.setText(html, true);


            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 비밀번호 변경 메일
    @Transactional
    public void sendPasswordEmail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            String url = "https://springboot-jpashop.page/reset/" + code;
            String html = "<a href=" + url + ">비밀번호 변경 하기</a>";

            mimeMessageHelper.setFrom("using1524@naver.com");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("SB Shop 비밀번호 변경 메일입니다.");
            mimeMessageHelper.setText(html, true);


            javaMailSender.send(message);

            // 인증 코드 DB Update
            Member member = memberRepository.findByEmail(email).get(0);
            System.out.println("email : " + member.getEmail() + ", code : " + code);
            member.setVerifyCode(code);
            memberRepository.save(member);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSha256(String password) {
        String shaPassword = null;
        try{
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            sha.update(password.getBytes());

            byte byteData[] = sha.digest();
            StringBuffer stringBuffer = new StringBuffer();


            for(int i = 0; i < byteData.length; i++) {
                stringBuffer.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
            }

            shaPassword = stringBuffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return shaPassword;
    }

    public Boolean notAdmin(HttpSession session) {
        if(getLoginMember(session).getRole() == Role.ADMIN) {
            return false;
        }
        return true;
    }
}
