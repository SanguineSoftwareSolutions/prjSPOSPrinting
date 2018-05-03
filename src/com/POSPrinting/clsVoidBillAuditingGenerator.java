/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.POSPrinting;

import com.POSGlobal.controller.clsGlobalVarClass;
import com.POSPrinting.Interfaces.clsVoidBillForAuditGenerationFormat;
import com.POSPrinting.Text.Bill.Audit.clsTextFormat5ForVoidBillAudit;

/**
 *
 * @author Ajim
 * @date Sep 5, 2017
 */
public class clsVoidBillAuditingGenerator
{

    private clsVoidBillForAuditGenerationFormat objVoidBillForAuditGenerationFormat;

    /**
     *
     * @param billno
     * @param reprint
     * @param formName
     * @param transType
     * @param billDate
     * @param posCode
     * @param viewORPrint
     */
    public void funGenerateBill(String billno, String reprint, String formName, String transType, String billDate, String posCode, String viewORPrint)
    {

        switch (clsGlobalVarClass.gBillFormatType)
        {
            case "Text 1":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 2":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 3":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 4":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 5":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 6":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 7":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 8":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 9":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 10":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 11":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 12":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 13":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 14":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 15":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 16":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 17":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Text 18":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Jasper 1":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Jasper 2":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Jasper 3":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Stationery 1":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Stationery 2":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;

            case "Stock Transfer Note":
                objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
                break;
        }

        if (objVoidBillForAuditGenerationFormat == null)
        {
            objVoidBillForAuditGenerationFormat = new clsTextFormat5ForVoidBillAudit();
        }

        objVoidBillForAuditGenerationFormat.funGenerateBill(billno, reprint, formName, transType, billDate, posCode, viewORPrint);
    }
}
