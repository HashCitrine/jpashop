package shop.jpa.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class ResetForm {

    @NotEmpty(message="비밀번호를 입력해주세요")
    private String password;

    @NotEmpty(message = "비밀번호를 다시 입력해주세요")
    private String confirmPassword;

    private String code;
}
