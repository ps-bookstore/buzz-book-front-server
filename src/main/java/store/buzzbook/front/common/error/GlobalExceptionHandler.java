package store.buzzbook.front.common.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        String formattedErrorMessage = errorMessage(e);
        model.addAttribute("page", "error");
        model.addAttribute("error", formattedErrorMessage);
        log.error(formattedErrorMessage);
        return "index";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
        String formattedErrorMessage = errorMessage(e);
        model.addAttribute("page", "error");
        model.addAttribute("error", "The requested resource was not found.");
        log.error(formattedErrorMessage);
        return "index";
    }

    private String errorMessage(Exception e) {
        return String.format("\n프론트 서버 에러 \n▶️ %s \n▶️ %s", e.getMessage(), e);
    }
}
