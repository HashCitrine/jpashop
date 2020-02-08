package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;
import shop.jpa.domain.Role;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message="이름을 입력해주세요")
    private String name;

    @NotEmpty(message="이메일을 입력해주세요")
    private String email;

    @NotEmpty(message="비밀번호를 입력해주세요")
    private String password;

    @NotEmpty(message = "비밀번호를 다시 입력해주세요")
    private String confirmPassword;

    private Role role;
    private Long memberId;
}
