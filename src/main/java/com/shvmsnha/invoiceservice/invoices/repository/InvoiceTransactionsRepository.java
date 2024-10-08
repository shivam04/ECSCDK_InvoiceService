package com.shvmsnha.invoiceservice.invoices.repository;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.shvmsnha.invoiceservice.invoices.enums.InvoiceTransactionStatus;
import com.shvmsnha.invoiceservice.invoices.models.InvoiceTransaction;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@XRayEnabled
@Repository
public class InvoiceTransactionsRepository {

    private static final String PARTITION_KEY = "#invoiceTransaction_";
    private final DynamoDbAsyncTable<InvoiceTransaction> invoiceTransactionTable;
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Autowired
    public InvoiceTransactionsRepository(
        @Value("${invoices.ddb.name}") String invoicesDdbName,
        DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient
    ) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.invoiceTransactionTable = 
            this.dynamoDbEnhancedAsyncClient.table(invoicesDdbName, 
                TableSchema.fromBean(InvoiceTransaction.class));
    }

    public CompletableFuture<Void> createInvoiceTransaction(
        String email, String invoiceNumber, String invoiceTransactionId, 
        String invoiceFileTransactionId, 
        InvoiceTransactionStatus invoiceTransactionStatus
    ) {
        long timestamp = Instant.now().toEpochMilli();
        long ttl = Instant.now().plusSeconds(300).getEpochSecond();

        InvoiceTransaction invoiceTransaction = new InvoiceTransaction();
        invoiceTransaction.setPk(PARTITION_KEY.concat(invoiceFileTransactionId));
        invoiceTransaction.setSk(invoiceTransactionId);
        invoiceTransaction.setTtl(ttl);
        invoiceTransaction.setCreatedAt(timestamp);
        invoiceTransaction.setCustomerEmail(email);
        invoiceTransaction.setInvoiceNumber(invoiceNumber);
        invoiceTransaction.setTransactionStatus(invoiceTransactionStatus);

        return invoiceTransactionTable.putItem(invoiceTransaction);
    }
}
