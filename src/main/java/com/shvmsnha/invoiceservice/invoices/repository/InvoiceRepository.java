package com.shvmsnha.invoiceservice.invoices.repository;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.shvmsnha.invoiceservice.invoices.dto.InvoiceFileDto;
import com.shvmsnha.invoiceservice.invoices.models.Invoice;
import com.shvmsnha.invoiceservice.invoices.models.InvoiceProduct;

import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

@XRayEnabled
@Repository
public class InvoiceRepository {

    private static final String PARTITION_KEY = "#invoice_";
    private final DynamoDbAsyncTable<Invoice> invoiceTable;
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Autowired
    public InvoiceRepository(
        @Value("${invoices.ddb.name}") String invoicesDdbName,
        DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient
    ) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.invoiceTable = 
            this.dynamoDbEnhancedAsyncClient.table(invoicesDdbName, 
                TableSchema.fromBean(Invoice.class));
    }

    public CompletableFuture<Void> createInvoice(
        InvoiceFileDto invoiceFileDto, String invoiceTransactionId, String invoiceFileTransactionId
    ) {
        long timestamp = Instant.now().toEpochMilli();

        Invoice invoice = new Invoice();
        invoice.setPk(PARTITION_KEY.concat(invoiceFileDto.customerEmail()));
        invoice.setSk(invoiceFileDto.invoiceNumber());
        invoice.setCreatedAt(timestamp);
        invoice.setTtl(0L);
        invoice.setTotalValue(invoiceFileDto.totalValue());
        invoice.setProducts(invoiceFileDto.products().stream().map(invoiceProductFileDto -> {
            InvoiceProduct invoiceProduct = new InvoiceProduct();
            invoiceProduct.setId(invoiceProductFileDto.id());
            invoiceProduct.setQuantity(invoiceProductFileDto.quantity());
            return invoiceProduct;
        }).toList());
        invoice.setFileTransactionId(invoiceFileTransactionId);
        invoice.setInvoiceTransactionId(invoiceTransactionId);

        return invoiceTable.putItem(invoice);
    }

    public SdkPublisher<Page<Invoice>> findByCustomerEmail(String customerEmail) {
        String pk = PARTITION_KEY.concat(customerEmail);
        return invoiceTable.query(QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(pk)
                    .build()))
            .build());
    }

}
