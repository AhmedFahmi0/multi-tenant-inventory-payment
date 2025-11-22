package com.inventory.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.beans.PropertyEditorSupport;
import java.util.List;

@ControllerAdvice
public class PaginationControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Pageable.class, new PageablePropertyEditor());
    }

    private static class PageablePropertyEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            // This is just to prevent binding issues with Pageable
            setValue(PageRequest.of(0, 10));
        }
    }
}
