package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class LoginForm {

    @NotEmpty(message="이메일을 입력하세요")
    private String email;

    @NotEmpty(message = "비밀번호를 입력하세요")
    private String password;
}
