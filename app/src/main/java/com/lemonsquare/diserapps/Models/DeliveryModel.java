package com.lemonsquare.diserapps.Models;

public class DeliveryModel extends MaterialModel{

    private String invoiceDate;
    private String invoiceNum;


    public String getInvoiceDate() {
        return invoiceDate.trim();
    }


    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate.trim();
    }


    public String getInvoiceNum() {
        return invoiceNum.trim();
    }

    public void setInvoiceNum(String invoiceNum) {
        this.invoiceNum = invoiceNum.trim();
    }
}
