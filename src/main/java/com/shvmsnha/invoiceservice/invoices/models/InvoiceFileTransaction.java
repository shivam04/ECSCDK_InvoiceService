package com.shvmsnha.invoiceservice.invoices.models;

import com.shvmsnha.invoiceservice.invoices.enums.InvoiceFileTransactionStatus;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class InvoiceFileTransaction {
    private String pk; // #fileTransaction
    private String sk; //  file transaction id
    private String requestId;
    private Long createdAt;
    private Long ttl;
    private Integer expiresIn;
    private InvoiceFileTransactionStatus fileTransactionStatus;

    @DynamoDbPartitionKey
    public String getPk() {
        return this.pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return this.sk;
    }


    public void setPk(String pk) {
        this.pk = pk;
    }
    
    public void setSk(String sk) {
        this.sk = sk;
    }

    /**
     * @return String return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return Long return the createdAt
     */
    public Long getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return Long return the ttl
     */
    public Long getTtl() {
        return ttl;
    }

    /**
     * @param ttl the ttl to set
     */
    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }

    /**
     * @return Integer return the expiresIn
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param expiresIn the expiresIn to set
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * @return InvoiceFileTransactionStatus return the fileTransactionStatus
     */
    public InvoiceFileTransactionStatus getFileTransactionStatus() {
        return fileTransactionStatus;
    }

    /**
     * @param fileTransactionStatus the fileTransactionStatus to set
     */
    public void setFileTransactionStatus(InvoiceFileTransactionStatus fileTransactionStatus) {
        this.fileTransactionStatus = fileTransactionStatus;
    }

}
