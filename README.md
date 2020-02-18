# Spring Boot & JPA를 활용한 쇼핑몰[![Build Status](https://travis-ci.org/HashCitrine/jpashop.svg?branch=master)](https://travis-ci.org/HashCitrine/jpashop)

### 1. 프로젝트 소개

---

![메인 화면](https://github.com/HashCitrine/jpashop/blob/master/upload/02.PNG?raw=true)

<http://www.springboot-jpashop.page/>

Spring Boot 및 JPA 학습 성과를  확인하기 위한 개인 프로젝트

---

### 1.1 이용 기술

---

- 사용 언어 : JAVA

- 프레임워크 : Spring Boot

- 템플릿 엔진 : Thymeleaf

- 주요 라이브러리 : JPA(JPQL 활용), Lombok

- DB

  - 개발환경 : H2
  - 서버환경 : MariaDB (AWS RDS)

  

- 배포 : AMAZON LINUX - CentOS (AWS EC2)

  - Travis CI + Nginx 적용 (AWS CodeDeploy & S3 버킷 등을 활용하여 Git push시 무중단 자동 배포) 

---

### 1.2 참고 강의

---

- [실전! 스프링 부트와 JPA 활용1]([https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8-JPA-%ED%99%9C%EC%9A%A9-1#](https://www.inflearn.com/course/스프링부트-JPA-활용-1#))

- 해당 강의 내용을 밑바탕으로 삼아 프로젝트 시작

---

### 1.3 밑바탕이 된 부분

---

- 기본적인 Entity 및 DB 구조 (Member, Item, Order, OrderItem, Delivery)

- 저장 및 조회를 위한 JPQL (**Repository의 save, findOne, findById 등)
- Controller에서 사용하기 위한 Service 코드 (save, find  ... 등)

---

### 1.3 직접 개발한 부분

---

- 전체 템플릿 변경 (부트스트랩 무료 템플릿 활용)
- 상품과 함께 보여줄 이미지 업로드/불러오기 구현



- 상품, 주문 등을 '**정렬**'하기 위한 Repositoy 메소드 추가
- 다양한 엔티티 타입의 ResultSet을 뷰에서 **페이지 목록**으로 보기 위한 코드 개발 (PagingService)
- Review, Comment 엔티티 추가 및 '**후기**' 게시판 & '**댓글**' 구현
- OrderItem 엔티티를 Cart 엔티티로 변경 후 Member 테이블과 연결하여 '**장바구니**' 기능 구현
- Enum 클래스 'DeliveryStatus' 변경 (READY, DELIVERY, COMPLETE)



- 회원가입 후 로그인을 위해 **이메일 인증** 추가
- 잃어버린 비밀번호를 찾을 수 있는 이메일 전송 기능 추가
- **회원 권한** 구현을 위해 Enum 클래스 'Role' 추가 (NORMAL, ADMIN)
- 템플릿 엔진(thymeleaf)을 이용하여 뷰에서도 일반 회원, 관리자 권한 구현
  - **일반 회원** : 후기/댓글 수정, 삭제 및 관리 페이지에서 본인의 주문과 작성한 글 목록 열람 가능
  - **관리자** : 전체 상품/주문/회원 목록 열람 가능, 주문 상태 및 회원 정보 수정 가능

---

### 2. 살펴보기

---

### 2.1 전체 DB 구조

---

![db](https://github.com/HashCitrine/jpashop/blob/master/upload/01.png?raw=true)

- **Member** : 회원

  사용자의 주문 관리 및 사이트 활동에 대한 기록을 유지하고 권한에 따라 이용가능한 기능에 차이를 두기 위해 생성

- **Item** : 상품

  쇼핑몰에서 판매할 상품으로 카테고리를 이용해 분류하거나 정렬하기 위해 생성
  주문 과정에서 참조하여 주문가능한 수량을 결정

- **Order** : 주문

  사용자가 신청한 주문을 통합적으로 관리하기 위해 생성

- **Cart** : 장바구니

  한 번의 주문에 여러 상품을 포함하여 진행할 수 있도록 하기 위해 생성

  상품을 참조하고 주문 수량 및 전체 가격을 결정

- **Delivery** : 배송정보

  주문서 작성시 입력받은 주소와 주문 테이블을 1:1 관계로 참조

  관리자가 상태(status)를 변경하여 주문자가 확인할 수 있음

- **Review** : 후기

  상품에 관련된 후기를 남길 수 있는 게시판

- **Comment** : 댓글

  게시판에 답변을 등록할 수 있음

  댓글에 대한 답변(이하 답글)이 가능함

  답글은 대상 댓글의 바로 아래에 위치하며 이에 적합하게 정렬됨



- Address : 주소 정보
  - postcode(int) : 우편번호
  - address(String) : 상세 주소지
- Enum 클래스
  - OrderStatus : ORDER, CANCEL
  - DeliveryStatus : READY, DELVERY, COMPLETE
  - Role : NORMAL, ADMIN

---

### 2.2 이미지 업로드

---

- 상품 등록/수정 폼에 'enctype = multipart/form-data' 추가

```html
<div class="card-header">상품 등록</div>
	<div class="card-body">
        <form th:action="@{/new}" th:object="${form}" method="post" enctype = multipart/form-data>
```



- uploadFile 메소드 : ItemService에  추가하여 사용

```java
public String uploadFile(MultipartFile file) throws IOException {
        if(file.getSize() == 0) {
            return null;
        }

        // 확장자를 제외한 파일이름
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        System.out.println(fileName + ", " + index);

        // 파일 확장자
        String fileExtension = fileName.substring(index + 1);

        // 파일 이름 랜덤 설정 후 확장자 부여
        String savedName = UUID.randomUUID().toString() + "." + fileExtension;

        // 지정한 경로에 복사하여 파일 저장
        File target = new File(imgConfig.getUploadPath(), savedName);
        FileCopyUtils.copy(file.getBytes(), target);

        // '/img/파일명'을 db에 저장
        String path = target.getPath();
        int pathIndex = path.lastIndexOf("/img");

        return path.substring(pathIndex);
    }
```

- ImgConfig.java : 외부 업로드 경로 지정

```java
@Configuration
@PropertySource("classpath:application.yml")
@Getter
public class ImgConfig implements WebMvcConfigurer {

    // yml에 저장된 이미지 외부 저장 경로
    @Value("${custom.uploadPath}")
    private String uploadPath;

    // '/img'로 시작하는 경로는 'file://{이미지 저장경로}/{파일명}'으로 접근
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file://" + uploadPath);
    }
}
```

- 메인화면과 상품 단독창에서 이미지 불러오기

![이미지](https://github.com/HashCitrine/jpashop/blob/master/upload/03.PNG?raw=true)

---

### 2.3 회원가입 후 이메일 인증

---

- 회원가입 시 인증 메일 전송

```java
@Controller
@RequiredArgsConstructor
public class MemberContoller {

    private final MemberService memberService;    

	@PostMapping("register")
    public String createUser(@Valid @ModelAttribute("form") MemberForm form, BindingResult result, Model model) {
        
       ...
           
        Member member = new Member();

        ...
            
        // 이메일 전송
        memberService.sendVerifiedEmail(form.getEmail(), code);

        // 회원가입
        memberService.join(member);

        return "email/sendEmail";
    }
}
```



- 이메일 전송 : verify/{Verify_Code}

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JavaMailSender javaMailSender;
    
    ...
    
	// 인증 메일
    public void sendVerifiedEmail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");

            // 인증 확인 페이지 주소
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
```

![이미지](https://github.com/HashCitrine/jpashop/blob/master/upload/04.png?raw=true)



- 메일의 주소로 접속 시, verifyEmail 메소드 실행
-  Member 테이블의 Verify필드는 True / Verify_Code 필드는 null로 변경

```java
@Controller
@RequiredArgsConstructor
public class MemberContoller {

    private final MemberService memberService;    
    
	@GetMapping("verify/{code}")
    public String verifyCodeCheck(@PathVariable("code") String code, Model model) {

        model.addAttribute("verify", memberService.verifyEmail(code));

        return "email/verify";
    }
```

```java
    // 이메일 인증 확인
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

```

![이미지](https://github.com/HashCitrine/jpashop/blob/master/upload/05.png?raw=true)



- 로그인 시, Verify = True 확인

```java
    // 로그인
    @PostMapping("login")
    public String login(@Valid @ModelAttribute("login") LoginForm form, BindingResult result, HttpSession session) {

        // 입력한 정보와 일치하는 회원 검색
        List<Member> member = memberService.login(form.getEmail(), memberService.getSha256(form.getPassword()));

        if (result.hasErrors()) {
            System.out.println("로그인 에러 발생");
            return "user/login";
        }
        
		...
    
        // 이메일 미인증시 로그인 에러 발생
        if (!memberService.veifyCheck(member)) {
            FieldError error = new FieldError("form", "password", "이메일 인증을 하셔야 합니다.");
            result.addError(error);
            return "user/login";
        }
        
        ...

        return "redirect:/";
    }
```



---

### 2.4 답글(대댓글)

---

- 댓글 화면에서 답글달기 버튼을 통해 등록 가능

![댓글](https://github.com/HashCitrine/jpashop/blob/master/upload/06.png?raw=true)



- Sequnce 필드에 부모 댓글의 Date값을 저장
- num : 대댓글 정렬을 위한 순서 필드

```java
    // 댓글 등록
    @PostMapping("/{itemId}/review/{reviewId}/{parentId}")
    public String createComment(@PathVariable("itemId") Long id, @PathVariable("reviewId") Long reviewId, @PathVariable("parentId") Long parentId, @ModelAttribute("form") CommentForm form, Model model, HttpSession session) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        Long memberId = memberService.getLoginMemberId(session);

        // 댓글 등록
        Comment comment = new Comment();
        comment.setMember(memberService.findOne(memberId));
        comment.setReview(reviewService.findOne(reviewId));
        comment.setMemo(form.getMemo());

        // 댓글 등록시간(date)과 같은 값을 sequnce 필드에 저장
        LocalDateTime date = LocalDateTime.now();
        comment.setDate(date);
        comment.setSequence(date);

        comment.setParent(0L);
        comment.setNum(1L);
        System.out.println("parentId : " + parentId);

        // 답글일 경우 :
        if (parentId != 0) {
            comment.setParent(form.getParentId());
            comment.setSequence(commentService.findOne(form.getParentId()).getDate());
            comment.setNum(commentService.getSequenceCount(commentService.findOne(form.getParentId()).getDate()) + 1);
        }

        commentService.saveComment(comment);

        return "redirect:/{itemId}/review/{reviewId}";
    }
```



- 정렬 : sequnce ASC, num ASC

```java
    public List<Comment> findAll(Review review) {
        return em.createQuery("select c from Comment c where c.review = ?1 ORDER BY c.sequence ASC, c.num ASC", Comment.class)
                .setParameter(1, review)
                .getResultList();
    }
```



- Seunce 필드에 값이 있으면, 화면 출력 시 공백 추가

![답글](https://github.com/HashCitrine/jpashop/blob/master/upload/07.PNG?raw=true)



---

### 2.5 페이징

---

- PagingService
- getBoardPage - 인자 : getResultList로 반환된 List 타입의 데이터, 현재 페이지, 한 페이지에서 출력할 게시물 갯수, 화면에 출력할 페이지 갯수

```java
@Service
@Transactional
@RequiredArgsConstructor
public class PagingService {

    private int allCount;   // 객체의 총 갯수

    private int unit;       // 노출할 객채수 (10개씩 노출)
    private int viewPage;   // 노출될 총 페이지수 (5페이지씩 노출)


    public List<?> getBoardPage(List<?> object, int currentPage, int unit, int viewPage, Model model) {
        this.unit = unit;
        this.viewPage = viewPage;

        this.allCount = object.size();

        int start = (currentPage - 1) * unit;
        int end = allCount;

        // 나머지가 있으면 start가 end와 같거나 큼
        if ((currentPage * unit) < end) {
           end = start + unit;
        }

        if(viewPage != 0) {
            this.setStartEndPage(currentPage, model);
        }

        return object.subList(start, end);

    }
    
    private int start;
    private int end;
    private int previous;
    private int next;
    private int maxPage;


    public void setStartEndPage(int currentPage, Model model) {
        // 모든 페이지 중 마지막 페이지
        maxPage = this.allCount / this.unit;
        int plusPage = this.allCount % this.unit;

        // 나머지가 값이 0보다 크면(나머지가 있으면) 페이지 추가 / 없으면 그대로
        if (plusPage > 0) {
            maxPage++;
        }

        // 나타낼 페이징의 수 : 5개씩 (1~5) -> viewPage = 5;
        // 1~5 페이지(1레벨) : 현재 페이지 / 6
        int firstPage = (currentPage / (this.viewPage + 1)) + 1;

        // 6이상 페이지
        if(currentPage > this.viewPage){
            if(currentPage % this.viewPage != 0){   // 나머지 값이 있을 때 = 레벨 유지
                firstPage = ((currentPage / this.viewPage) * this.viewPage) + 1;
            } else {    // 나머지 값이 없을 때 = 다음 레벨 직전 페이지
                firstPage = ((currentPage / this.viewPage) - 1) * this.viewPage + 1;
            }
        }

         // 첫 페이지 + 4가 마지막 페이지
        int lastPage = firstPage + this.viewPage - 1;          

        start = firstPage;
        end = lastPage;
        previous = start - unit + 1;
        next = end + 1;

        if (previous < 0) {
            previous = 1;
        }

        // 노출될 마지막 페이지의 값보다 총 마지막 페이지의 값이 작을 때 (1~5까지 노출하려 했지만 총 페이지 값이 3일 때)
        if (lastPage > maxPage) {
            end = maxPage; // 2까지만 노출
            next = end;
        }

        if(end == maxPage ) {
            next = maxPage;
        }

        model.addAttribute("resetPage", 1);
        model.addAttribute("previous", this.getPrevious());
        model.addAttribute("start", this.getStart());
        model.addAttribute("end", this.getEnd());
        model.addAttribute("next", this.getNext());
        model.addAttribute("lastPage", this.getMaxPage());
    }
```



- 추가되는 model 파라미터
  - start : 현재 페이지 기준으로 노출할 첫 페이지 (2페이지 : 1 & **8페이지** : 6)
  - end : 현재 페이지 기준으로 노출할 마지막 페이지 (2페이지 : 5 & **8페이지** : 10)
  - previous : 현재 페이지 기준으로 이전 페이지 저장 (**이전**)
  - next : 현재 페이지 기준으로 마지막 페이지 저장 (**다음**)
  - resetPage: 1페이지 저장 (**<<**)
  - lastpage : 마지막 페이지 저장 (**>>**)
  
- 페이지 버튼 : thymeleaf 시퀸스 반복문을 통해 해당되는 url 주소 입력

- 메인 화면, 후기 목록, 관리 페이지 등에 적용

  

![페이징](https://github.com/HashCitrine/jpashop/blob/master/upload/08.png?raw=true)

---

### 2.6 장바구니 → 주문

---

- addCart : 장바구니 추가
- 같은 상품 추가 : count의 입력값을 더하여 반영
- 다른 상품 추가 : cart에 새로운 데이터 추가

```java
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
        
        // 로그인 요구
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        // 장바구니 추가
        Cart cart = new Cart();
        cartService.add((Member)session.getAttribute("member"), itemService.findOne(itemId), itemForm.getCount());

        // redirect:/shop/{itemId}
        return "others/addCart";
    }
```



- 장바구니

![장바구니](https://github.com/HashCitrine/jpashop/blob/master/upload/09.PNG?raw=true)



- 주문
- kakao 주소 API 추가(JavaScript)
- <http://postcode.map.daum.net/guide>

![주문](https://github.com/HashCitrine/jpashop/blob/master/upload/10.PNG?raw=true)

- 주문이 완료되면 buy = True(1)
- 장바구니 화면에서 제거 : --- 주소 미입력시 에러 반환 코드 추가 ----

```java
    // 상품 구매
    @PostMapping("/buy")
    public String orderItem(@ModelAttribute("form") OrderForm form, Model model, HttpSession session, BindingResult result) {
        if(memberService.getLoginMember(session) == null) {
            return "others/needLogin";
        }

        if(result.hasErrors()) {
            return "order/orderPage";
        }

        Address address = new Address(form.getMainAddress() + form.getExtraAddress(), form.getPostcode());

        Member member = memberService.getLoginMember(session);
        List<Cart> carts = cartService.findCart(member.getId());

        orderService.order(session, address, carts);

        return "order/orderSuccess";
    }
```



- 주문 전

![주문 전](https://github.com/HashCitrine/jpashop/blob/master/upload/11.png?raw=true)

- 주문 후

![주문 후](https://github.com/HashCitrine/jpashop/blob/master/upload/12.png?raw=true)

---

### 2.7 권한

---

- 회원 권한 : Role 

```java
public enum Role {
    ADMIN, NORMAL
}
```



- Httpsession에 저장된 member의 role을 확인하는 것으로 구현

- 댓글 수정/삭제

  - 본인 / 관리자 : 수정/삭제 버튼
  - 다른 일반 회원 : 버튼 없음

  ![관리자 댓글](https://github.com/HashCitrine/jpashop/blob/master/upload/14.PNG?raw=true)

- 관리 페이지

  - 일반 회원 : 주문한 상품, 후기, 댓글 목록

  ![일반 회원 목록](https://github.com/HashCitrine/jpashop/blob/master/upload/15.png?raw=true)

  - 관리자 : 전체 상품, 주문, 회원 목록

  ![관리자 목록](https://github.com/HashCitrine/jpashop/blob/master/upload/16.png?raw=true)

- 주문

  - 일반 회원 : 주문 취소, 재주문
  - 관리자 : 주문 상태 변경

  ![주문상태 변경](https://github.com/HashCitrine/jpashop/blob/master/upload/17.png?raw=true)

![주문 취소 불가](https://github.com/HashCitrine/jpashop/blob/master/upload/18.PNG?raw=true)

- 개인정보 수정

  - 일반 회원 : 비밀번호 변경
  - 관리자 : 회원 권한 변경
  
  ![개인정보 수정](https://github.com/HashCitrine/jpashop/blob/master/upload/19.png?raw=true)

---

### 3. 배포

---

- [스프링 부트와 AWS로 혼자 구현하는 웹 서비스](https://jojoldu.tistory.com/259)
- 비슷한 개발환경에서 배포과정을 상세히 서술하신 것을 발견하여 이와 같은 방식으로 배포를 진행

---

### 3.1 배포 환경

---

- AWS 서비스 활용
- EC2 : AMAZON LINUX (CentOS)
- RDS : MariaDB
- 도메인 : 구글 도메인

---

### 3.3 Travis CI

---

- 자동 배포 : git push 후 자동으로 배포를 진행하기 위해 설치

- AWS 설정

  - EC2 : 인스턴스 설정의 IAM ROLE을  'EC2CodeDeployRole'로 변경
  - Code Deploy
    - Travis용 계정을 생성 : Access key & Sercet Access Key 발급
    - 애플리케이션 생성 : IAM ROLE의 CodeDeployRole로 역할 설정

  - S3 : 버킷에서 build된 jar 파일 보관

  

- 인스턴스 내부
  - aws-cli & AWS CodeDeploy Agent 설치
  - 프로젝트 내부
    -  .travis.yml : travis CI 연결 설정
    - appspec.yml : AWS CodeDeploy 연결 설정

---

### 3.4 Nginx

---

- 무중단 배포 : 배포가 이루어질 때 사이트가 중단되는 문제점을 해결하기 위해 설치
- 80, 443 포트 할당(http, https) : 해당 포트로 전송된 파일만 웹 노출



- 무중단 원리
  - spring.profiles : port값이 8081과 8082로 나누어진 2가지 profiles을 이용 (yml 설정)

    ```
    nohup java -jar (파일 이름) -Dspring.profiles.active=(profiles 이름)
    ```

  - 새로운 배포가 진행되면 사이트를 중단하지 않고 배포 진행(travis - codedeploy)

    - 2개의 jar 파일이 다른 포트에서 동시에 실행 중

  - 새롭게 배포된 jar 파일의 실행 상태를 확인 (actuator 라이브러리 활용)

  - 정상 실행 확인 후, 80, 443포트(Nginx)로 전송할 profiles 변경

  - 사이트 중단 시간이 거의 없이 jar 파일 전환

  

- deploy.sh : git push 후 travis를 통해 자동 배포가 진행되면 Nginx를 이용한 profiles 변경 진행
- execute-deploy.sh를 프로젝트 내부에 작성 : deploy.sh 파일 실행
  - appspec.yml 설정을 통해 실행시킬 수 있는 파일
  - 배포가 끝나면 execute-deploy.sh 실행 → deploy.sh 실행

---

### 4.  프로젝트 소감

---

### 4.1 느낀 점

---

- 이론적인 지식과 프로젝트 경험의 차이
  - 구조적으로 완성된 or 간단한 코드 VS 실제로 자신이 생각하여 작성한 코드

  - 수많은 에러 코드 & 예상치 못한 결과들과 조우

    → 해결방법을 찾기 위해 발생한 문제의 ‘중점’을 파악하고자 노력
    → 적절한 검색 키워드를 선정하여 해결방법 탐색

    

-  다양한 라이브러리들의 유용성
  
  - 서비스 구현에 필요한 라이브러리들을 알게 됨(File, Mail 등)
  
  - 프로젝트 진행에 필수적인 라이브러리들 위주로 적용하여 진행
  
  
  
-  Front-End 및 협업의 필요성

  - 모든 부분을 개인 개발 : 디자인 요소 미흡(JAVA, Spring 위주 학습의 한계)

  - 웹 개발에 Front-End의 중요성과 영향력을 깨달음

  

- JavaScript의 영향력

  - 언어 하나로 Front-Back을 모두 구현할 수 있다는 강점을 실감

  - JavaScript를 이용함으로써 사이트 이용 경험이 크게 개선되는 것을 느낌

---

### 4.2 업데이트 하고 싶은 점

---

- 상품 카테고리 기능 구현

  \- 상품 카테고리별로 필드 차이는 있지만 현재 미사용 중 (등록폼에서 제외)

  \- 카테고리별 등록폼 입력란 생성 

  \- 카테고리별 최신순, 주문순, 후기순 정렬

  

- Spring Security 적용

  - Oauth 구현 (구글, 네이버 등)
  - 비밀번호 암호화 / 권한 검증 등을 해당 라이브러리를 통해 실시

  

- AJAX 적용
  - 댓글 등록 후 새로고침 없이 확인

---

### 5. 업데이트 내역

---

- 미흡한 점 보완 & 새로 알게 된 기능을 테스트 하기 위한 업데이트 예정



- To Be Continued...