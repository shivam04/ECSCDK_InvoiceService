package com.shvmsnha.invoiceservice.invoices.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.shvmsnha.invoiceservice.invoices.dto.InvoiceApiDto;
import com.shvmsnha.invoiceservice.invoices.dto.InvoiceFileTransactionApiDto;
import com.shvmsnha.invoiceservice.invoices.dto.InvoiceProductApiDto;
import com.shvmsnha.invoiceservice.invoices.dto.UrlResponseDto;
import com.shvmsnha.invoiceservice.invoices.models.InvoiceFileTransaction;
import com.shvmsnha.invoiceservice.invoices.repository.InvoiceRepository;
import com.shvmsnha.invoiceservice.invoices.repository.InvoicesFileTransactionsRepository;
import com.shvmsnha.invoiceservice.invoices.services.S3InvoiceService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@XRayEnabled
@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {

    private static final Logger LOG = LogManager.getLogger(InvoicesController.class);
    private final S3InvoiceService s3InvoiceService;
    private final InvoicesFileTransactionsRepository invoicesFileTransactionsRepository;
    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoicesController(
        S3InvoiceService s3InvoiceService,
        InvoicesFileTransactionsRepository invoicesFileTransactionsRepository, 
        InvoiceRepository invoiceRepository
    ) {
        this.s3InvoiceService = s3InvoiceService;
        this.invoicesFileTransactionsRepository = invoicesFileTransactionsRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @PostMapping
    public ResponseEntity<UrlResponseDto> generatePreSignedUrl(
        @RequestHeader("requestId") String requestId
    ) {
        String key = UUID.randomUUID().toString();
        int expiresIn = 300;
        ThreadContext.put("invoiceFileTransactionId", key);
        
        String preSignedUrl = s3InvoiceService.generatePreSignedUrl(key, expiresIn);
        invoicesFileTransactionsRepository.createInvoiceFileTransaction(key, requestId, expiresIn).join();

        LOG.info("Invoice file transaction generated.....");
        return new ResponseEntity<>(new UrlResponseDto(preSignedUrl, expiresIn, key), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceApiDto>> getAllInvoicesByemail(@RequestParam() String email) {
        LOG.info("Get all invoices by email: [{}]", email);
        List<InvoiceApiDto> invoiceApiDtos = new ArrayList<>();

        invoiceRepository.findByCustomerEmail(email).subscribe(invoicePage -> {
            invoiceApiDtos.addAll(
                invoicePage.items().parallelStream()
                    .map(invoice -> new InvoiceApiDto(
                            invoice.getPk().split("_")[1],
                            invoice.getSk(),
                            invoice.getTotalValue(),
                            invoice.getProducts().parallelStream()
                                .map(product -> new InvoiceProductApiDto(
                                        product.getId(), 
                                        product.getQuantity()
                                    )
                                )
                                .toList(),
                            invoice.getInvoiceTransactionId(),
                            invoice.getFileTransactionId(),
                            invoice.getCreatedAt()
                        )
                    )
                    .toList()
            );
        }).join();

        return new ResponseEntity<>(invoiceApiDtos, HttpStatus.OK);
    }
    
    @GetMapping("/transactions/{fileTransactionId}")
    public ResponseEntity<?> getInvoiceFileTransaction(@PathVariable("fileTransactionId") String fileTransactionId) {
        LOG.info("Get invoice file transaction by tis id: [{}]", fileTransactionId);
        InvoiceFileTransaction invoiceFileTransaction = invoicesFileTransactionsRepository
            .getInvoiceFileTransaction(fileTransactionId).join();
        if (invoiceFileTransaction != null) { 
            return new ResponseEntity<>(new InvoiceFileTransactionApiDto(
                fileTransactionId, 
                invoiceFileTransaction.getFileTransactionStatus().name()
            ), HttpStatus.OK);
        }
        return new ResponseEntity<>("Invoice file transaction not found", HttpStatus.NOT_FOUND);
    }

}
