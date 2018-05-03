/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting;

import com.POSPrinting.Interfaces.clsAdvReceiptGenerationFormat;
import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSPrinting.Jasper.AdvReceipt.clsJasperFormat1ForAdvReceipt;
import com.POSPrinting.Text.AdvReceipt.clsTextFormat1ForAdvReceipt;
import com.POSPrinting.Text.AdvReceipt.clsTextFormat2ForAdvReceipt;

/**
 *
 * @author Ajim
 * @date Aug 28, 2017
 */
public class clsAdvOrderReceiptGenerator
{

    private clsAdvReceiptGenerationFormat objAdvReceiptGenerationFormat;

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
    public void funGenerateAdvReceipt(String advBookNo, String receiptNo, String posCode, String Reprint, String custName, String orderDate, String waiterName, String formName)
    {

        switch (clsGlobalVarClass.gBillFormatType)
        {
            case "Text 1":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 2":
                objAdvReceiptGenerationFormat = new clsTextFormat2ForAdvReceipt();
                break;

            case "Text 3":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 4":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 5":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 6":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 7":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 8":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 9":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 10":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 11":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 12":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 13":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 14":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 15":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 16":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 17":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Text 18":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Jasper 1":
                objAdvReceiptGenerationFormat = new clsJasperFormat1ForAdvReceipt();
                break;

            case "Jasper 2":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Jasper 3":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Stationery 1":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Stationery 2":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;

            case "Stock Transfer Note":
                objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
                break;
        }

        if (objAdvReceiptGenerationFormat == null)
        {
            objAdvReceiptGenerationFormat = new clsTextFormat1ForAdvReceipt();
        }

        objAdvReceiptGenerationFormat.funGenerateAdvReceipt(advBookNo, receiptNo, posCode, Reprint, custName, orderDate, waiterName, formName);
    }
}
