package com.BillardManagement.Service;

public interface InvoicePdfService {
    byte[] renderBillPdf(Integer billId);
}

