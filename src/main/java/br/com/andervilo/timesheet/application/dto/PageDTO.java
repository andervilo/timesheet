package br.com.andervilo.timesheet.application.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record PageDTO<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int currentPage,
    int pageSize,
    boolean first,
    boolean last
) {
    public static <T> PageDTO<T> from(Page<T> page) {
        return new PageDTO<>(
            page.getContent(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.getNumber(),
            page.getSize(),
            page.isFirst(),
            page.isLast()
        );
    }
} 