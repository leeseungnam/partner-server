package kr.wrightbrothers.framework.lang;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class WBValidateException extends RuntimeException {

    private final Errors errors;

    public WBValidateException(Errors errors) {
        this.errors = errors;
    }

}
