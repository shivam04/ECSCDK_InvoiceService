package com.shvmsnha.invoiceservice.invoices.repository;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.shvmsnha.invoiceservice.invoices.enums.InvoiceFileTransactionStatus;
import com.shvmsnha.invoiceservice.invoices.models.InvoiceFileTransaction;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

@XRayEnabled
@Repository
public class InvoicesFileTransactionsRepository {
    private static final String PARTITION_KEY = "#fileTransaction";
    private final DynamoDbAsyncTable<InvoiceFileTransaction> invoiceFileTransactionTable;
    private final DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Autowired
    public InvoicesFileTransactionsRepository(
        @Value("${invoices.ddb.name}") String invoicesDdbName,
        DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient
    ) {
        this.dynamoDbEnhancedAsyncClient = dynamoDbEnhancedAsyncClient;
        this.invoiceFileTransactionTable = 
            this.dynamoDbEnhancedAsyncClient.table(invoicesDdbName, 
                TableSchema.fromBean(InvoiceFileTransaction.class));
    }

    public CompletableFuture<Void> createInvoiceFileTransaction(
        String key, String requestId, int expiresIn
    ) {
        long timestamp = Instant.now().toEpochMilli();
        long ttl = Instant.now().plusSeconds(300).getEpochSecond();

        InvoiceFileTransaction invoiceFileTransaction = new InvoiceFileTransaction();
        invoiceFileTransaction.setPk(PARTITION_KEY);
        invoiceFileTransaction.setSk(key);
        invoiceFileTransaction.setTtl(ttl);
        invoiceFileTransaction.setRequestId(requestId);
        invoiceFileTransaction.setCreatedAt(timestamp);
        invoiceFileTransaction.setExpiresIn(expiresIn);
        invoiceFileTransaction.setFileTransactionStatus(InvoiceFileTransactionStatus.GENERATED);

        return invoiceFileTransactionTable.putItem(invoiceFileTransaction);
    }

    public CompletableFuture<InvoiceFileTransaction> updateInvoiceFileTransaction(
        String transactionId, InvoiceFileTransactionStatus invoiceFileTransactionStatus
    ) {

        InvoiceFileTransaction invoiceFileTransaction = new InvoiceFileTransaction();
        invoiceFileTransaction.setPk(PARTITION_KEY);
        invoiceFileTransaction.setSk(transactionId);
        invoiceFileTransaction.setFileTransactionStatus(invoiceFileTransactionStatus);

        return invoiceFileTransactionTable.updateItem(
            UpdateItemEnhancedRequest.builder(InvoiceFileTransaction.class)
            .item(invoiceFileTransaction)
            .ignoreNulls(true)
            .conditionExpression(
                Expression.builder()
                .expression("attribute_exists(sk)")
                .build()
            )
            .build()
        );
    }

    public CompletableFuture<InvoiceFileTransaction> getInvoiceFileTransaction(
        String transactionId
    ) {
        return invoiceFileTransactionTable.getItem(
            GetItemEnhancedRequest.builder()
            .key(Key.builder()
                .partitionValue(PARTITION_KEY)
                .sortValue(transactionId)
                .build()
                )
            .build()
        );
    }
}
