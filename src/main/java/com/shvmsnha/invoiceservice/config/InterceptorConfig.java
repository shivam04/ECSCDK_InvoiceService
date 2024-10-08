package com.shvmsnha.invoiceservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.shvmsnha.invoiceservice.invoices.interceptors.InvoicesInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final InvoicesInterceptor interceptor;

    @Autowired
    public InterceptorConfig(InvoicesInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.interceptor).addPathPatterns("/api/invoices/**");
    }
}
