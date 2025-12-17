package com.portfolio.aips.project.url_service.protect_url.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordCreateRequest extends BaseCreateProtectURLRequest{


    @NotBlank(message = "패스워드를 입력해주세요.")
    @Size(min = 4, max = 32, message = "패스워드는 최소 4자리 ~ 최대 32자까지 가능합니다.")
    private String password;
    private String confirmPassword;


}
