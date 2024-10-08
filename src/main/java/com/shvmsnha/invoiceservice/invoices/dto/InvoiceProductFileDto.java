package com.shvmsnha.invoiceservice.invoices.dto;

import com.amazonaws.lambda.thirdparty.com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InvoiceProductFileDto(
    String id, int quantity
) {

}
