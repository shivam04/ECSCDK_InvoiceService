package com.shvmsnha.invoiceservice.invoices.dto;

import java.util.List;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InvoiceFileDto(
    String customerEmail,
    String invoiceNumber,
    Float totalValue,
    List<InvoiceProductFileDto> products
) {
}
