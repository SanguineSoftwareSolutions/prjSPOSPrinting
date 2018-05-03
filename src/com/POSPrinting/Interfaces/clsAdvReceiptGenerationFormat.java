/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting.Interfaces;

/**
 *
 * @author Ajim
 * @date Aug 28, 2017
 */
public interface clsAdvReceiptGenerationFormat
{

    /**
     *
     * @param advBookNo
     * @param receiptNo
     * @param posCode
     * @param Reprint
     * @param custName
     * @param orderDate
     * @param waiterName
     * @param formName
     */
    public void funGenerateAdvReceipt(String advBookNo, String receiptNo, String posCode, String Reprint, String custName, String orderDate, String waiterName, String formName);
}
