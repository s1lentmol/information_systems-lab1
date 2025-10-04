package com.example.islab1.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleBadRequest(Exception ex, HttpServletRequest request) {
        ModelMap model = baseModel(request);
        model.addAttribute("title", "Некорректный запрос");
        model.addAttribute("message", "Проверьте введённые данные и попробуйте снова.");
        model.addAttribute("detail", ex.getMessage());
        return new ModelAndView("error/400", model);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
        ModelMap model = baseModel(request);
        model.addAttribute("title", "Запись не найдена");
        model.addAttribute("message", "Мы не нашли объект, к которому вы обратились.");
        model.addAttribute("detail", ex.getMessage());
        return new ModelAndView("error/404", model);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneric(Exception ex, HttpServletRequest request) {
        String errorId = UUID.randomUUID().toString();
        log.error("Unexpected error [{}] on {}", errorId, request.getRequestURI(), ex);

        ModelMap model = baseModel(request);
        model.addAttribute("title", "Внутренняя ошибка");
        model.addAttribute("message", "Что-то пошло не так. Мы уже работаем над этим.");
        model.addAttribute("errorId", errorId);
        model.addAttribute("detail", ex.getMessage());
        return new ModelAndView("error/500", model);
    }

    private ModelMap baseModel(HttpServletRequest request) {
        ModelMap model = new ModelMap();
        model.addAttribute("timestamp", OffsetDateTime.now());
        model.addAttribute("path", request.getRequestURI());
        return model;
    }
}
