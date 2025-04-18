package com.example.security.security.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

public class VirtualFilterChain implements FilterChain {

    private final List<Filter> filters;
    private final FilterChain originalChain;
    private int currentPosition = 0;

    public VirtualFilterChain(List<Filter> filters, FilterChain originalChain) {
        this.filters = filters;
        this.originalChain = originalChain;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (currentPosition < filters.size()) {
            Filter nextFilter = filters.get(currentPosition++);
            nextFilter.doFilter(request, response, this);
        } else {
            originalChain.doFilter(request, response);
        }
    }
}