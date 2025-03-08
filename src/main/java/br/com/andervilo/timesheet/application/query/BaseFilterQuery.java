package br.com.andervilo.timesheet.application.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class BaseFilterQuery {
    private int page = 0;
    private int size = 10;
    private String sortBy;
    private Sort.Direction direction = Sort.Direction.ASC;

    public Pageable toPageable() {
        if (sortBy != null && !sortBy.isEmpty()) {
            return PageRequest.of(page, size, direction, sortBy);
        }
        return PageRequest.of(page, size);
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public Sort.Direction getDirection() {
        return direction;
    }
} 