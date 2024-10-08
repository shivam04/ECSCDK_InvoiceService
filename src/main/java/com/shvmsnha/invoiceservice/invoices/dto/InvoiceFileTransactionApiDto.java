package com.shvmsnha.invoiceservice.invoices.dto;

public record InvoiceFileTransactionApiDto(
    String transactionId,
    String status
) {
}
