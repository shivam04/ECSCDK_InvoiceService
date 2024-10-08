package com.shvmsnha.invoiceservice.invoices.dto;

public record UrlResponseDto(
    String url,
    int expireIn,
    String transactionId
) {
}
