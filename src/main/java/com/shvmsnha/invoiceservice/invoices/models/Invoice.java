package com.shvmsnha.invoiceservice.invoices.models;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Invoice {
    private String pk; // #invoice_customerEmail
    private String sk; // invoiceNumer
    private Float totalValue;
    private List<InvoiceProduct> products;
    private String invoiceTransactionId;
    private String fileTransactionId;
    private Long ttl;
    private Long createdAt;

    @DynamoDbPartitionKey
    public String getPk() {
        return pk;
    }

    /**
     * @param pk the pk to set
     */
    public void setPk(String pk) {
        this.pk = pk;
    }

    @DynamoDbSortKey
    public String getSk() {
        return sk;
    }

    /**
     * @param sk the sk to set
     */
    public void setSk(String sk) {
        this.sk = sk;
    }

    /**
     * @return Float return the totalValue
     */
    public Float getTotalValue() {
        return totalValue;
    }

    /**
     * @param totalValue the totalValue to set
     */
    public void setTotalValue(Float totalValue) {
        this.totalValue = totalValue;
    }

    /**
     * @return List<InvoiceProduct> return the products
     */
    public List<InvoiceProduct> getProducts() {
        return products;
    }

    /**
     * @param products the products to set
     */
    public void setProducts(List<InvoiceProduct> products) {
        this.products = products;
    }

    /**
     * @return String return the invoiuceTransactionId
     */
    public String getInvoiceTransactionId() {
        return invoiceTransactionId;
    }

    /**
     * @param invoiuceTransactionId the invoiuceTransactionId to set
     */
    public void setInvoiceTransactionId(String invoiceTransactionId) {
        this.invoiceTransactionId = invoiceTransactionId;
    }

    /**
     * @return String return the fileTransactionId
     */
    public String getFileTransactionId() {
        return fileTransactionId;
    }

    /**
     * @param fileTransactionId the fileTransactionId to set
     */
    public void setFileTransactionId(String fileTransactionId) {
        this.fileTransactionId = fileTransactionId;
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

}
